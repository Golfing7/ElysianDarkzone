package com.golfing8.darkzone.module.data

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.DarkzoneLevel
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.kcommon.data.SenderSerializable

/**
 * Contains a player's data relating to the darkzone
 */
class PlayerDarkzoneData : SenderSerializable() {
    /** How much of the darkzone currency the player has. */
    var dzCurrency: Long = 0
    /** How much darkzone XP the player has */
    var darkzoneXP: Long = 0
    /** The levels of each upgrade a player has unlocked */
    var upgradeLevels: HashMap<UpgradeType, Int> = HashMap()

    /**
     * Naturally grants XP to the player
     *
     * @param totalXP the XP to grant.
     */
    fun grantXP(totalXP: Long) {
        val oldLevel = getLevel()
        darkzoneXP += totalXP
        val newLevel = getLevel()
        if (newLevel !== oldLevel && player != null) {
            DarkzoneModule.levelUpMsg.send(
                player,
                "OLD_LEVEL", oldLevel.displayName,
                "NEW_LEVEL", newLevel.displayName,
            )
        }
    }

    /**
     * Gets the darkzone the player is at.
     *
     * @return the level.
     */
    fun getLevel(): DarkzoneLevel {
        return DarkzoneModule.levelsByXP.floorEntry(darkzoneXP).value
    }
}