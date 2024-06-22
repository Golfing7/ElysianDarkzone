package com.golfing8.darkzone.module.sub

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.event.CustomMobKillEvent
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.module.SubModule
import com.golfing8.kcommon.struct.drop.DropTable
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.Location
import org.bukkit.NamespacedKey
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
    var entities = HashMap<String, EntityDefinition>()
        private set

    override fun getPrefix(): String = ""

    /**
     * Gets the custom type of entity given that it is a custom entity.
     */
    fun getCustomEntityType(entity: Entity): String? {
        if (!entity.persistentDataContainer.has(customEntityKey, PersistentDataType.STRING))
            return null

        return entity.persistentDataContainer.get(customEntityKey, PersistentDataType.STRING)
    }

    /**
     * Checks if the given entity is a custom entity.
     *
     * @param entity the entity to check
     * @return true if it is a custom entity
     */
    fun isCustomEntity(entity: Entity): Boolean {
        return entity.persistentDataContainer.has(customEntityKey, PersistentDataType.STRING)
    }

    fun isMob(entity: Entity): Boolean {
        return isCustomEntity(entity) && !isBoss(entity)
    }

    fun isBoss(entity: Entity): Boolean {
        return entity.persistentDataContainer.has(bossEntityKey, PersistentDataType.STRING)
    }

    /**
     * Tries to naturally spawn an entity at the given location.
     *
     * @param loc the location to spawn the entity near to.
     * @return the spawned entity, or null if the entity cannot be spawned there.
     */
    fun trySpawnEntity(loc: Location, definition: EntityDefinition): LivingEntity? {
        val createdEntity = definition.trySpawnNaturallyAt(loc) ?: return null

        createdEntity.persistentDataContainer.set(customEntityKey, PersistentDataType.STRING, definition._key)
        return createdEntity
    }

    @EventHandler
    fun onEntityDie(event: EntityDeathEvent) {
        if (!isCustomEntity(event.entity))
            return

        val entityType = entities[event.entity.persistentDataContainer.get(customEntityKey, PersistentDataType.STRING)] ?: return
        val deathEvent = CustomMobKillEvent(entityType, event.entity)
        deathEvent.callEvent()

        event.drops.clear()
        entityType.dropTable!!.giveOrDropAt(deathEvent.dropContext, event.entity.location)
    }
}