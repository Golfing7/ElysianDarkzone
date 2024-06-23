package com.golfing8.darkzone.module.event

import com.golfing8.darkzone.module.struct.DarkzoneBossWrapper
import com.golfing8.darkzone.module.struct.DarkzoneMob
import com.golfing8.kcommon.struct.drop.DropContext
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/**
 * Called when a custom boss is killed
 */
class CustomBossKillEvent(val mob: DarkzoneBossWrapper, entity: LivingEntity) : EntityEvent(entity) {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    var dropContext: DropContext = DropContext.DEFAULT

    override fun getEntity(): LivingEntity {
        return super.getEntity() as LivingEntity
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}