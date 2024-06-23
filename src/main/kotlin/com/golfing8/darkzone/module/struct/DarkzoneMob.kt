package com.golfing8.darkzone.module.struct

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.event.CustomMobKillEvent
import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.entity.EntityDefinition
import com.golfing8.kcommon.util.MS
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.min

/**
 * Represents a darkzone mob.
 */
class DarkzoneMob : CASerializable {
    lateinit var _key: String
    lateinit var entityDefinition: EntityDefinition
        private set
    var xpPerKill = 0

    /**
     * Handles a pre-filtered event for entity damage.
     * It is guaranteed that the entity is this darkzone mob.
     *
     * @param event the damage event.
     */
    fun handleMobDamage(event: EntityDamageEvent) {
        val livingEntity = event.entity as? LivingEntity ?: return
        event.entity.customName = MS.parseSingle(entityDefinition.name,
            "HEALTH", StringUtil.parseCommas(min(0.0, livingEntity.health - event.finalDamage)),
            "MAX_HEALTH", StringUtil.parseCommas(entityDefinition.maxHealth)
        )
    }

    fun handleDeath(entity: LivingEntity) {
        val deathEvent = CustomMobKillEvent(this, entity)
        deathEvent.callEvent()

        if (entity.killer == null)
            return

        DarkzoneModule.generateDropsToBackpack(entity.killer, this.entityDefinition.dropTable!!, deathEvent.dropContext)
        val playerData = DarkzoneModule.getOrCreate(entity.killer.uniqueId, PlayerDarkzoneData::class.java)
        playerData.grantXP(xpPerKill.toLong())
    }
}