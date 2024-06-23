package com.golfing8.darkzone.module.struct

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

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
     * Should be called when the boss has died and rewards should be distributed.
     */
    fun onBossDeath(killer: Player? = null) {
        val totalHealth = bossData.entityDefinition.spawnHealth

        for (entry in totalPlayerDamage) {
            val damageRatio = entry.value / totalHealth
            val totalXP = damageRatio * bossData.xpPerKill

            val playerData = DarkzoneModule.getOrCreate(entry.key.uniqueId, PlayerDarkzoneData::class.java)
            playerData.grantXP(totalXP.toLong())
        }
    }
}