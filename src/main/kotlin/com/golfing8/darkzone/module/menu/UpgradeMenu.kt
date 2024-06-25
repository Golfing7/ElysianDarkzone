package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.MenuUtils
import com.golfing8.kcommon.menu.PlayerMenuContainer
import com.golfing8.kcommon.menu.SimpleGUIItem
import com.golfing8.kcommon.menu.shape.MenuCoordinate
import com.golfing8.kcommon.struct.item.ItemStackBuilder
import com.golfing8.kcommon.struct.placeholder.Placeholder
import com.golfing8.kcommon.util.MS
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.entity.Player

/**
 * A menu for purchasing upgrades.
 */
class UpgradeMenu(player: Player) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("upgrade-menu")

    override fun loadMenu(): Menu {
        val playerData = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        val lockedItem = ItemStackBuilder(section.getConfigurationSection("locked-item"))
        val builder = MenuBuilder(section)
        builder.globalPlaceholders(Placeholder.curly(
            "BALANCE", StringUtil.parseCommas(playerData.dzCurrency)
        ))
        UpgradeType.entries.forEach { type ->
            val currentLevel = playerData.upgradeLevels[type] ?: 0
            if (playerData.getLevel().level < type.get().levelRequired) {
                val current = builder.getSpecialItem(type.name)
                builder.setSpecialItem(type.name, SimpleGUIItem(lockedItem, current.slot))
                builder.specialPlaceholders(type.name) { Placeholder.compileCurly(
                    "UPGRADE_DISPLAY", type.get().displayName,
                    "LEVEL", type.get().levelRequired,
                    "LEVEL_NAME", DarkzoneModule.levelsByLevel[type.get().levelRequired]!!.displayName
                )}
                builder.bindTo(type.name) {}
                return@forEach
            }
            builder.specialPlaceholders(type.name) {
                Placeholder.compileCurly(
                    "COST", type.get().upgradeCosts[currentLevel + 1] ?: "N/A",
                    "CURRENT_LEVEL", currentLevel,
                    "UPGRADE_DISPLAY", type.get().displayName
                )
            }
            builder.bindTo(type.name) {
                val cost = type.get().upgradeCosts[currentLevel + 1] ?: return@bindTo

                if (playerData.upgradeLevels.getOrDefault(type, 0) >= type.get().getMaxLevel()) {
                    return@bindTo
                }

                if (type.get().levelRequired > playerData.getLevel().level) {
                    DarkzoneModule.upgradeLevelTooLowMsg.send(player)
                    return@bindTo
                }

                if (playerData.dzCurrency < cost) {
                    DarkzoneModule.cantAffordUpgradeMsg.send(player,
                        "COST", cost,
                        "BALANCE", playerData.dzCurrency)
                    return@bindTo
                }

                playerData.dzCurrency -= cost
                playerData.upgradeLevels.compute(type) { _, v ->
                    if (v == null) 1 else v + 1
                }
                DarkzoneModule.boughtUpgradeMsg.send(player,
                    "COST", StringUtil.parseCommas(cost),
                    "UPGRADE", type.get().displayName)
                refresh()
            }
        }

        return builder.buildSimple()
    }
}