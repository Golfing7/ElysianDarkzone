package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.DarkzoneModule
import org.bukkit.Bukkit

/**
 * Automatically sells backpack contents for a player.
 */
class AutoSellUpgrade(type: UpgradeType) : Upgrade(type) {
    override fun enable() {
        super.enable()

        DarkzoneModule.addTask {
            for (player in Bukkit.getOnlinePlayers()) {
                //
            }
        }.startTimer(0L, 20L)
    }
}