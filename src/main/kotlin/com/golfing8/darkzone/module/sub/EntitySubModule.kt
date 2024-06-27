package com.golfing8.darkzone.module.sub

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.event.CustomBossKillEvent
import com.golfing8.darkzone.module.event.CustomMobKillEvent
import com.golfing8.darkzone.module.struct.DarkzoneBoss
import com.golfing8.darkzone.module.struct.DarkzoneBossWrapper
import com.golfing8.darkzone.module.struct.DarkzoneMob
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.module.SubModule
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataType

val customEntityKey = NamespacedKey(ElysianDarkzone.INSTANCE, "elysian_darkzone_entity")
val bossEntityKey = NamespacedKey(ElysianDarkzone.INSTANCE, "elysian_darkzone_boss")

/**
 * Sub module for handling all things related to entities.
 */
object EntitySubModule : SubModule<DarkzoneModule>() {
    @Conf(config = "entities")
    var entities = HashMap<String, DarkzoneMob>()
        private set
    @Conf(config = "entities")
    var bosses = HashMap<String, DarkzoneBoss>()
        private set

    private val livingBosses = mutableMapOf<LivingEntity, DarkzoneBossWrapper>()
    fun getLivingBosses(): Map<LivingEntity, DarkzoneBossWrapper> {
        livingBosses.entries.removeIf {
            !it.value.entity.isValid
        }
        return livingBosses
    }

    override fun getPrefix(): String = ""

    fun getCustomEntityType(entity: Entity): String? {
        if (!entity.persistentDataContainer.has(customEntityKey, PersistentDataType.STRING))
            return null

        return entity.persistentDataContainer.get(customEntityKey, PersistentDataType.STRING)
    }

    fun getCustomBoss(entity: Entity): DarkzoneBossWrapper? {
        val bosses = getLivingBosses()
        if (!bosses.containsKey(entity))
            return null

        return bosses[entity]
    }

    fun isMob(entity: Entity): Boolean {
        return entity.persistentDataContainer.has(customEntityKey, PersistentDataType.STRING)
    }

    fun isBoss(entity: Entity): Boolean {
        return entity.persistentDataContainer.has(bossEntityKey, PersistentDataType.STRING)
    }

    /**
     * Spawns a boss at the given location.
     *
     * @param location the location of the spawned boss.
     * @param boss the type of boss to spawn
     * @return the spawned boss
     */
    fun spawnBoss(location: Location, boss: DarkzoneBoss): DarkzoneBossWrapper {
        val spawned = boss.entityDefinition.spawnEntity(location)
        if (boss.overrideAI)
            (spawned as CraftEntity).handle.fromMobSpawner = false
        val wrapper = DarkzoneBossWrapper(boss, spawned as LivingEntity)
        livingBosses[spawned] = wrapper
        return wrapper
    }

    /**
     * Tries to naturally spawn an entity at the given location.
     *
     * @param loc the location to spawn the entity near to.
     * @return the spawned entity, or null if the entity cannot be spawned there.
     */
    fun trySpawnEntity(loc: Location, definition: DarkzoneMob): LivingEntity? {
        val createdEntity = definition.entityDefinition.trySpawnNaturallyAt(loc) ?: return null

        createdEntity.persistentDataContainer.set(customEntityKey, PersistentDataType.STRING, definition._key)
        return createdEntity
    }

    fun adaptEntity(livingEntity: LivingEntity, definition: DarkzoneMob) {
        definition.entityDefinition.applyToEntity(livingEntity)
        livingEntity.persistentDataContainer.set(customEntityKey, PersistentDataType.STRING, definition._key)
    }

    @EventHandler
    fun onBossDie(event: EntityDeathEvent) {
        if (!isBoss(event.entity))
            return

        val bossWrapper = getLivingBosses()[event.entity] ?: return

        event.drops.clear()
        bossWrapper.onBossDeath(event.entity.killer)
    }

    @EventHandler
    fun onMobDie(event: EntityDeathEvent) {
        if (!isMob(event.entity))
            return

        event.drops.clear()
        val entityType = entities[event.entity.persistentDataContainer.get(customEntityKey, PersistentDataType.STRING)] ?: return
        entityType.handleDeath(event.entity)
    }
}