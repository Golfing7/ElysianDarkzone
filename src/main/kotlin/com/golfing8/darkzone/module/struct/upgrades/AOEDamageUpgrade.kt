package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.Range
import com.golfing8.kcommon.struct.map.RangeMap
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * Gives you AOE damage to nearby entities
 */
class AOEDamageUpgrade(type: UpgradeType) : Upgrade(type) {
    @Conf
    private var damageRatio = RangeMap.builder<Double>()
        .put(1.0, 0.25)
        .put(2.0, 0.5)
        .put(3.0, 0.75)
        .put(4.0, 1.0)
        .put(5.0, 1.25)
        .build<Double>()

    @Conf
    private var range = RangeMap.builder<Double>()
        .put(Range(1.0, 5.0), 4.0)
        .build<Double>()

    @Conf
    private var applyToEverything = false

    private var eventEnabled = true
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (!eventEnabled)
            return

        val attacker = event.damager as? Player ?: return
        val level = getLevel(attacker)
        if (level <= 0)
            return

        if (!DarkzoneModule.inDarkzoneArea(attacker.location))
            return

        val range = this.range[level.toDouble()]!!
        val damageRatio = this.damageRatio[level.toDouble()]!!
        val attackDamage = event.damage * damageRatio

        // Find all nearby entities and damage them!
        for (entity in event.entity.getNearbyEntities(range, range, range)) {
            if (entity !is LivingEntity || (!applyToEverything && !EntitySubModule.isMob(entity) && !EntitySubModule.isBoss(entity)))
                continue

            eventEnabled = false
            entity.damage(attackDamage, attacker)
            eventEnabled = true
        }
    }
}