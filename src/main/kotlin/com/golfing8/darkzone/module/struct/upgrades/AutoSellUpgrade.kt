package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.NMS
import com.golfing8.kcommon.config.generator.Conf
import org.bukkit.Bukkit

/**
 * Automatically sells backpack contents for a player.
 */
class AutoSellUpgrade(type: UpgradeType) : Upgrade(type) {
    @Conf
    private var sellFrequencies = HashMap<Int, Int>().apply {
        put(1, 300)
        put(2, 240)
        put(3, 180)
        put(4, 120)
        put(5, 60)
    }

    override fun enable() {
        super.enable()

        DarkzoneModule.addTask(Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                val playerData = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
                val playerLevel = getLevel(player)
                val frequency = sellFrequencies[playerLevel] ?: continue
                if ((NMS.getTheNMS().currentTick % (frequency * 20L)) != 0L)
                    continue

                playerData.sellBackpackContents()
            }
        }).startTimer(0L, 20L)
    }
}