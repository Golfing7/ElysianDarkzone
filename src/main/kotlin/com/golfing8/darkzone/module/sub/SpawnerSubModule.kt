package com.golfing8.darkzone.module.sub

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.CustomSpawner
import com.golfing8.kcommon.NMS
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.module.SubModule
import com.golfing8.kcommon.nms.event.PreSpawnSpawnerEvent
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.random.Random

const val PER_ENTITY_SPAWN_ATTEMPTS = 10

/**
 * Handles spawning for the darkzone
 */
object SpawnerSubModule : SubModule<DarkzoneModule>() {
    private val customSpawnerKey = NamespacedKey(ElysianDarkzone.INSTANCE, "elysian_darkzone_spawner")

    @Conf(config = "spawners")
    lateinit var customSpawners: HashMap<String, CustomSpawner> private set

    // Since we use our own config, this is safe to do.
    override fun getPrefix(): String = ""

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpawn(event: PreSpawnSpawnerEvent) {
        val creatureSpawner = event.block.state as? CreatureSpawner ?: return

        if (!creatureSpawner.persistentDataContainer.has(customSpawnerKey, PersistentDataType.STRING))
            return

        // Always cancel custom spawns as we handle them manually.
        event.isCancelled = true

        // Get the custom spawner
        val key = creatureSpawner.persistentDataContainer.get(customSpawnerKey, PersistentDataType.STRING)
        val customSpawner = customSpawners[key] ?: return

        // Apply next spawn tick delay
        module.addTask {
            creatureSpawner.delay = customSpawner.ticksPerSpawn.randomI
        }.start()

        val entityData = EntitySubModule.entities[customSpawner.entitySpawnType] ?: return

        // Count all nearby entities
        var nearbyEntities = event.block.location.world.getNearbyEntities(event.block.location, customSpawner.nearbyRange, customSpawner.nearbyRange, customSpawner.nearbyRange).count {
            EntitySubModule.isCustomEntity(it)
        }
        repeat(customSpawner.mobsPerSpawn) {
            if (nearbyEntities >= customSpawner.maxOfTypeNearby)
                return

            // Try spawning an entity at that location
            val spawnLocation = event.block.location.add(if (Random.nextBoolean()) 1.0 else -1.0, 0.0, if (Random.nextBoolean()) 1.0 else -1.0)
            for (i in 0 until PER_ENTITY_SPAWN_ATTEMPTS) {
                if (EntitySubModule.trySpawnEntity(spawnLocation, entityData) != null) {
                    nearbyEntities++
                    break
                }
            }
        }
    }
}