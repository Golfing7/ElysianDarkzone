package com.golfing8.darkzone.module.struct

import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.kcommon.data.SenderSerializable

/**
 * Contains a player's data relating to the darkzone
 */
class PlayerDarkzoneData : SenderSerializable() {
    /** How much of the darkzone currency the player has. */
    var dzCurrency: Long = 0
    /** The levels of each upgrade a player has unlocked */
    var upgradeLevels: HashMap<UpgradeType, Int> = HashMap()
}