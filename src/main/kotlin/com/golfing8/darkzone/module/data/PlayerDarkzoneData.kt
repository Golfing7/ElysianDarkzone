package com.golfing8.darkzone.module.data

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.CurrencyContainer
import com.golfing8.darkzone.module.struct.DarkzoneLevel
import com.golfing8.darkzone.module.struct.upgrades.LargerBackpackUpgrade
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.kcommon.KCommon
import com.golfing8.kcommon.data.SenderSerializable
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.Bukkit

/**
 * Contains a player's data relating to the darkzone
 */
class PlayerDarkzoneData : SenderSerializable() {
    /** How much of the darkzone currency the player has. */
    var dzCurrency: Long = 0
    /** How much darkzone XP the player has */
    var darkzoneXP: Long = 0
    /** The levels of each upgrade a player has unlocked */
    var upgradeLevels = HashMap<UpgradeType, Int>()
        private set
    /** The items the player has accumulated while in the darkzone */
    var backpackContents = HashMap<String, Int>()
        private set

    /**
     * Gets the total amount of items in the player's backpack.
     *
     * @return the amount of items in the player's backpack.
     */
    fun getBackpackItemCount(): Int {
        return backpackContents.values.sum()
    }

    /**
     * Gets the size of the player's backpack.
     *
     * @return the size of the backpack.
     */
    fun getBackpackSize(): Int {
        return (UpgradeType.LARGER_BACKPACK.get() as LargerBackpackUpgrade)
            .getBackpackSize(upgradeLevels[UpgradeType.LARGER_BACKPACK] ?: 0)
    }

    /**
     * Adds the item with the given key to the player's backpack.
     *
     * @param itemKey the item's key
     * @param amount the amount of item
     * @return the leftover items that couldn't be added
     */
    fun addItemToBackpack(itemKey: String, amount: Int): Int {
        val max = getBackpackSize()
        val current = getBackpackItemCount()
        val actualAdded = amount.coerceIn(0, max - current)
        backpackContents.compute(itemKey) { _, v ->
            if (v == null) actualAdded else v + actualAdded
        }
        return amount - actualAdded
    }

    /**
     * Sells the backpack contents for this player.
     *
     * @return a pair of the resulting sell.
     */
    fun sellBackpackContents(): CurrencyContainer {
        var totalDZ = 0
        var totalVault = 0.0
        for (entry in backpackContents) {
            val currency = DarkzoneModule.itemWorths[entry.key] ?: continue
            dzCurrency += currency.darkzoneCurrency
            KCommon.getInstance().economy.depositPlayer(Bukkit.getOfflinePlayer(playerUUID), currency.vaultCurrency)

            totalDZ += currency.darkzoneCurrency
            totalVault += currency.vaultCurrency
        }

        if (player != null && (totalDZ > 0 || totalVault > 0)) {
            DarkzoneModule.backpackSoldMsg.send(player,
                "MONEY", StringUtil.parseMoney(totalVault),
                "DZ_CURRENCY", StringUtil.parseCommas(totalDZ))
        }
        backpackContents.clear()
        return CurrencyContainer(totalVault, totalDZ)
    }

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