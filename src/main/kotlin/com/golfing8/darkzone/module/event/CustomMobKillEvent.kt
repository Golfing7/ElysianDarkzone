package com.golfing8.darkzone.module.event

import com.golfing8.kcommon.struct.drop.DropContext
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/**
 * Called when a custom entity is killed
 */
class CustomMobKillEvent(val entityType: EntityDefinition, entity: LivingEntity) : EntityEvent(entity) {
    companion object {
        private val handlerList = HandlerList()

        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    var dropContext = DropContext.DEFAULT

    override fun getEntity(): LivingEntity {
        return super.getEntity() as LivingEntity
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}