package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.Range
import com.golfing8.kcommon.struct.map.RangeMap
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class ReducedMobDamageUpgrade(type: UpgradeType) : Upgrade(type) {
    @Conf
    private var damageReductionMod = RangeMap.builder<Double>()
        .put(Range(1.0, 5.0), 0.8)
        .build<Double>()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onDamageEntity(event: EntityDamageByEntityEvent) {
        val attacked = event.entity as? Player ?: return
        val attacker = event.damager as? LivingEntity ?: return

        if (!EntitySubModule.isMob(attacker))
            return

        val level = getLevel(attacked)
        if (level <= 0)
            return

        val reduction = damageReductionMod[level.toDouble()]!!.b
        event.damage *= reduction
    }
}