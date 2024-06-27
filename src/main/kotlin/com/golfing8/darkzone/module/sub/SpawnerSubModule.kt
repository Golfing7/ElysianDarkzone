package com.golfing8.darkzone.module.sub

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.DarkzoneSpawner
import com.golfing8.darkzone.module.struct.DarkzoneMob
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.module.SubModule
import com.golfing8.kcommon.struct.item.ItemStackBuilder
import com.golfing8.kcommon.struct.placeholder.Placeholder
import com.golfing8.shade.com.cryptomorin.xseries.XMaterial
import de.tr7zw.kcommon.nbtapi.NBT
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Handles spawning for the darkzone
 */
object SpawnerSubModule : SubModule<DarkzoneModule>() {
    private const val CUSTOM_SPAWNER_ITEM_KEY = "elysian_darkzone_spawner"
    private val customSpawnerKey = NamespacedKey(ElysianDarkzone.INSTANCE, "elysian_darkzone_spawner")

    @Conf(config = "spawners")
    lateinit var darkzoneSpawners: HashMap<String, DarkzoneSpawner> private set
    @Conf(config = "spawners")
    private var spawnerItemFormat = ItemStackBuilder()
        .material(XMaterial.SPAWNER)
        .name("&3&lDarkzone Spawner")
        .lore(
            "&7Place somewhere in the darkzone to spawn &e{ENTITY} &7entities!"
        )

    // Since we use our own config, this is safe to do.
    override fun getPrefix(): String = ""

    /**
     * Generates a spawner item for the given entity definition.
     *
     * @param definition the entity definition.
     * @return the generated item.
     */
    fun generateSpawnerItem(definition: DarkzoneMob): ItemStack {
        spawnerItemFormat.placeholders(Placeholder.curly("ENTITY", definition._key))
        val item = spawnerItemFormat.buildFromTemplate()
        NBT.modify(item) {
            it.setString(CUSTOM_SPAWNER_ITEM_KEY, definition._key)
        }
        return item
    }

    /**
     * Gets the entity definition from the given spawner item.
     *
     * @param item the item.
     * @return the entity definition.
     */
    fun getEntityDefinitionFromSpawnerItem(item: ItemStack?): DarkzoneMob? {
        if (item == null || !item.hasItemMeta())
            return null

        val readNbt = NBT.readNbt(item)
        if (!readNbt.hasTag(CUSTOM_SPAWNER_ITEM_KEY))
            return null

        return EntitySubModule.entities[readNbt.getString(CUSTOM_SPAWNER_ITEM_KEY)]
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onSpawnerPlace(event: BlockPlaceEvent) {
        val placedItem = event.itemInHand
        val entityDefinition = getEntityDefinitionFromSpawnerItem(placedItem) ?: return

        module.addTask {
            val blockAtLocation = event.block.location.block
            val blockMeta = blockAtLocation.state as? CreatureSpawner ?: return@addTask
            blockMeta.spawnedType = entityDefinition.entityDefinition.type.entityType
            blockMeta.persistentDataContainer.set(customSpawnerKey, PersistentDataType.STRING, entityDefinition._key)
            blockMeta.update()
        }.startLater(1L)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpawn(event: SpawnerSpawnEvent) {
        if (!event.spawner.persistentDataContainer.has(customSpawnerKey, PersistentDataType.STRING))
            return

        // Get the custom spawner
        val key = event.spawner.persistentDataContainer.get(customSpawnerKey, PersistentDataType.STRING)
        val customSpawner = darkzoneSpawners[key] ?: return

        // Apply next spawn tick delay
        module.addTask {
            event.spawner.delay = customSpawner.ticksPerSpawn.randomI
        }.start()

        val entityData = EntitySubModule.entities[customSpawner._key] ?: return

        // Count all nearby entities
        val nearbyEntities = event.spawner.location.world.getNearbyEntities(event.spawner.location, customSpawner.nearbyRange, customSpawner.nearbyRange, customSpawner.nearbyRange).count {
            EntitySubModule.isMob(it)
        }

        if (nearbyEntities >= customSpawner.maxOfTypeNearby) {
            event.isCancelled = true
            return
        }

        // Try spawning an entity at that location
        EntitySubModule.adaptEntity(event.entity as LivingEntity, entityData)
        if (entityData.overrideAI)
            (event.entity as CraftEntity).handle.fromMobSpawner = false
    }
}