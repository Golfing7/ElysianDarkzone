package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.Range
import com.golfing8.kcommon.struct.map.RangeMap
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * An abstract class for handling increased damage on entities.
 */
abstract class IncreasedDamageUpgrade(type: UpgradeType) : Upgrade(type) {
    @Conf
    private var damageIncrease = RangeMap.builder<Double>()
        .put(Range(1.0, 5.0), 1.25)
        .build<Double>()

    override fun enable() {
        super.enable()

        Bukkit.getServer().pluginManager.registerEvent(EntityDamageByEntityEvent::class.java, this, EventPriority.HIGH, this::handleEntityDamage, ElysianDarkzone.INSTANCE, true)
    }

    private fun handleEntityDamage(listener: Listener, rawEvent: Event) {
        val event = rawEvent as EntityDamageByEntityEvent

        val livingEntity = event.entity as? LivingEntity ?: return
        val attacker = event.damager as? Player ?: return
        val level = getLevel(attacker)
        if (level <= 0)
            return

        if (!worksOn(livingEntity))
            return

        event.damage *= damageIncrease.get(level.toDouble())!!.b
    }

    abstract fun worksOn(entity: LivingEntity): Boolean
}