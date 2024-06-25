package com.golfing8.darkzone.module

import com.golfing8.darkzone.module.cmd.DarkzoneCMD
import com.golfing8.darkzone.module.cmd.currency.CurrencyCMD
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.struct.DarkzoneLevel
import com.golfing8.darkzone.module.struct.CurrencyContainer
import com.golfing8.darkzone.module.struct.DarkzoneItem
import com.golfing8.darkzone.module.struct.DarkzoneRegion
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.SpawnerSubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.config.lang.LangConf
import com.golfing8.kcommon.config.lang.Message
import com.golfing8.kcommon.data.DataManagerContainer
import com.golfing8.kcommon.hook.placeholderapi.KPlaceholderDefinition
import com.golfing8.kcommon.module.Module
import com.golfing8.kcommon.module.ModuleInfo
import com.golfing8.kcommon.struct.drop.CommandDrop
import com.golfing8.kcommon.struct.drop.DropContext
import com.golfing8.kcommon.struct.drop.DropTable
import com.golfing8.kcommon.struct.drop.ItemDrop
import com.golfing8.kcommon.struct.time.Schedule
import com.golfing8.kcommon.struct.time.ScheduleTask
import com.golfing8.kcommon.struct.time.Timestamp
import com.golfing8.kcommon.util.PlayerUtil
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.util.Vector
import java.util.TreeMap
import kotlin.contracts.contract

@ModuleInfo(
    name = "darkzone"
)
object DarkzoneModule : Module(), DataManagerContainer {
    @Conf
    lateinit var regions: HashMap<String, DarkzoneRegion>
        private set

    /** Levels mapped from their required XP to the level */
    @Conf
    lateinit var levels: HashMap<String, DarkzoneLevel>
    lateinit var levelsByXP: TreeMap<Long, DarkzoneLevel>
        private set
    @Conf
    lateinit var levelsByLevel: HashMap<Int, DarkzoneLevel>
        private set

    @Conf
    lateinit var itemDefinitions: HashMap<String, DarkzoneItem>

    @Conf
    private var keepRegionChunksLoaded = true

    @Conf
    private var bossSpawnSchedule = Schedule(listOf(
        Timestamp.ofIntraDay(0, 0, 0),
        Timestamp.ofIntraDay(3, 0, 0),
        Timestamp.ofIntraDay(6, 0, 0),
        Timestamp.ofIntraDay(9, 0, 0),
        Timestamp.ofIntraDay(12, 0, 0),
        Timestamp.ofIntraDay(15, 0, 0),
        Timestamp.ofIntraDay(18, 0, 0),
        Timestamp.ofIntraDay(21, 0, 0),
    ))

    @Conf
    var backpackRightClickTakeAmount = 64
        private set

    @Conf
    var darkzoneCommandName = "darkzone"
        private set

    @Conf
    var backpackCommandName = "voidstorage"
        private set

    @Conf
    var currencyCommandName = "dracma"
        private set

    @LangConf
    var backpackSoldMsg = Message(listOf(
        "&aSold your &1Void Storage &acontents:",
        "  &a+\${MONEY}",
        "  &1+{DZ_CURRENCY} Dracma"
    ))
        private set

    @LangConf
    var rareDropMsg = Message("&a&lRARE DROP! {DROP}")
        private set

    @LangConf
    var upgradeLevelTooLowMsg = Message("&cYour level is too low to purchase that upgrade! It requires level &e{LEVEL}&c!")
        private set
    @LangConf
    var cantAffordUpgradeMsg = Message("&cYou can't afford this upgrade. It costs &e{COST}&c!")
        private set
    @LangConf
    var boughtUpgradeMsg = Message("&aUpgraded &e{UPGRADE} &afor &e{COST} &adracma!")
        private set

    @LangConf
    var levelUpMsg = Message("&aLeveled up from {OLD_LEVEL} &ato {NEW_LEVEL}&a!")
        private set
    @LangConf
    private var dropsLostMsg = Message("&c{AMOUNT_LOST} drops were lost as they couldn't be fit into your &1&lVoid Storage&c.")

    @LangConf
    private var bossesWillSpawnMsg = Message("&aBosses will spawn in the darkzone in &e{TIME}&a!")
    @LangConf
    private var bossesSpawnedMsg = Message("&aBosses have spawned in the darkzone!")

    @LangConf
    private var cantMoveIntoRegionMsg = Message("&cYou can't enter that region until level &e{LEVEL}&c!")

    override fun onEnable() {
        addDataManager("darkzone-data", PlayerDarkzoneData::class.java)

        // Make sure the configs properly exist.
        getConfig("entities")
        getConfig("spawners")

        addSubModule(EntitySubModule)
        addSubModule(SpawnerSubModule)

        addCommand(DarkzoneCMD(darkzoneCommandName))
        addCommand(CurrencyCMD(currencyCommandName))

        UpgradeType.startup()

        // Initialize the levels
        levelsByXP = TreeMap()
        levelsByLevel = HashMap()
        levels.forEach {
            levelsByXP[it.value.xpRequired] = it.value
        }

        var level = 1
        levelsByXP.forEach {
            it.value.level = level++
            levelsByLevel[it.value.level] = it.value
        }

        // Startup the boss spawning task
        val scheduleTask = ScheduleTask(bossSpawnSchedule) { _ ->
            for (region in regions.values) {
                region.spawnBoss()
            }
            for (player in Bukkit.getOnlinePlayers()) {
                bossesSpawnedMsg.send(player)
            }
        }
        scheduleTask.setAnticipateTask {
            for (player in Bukkit.getOnlinePlayers()) {
                bossesWillSpawnMsg.send(player, "TIME", it.toString())
            }
        }

        scheduleTask.start()
        addTask(scheduleTask)

        // Register placeholders
        addPlaceholder(KPlaceholderDefinition("level", "Information about your level")) { p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            data.getLevel().displayName
        }
        addPlaceholder(KPlaceholderDefinition("level_number", "Information about your level")) { p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            data.getLevel().level.toString()
        }
        addPlaceholder(KPlaceholderDefinition("currency", "The amount of the darkzone currency a player has")) {p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            StringUtil.parseCommas(data.dzCurrency.toString())
        }
        addPlaceholder(KPlaceholderDefinition("xp", "The amount of XP you have")) { p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            StringUtil.parseCommas(data.darkzoneXP)
        }
        addPlaceholder(KPlaceholderDefinition("backpack_capacity", "Capacity of your backpack")) { p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            StringUtil.parseCommas(data.getBackpackSize())
        }
        addPlaceholder(KPlaceholderDefinition("backpack_contents", "Amount of items in your backpack")) { p, args ->
            val data = getOrCreate(p.uniqueId, PlayerDarkzoneData::class.java)
            StringUtil.parseCommas(data.getBackpackItemCount())
        }
    }

    override fun onDisable() {
        UpgradeType.shutdown()
    }

    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent) {
        if (this.keepRegionChunksLoaded && this.regions.values.any { it.region.isPositionWithin(Vector(event.chunk.x shl 4, 0, event.chunk.z shl 4)) })
            event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        // Ignore same-block movement.
        if (event.to.blockX == event.from.blockX &&
            event.to.blockY == event.from.blockY &&
            event.to.blockZ == event.from.blockZ)
            return

        val destinationRegion = getDarkzoneRegion(event.to) ?: return
        val playerData = getOrCreate(event.player.uniqueId, PlayerDarkzoneData::class.java)
        if (playerData.getLevel().level < destinationRegion.levelRequirement) {
            event.isCancelled = true
            cantMoveIntoRegionMsg.send(event.player, "LEVEL", destinationRegion.levelRequirement)
        }
    }

    /**
     * Checks if the given location is within a darkzone area.
     *
     * @param location the location to check.
     * @return if it's in a darkzone area.
     */
    fun inDarkzoneArea(location: Location): Boolean {
        return regions.values.find {
            it.region.isPositionWithin(location)
        } != null
    }

    /**
     * Gets the darkzone region from the given location.
     *
     * @param location the location to check.
     * @return the region the location is in.
     */
    fun getDarkzoneRegion(location: Location): DarkzoneRegion? {
        return regions.values.find {
            it.region.isPositionWithin(location)
        }
    }

    /**
     * Generates drops from the drop table and adds them to the backpack of the player.
     *
     * @param player the player.
     * @param table the drop table to use.
     */
    fun generateDropsToBackpack(player: Player, table: DropTable, context: DropContext = DropContext.DEFAULT) {
        val playerData = getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        val drops = table.generateDrops(context)
        drops.forEach {
            if (it is CommandDrop) {
                it.giveTo(player)
            } else if (it is ItemDrop) {
                var totalLost = 0
                for (entry in it.items) {
                    val itemLost = playerData.addItemToBackpack(it._key, entry.value.buildFromTemplate().amount)
                    if (itemLost > 0) {
                        totalLost += itemLost
                        PlayerUtil.givePlayerItemSafe(player, itemDefinitions[entry.key]!!.item.buildFromTemplate())
                    }
                }

                if (totalLost > 0) {
                    dropsLostMsg.send(player,
                        "AMOUNT_LOST", totalLost)
                }

                if (it.displayName != null) {
                    rareDropMsg.send(player, "DROP", it.displayName)
                }
            }
        }
    }
}