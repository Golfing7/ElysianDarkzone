package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.event.CustomMobKillEvent
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.Range
import com.golfing8.kcommon.struct.drop.DropContext
import com.golfing8.kcommon.struct.map.RangeMap
import org.bukkit.event.EventHandler

/**
 * Improves the loot drops of players.
 */
class MoreLootUpgrade(type: UpgradeType) : Upgrade(type) {
    @Conf
    private var dropChanceModifier = RangeMap.builder<Double>()
        .put(Range(1.0, 5.0), 2.0)
        .build<Double>()

    @EventHandler
    fun onKillEntity(event: CustomMobKillEvent) {
        val killer = event.entity.killer ?: return
        val level = getLevel(killer)
        if (level <= 0)
            return

        event.dropContext = DropContext(killer, dropChanceModifier[level.toDouble()]!!.b)
    }
}