package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.config.generator.ConfigClass
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.TreeMap

/**
 * Represents an abstract upgrade
 */
abstract class Upgrade(val type: UpgradeType) : Listener, ConfigClass() {
    @Conf
    var upgradeCosts = TreeMap<Int, Int>().apply {
        put(1, 100)
        put(2, 250)
        put(3, 500)
        put(4, 1000)
        put(5, 2500)
    }
    @Conf
    var levelRequired = 0
    @Conf
    lateinit var displayName: String

    open fun enable() {
        DarkzoneModule.addSubListener(this)

        isRequireAnnotation = true
        initConfig()
        val config = DarkzoneModule.getConfig("upgrades")
        val changed = loadValues(config.getConfigurationSection(type.name) ?: config.createSection(type.name))
        if (changed) {
            config.save()
        }
    }

    open fun disable() {}

    /**
     * Gets the level of this upgrade the player has unlocked.
     *
     * @param player the player.
     * @return the level of the upgrade they have unlocked.
     */
    fun getLevel(player: Player): Int {
        val playerData = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        return playerData.upgradeLevels[type] ?: 0
    }
}