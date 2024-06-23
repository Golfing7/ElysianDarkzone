package com.golfing8.darkzone.module

import com.golfing8.darkzone.module.cmd.DarkzoneCMD
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.struct.DarkzoneLevel
import com.golfing8.darkzone.module.struct.CurrencyContainer
import com.golfing8.darkzone.module.struct.region.DarkzoneRegion
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.SpawnerSubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.config.lang.LangConf
import com.golfing8.kcommon.config.lang.Message
import com.golfing8.kcommon.data.DataManagerContainer
import com.golfing8.kcommon.module.Module
import com.golfing8.kcommon.module.ModuleInfo
import com.golfing8.kcommon.struct.drop.CommandDrop
import com.golfing8.kcommon.struct.drop.DropContext
import com.golfing8.kcommon.struct.drop.DropTable
import com.golfing8.kcommon.struct.drop.ItemDrop
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.util.Vector
import java.util.TreeMap

@ModuleInfo(
    name = "darkzone"
)
object DarkzoneModule : Module(), DataManagerContainer {
    @Conf
    lateinit var regions: HashMap<String, DarkzoneRegion>
        private set

    /** Levels mapped from their required XP to the level */
    @Conf
    private lateinit var levels: HashMap<String, DarkzoneLevel>
    lateinit var levelsByXP: TreeMap<Long, DarkzoneLevel>
        private set

    @Conf
    lateinit var itemWorths: HashMap<String, CurrencyContainer>

    @Conf
    private var keepRegionChunksLoaded = true

    @LangConf
    var upgradeLevelTooLowMsg = Message("&cYour level is too low to purchase that upgrade! It requires level &e{LEVEL}&c!")
    @LangConf
    var cantAffordUpgradeMsg = Message("&cYou can't afford this upgrade. It costs &e{COST}&a!")
    @LangConf
    var boughtUpgradeMsg = Message("&aUpgraded &e{UPGRADE} &afor &e{COST} &adracma!")

    @LangConf
    var levelUpMsg = Message("&aLeveled up from {OLD_LEVEL} &ato {NEW_LEVEL}&a!")
    @LangConf
    var dropsLostMsg = Message("&c{AMOUNT_LOST} drops were lost as they couldn't be fit into your &1&lVoid Storage&c.")

    override fun onEnable() {
        addDataManager("darkzone-data", PlayerDarkzoneData::class.java)

        // Make sure the configs properly exist.
        getConfig("entities")
        getConfig("spawners")

        addSubModule(EntitySubModule)
        addSubModule(SpawnerSubModule)

        addCommand(DarkzoneCMD())

        UpgradeType.startup()

        levelsByXP = TreeMap()
        levels.forEach {
            levelsByXP[it.value.xpRequired] = it.value
        }

        var level = 1
        levelsByXP.forEach {
            it.value.level = level++
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
                    totalLost += playerData.addItemToBackpack(it._key, entry.value.buildFromTemplate().amount)
                }

                if (totalLost > 0) {
                    dropsLostMsg.send(player,
                        "AMOUNT_LOST", totalLost)
                }
            }
        }
    }
}