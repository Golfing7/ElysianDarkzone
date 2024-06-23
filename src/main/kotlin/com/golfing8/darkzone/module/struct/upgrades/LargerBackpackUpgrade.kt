package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.map.RangeMap
import org.bukkit.entity.Player

/**
 * Increases the total size of the backpack.
 */
class LargerBackpackUpgrade(type: UpgradeType) : Upgrade(type) {

    @Conf
    private var backpackSize = RangeMap.builder<Double>()
        .put(0.0, 500.0)
        .put(1.0, 750.0)
        .put(2.0, 1000.0)
        .put(3.0, 1250.0)
        .put(4.0, 1500.0)
        .put(5.0, 1750.0)
        .put(6.0, 2000.0)
        .put(7.0, 2250.0)
        .put(8.0, 2500.0)
        .build<Double>()

    /**
     * Gets the backpack size of the given player.
     * This will work for players even without any upgrades.
     *
     * @param player the player.
     * @return the amount of items the player can store in their backpack.
     */
    fun getBackpackSize(player: Player): Int {
        return getLevel(player)
    }

    fun getBackpackSize(level: Int): Int {
        return backpackSize[level.toDouble()]!!.b.toInt()
    }
}