package com.golfing8.darkzone.module.struct

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.event.CustomBossKillEvent
import com.golfing8.kcommon.util.MS
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.min

/**
 * Wraps a living darkzone boss
 */
class DarkzoneBossWrapper(val bossData: DarkzoneBoss, val entity: LivingEntity) {
    private val totalPlayerDamage = mutableMapOf<Player, Double>()

    fun trackPlayerDamage(player: Player, damage: Double) {
        totalPlayerDamage.compute(player) { _, v ->
            if (v == null) damage else v + damage
        }
    }

    /**
     * Handles a pre-filtered event for entity damage.
     * It is guaranteed that the entity is this darkzone mob.
     *
     * @param event the damage event.
     */
    fun handleMobDamage(event: EntityDamageEvent) {
        val livingEntity = event.entity as? LivingEntity ?: return
        event.entity.customName = MS.parseSingle(bossData.entityDefinition.name,
            "HEALTH", StringUtil.parseCommas(min(0.0, livingEntity.health - event.finalDamage)),
            "MAX_HEALTH", StringUtil.parseCommas(bossData.entityDefinition.maxHealth)
        )
    }

    /**
     * Should be called when the boss has died and rewards should be distributed.
     */
    fun onBossDeath(killer: Player? = null) {
        val deathEvent = CustomBossKillEvent(this)
        deathEvent.callEvent()

        val totalHealth = bossData.entityDefinition.spawnHealth

        for (entry in totalPlayerDamage) {
            val damageRatio = entry.value / totalHealth
            val totalXP = damageRatio * bossData.xpPerKill

            val playerData = DarkzoneModule.getOrCreate(entry.key.uniqueId, PlayerDarkzoneData::class.java)
            playerData.grantXP(totalXP.toLong())

            if (damageRatio * 100.0 > bossData.minimumDamagePercentageForDrops && entry.key.isOnline)
                DarkzoneModule.generateDropsToBackpack(entry.key, bossData.entityDefinition.dropTable!!)
        }
    }
}