package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.PlayerDarkzoneData
import com.golfing8.kcommon.config.generator.ConfigClass
import org.bukkit.entity.Player
import org.bukkit.event.Listener

/**
 * Represents an abstract upgrade
 */
abstract class Upgrade(val type: UpgradeType) : Listener, ConfigClass() {
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