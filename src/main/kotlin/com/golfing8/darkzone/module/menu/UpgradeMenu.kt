package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.PlayerMenuContainer
import com.golfing8.kcommon.struct.placeholder.Placeholder
import com.golfing8.kcommon.util.MS
import org.bukkit.entity.Player

/**
 * A menu for purchasing upgrades.
 */
class UpgradeMenu(player: Player) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("upgrade-menu")

    override fun loadMenu(): Menu {
        val builder = MenuBuilder(section)
        UpgradeType.entries.forEach { type ->
            builder.bindTo(type.name) {
                val playerData = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
                val cost = type.get().upgradeCosts.lastEntry().value

                if (type.get().levelRequired > playerData.getLevel().level) {
                    DarkzoneModule.upgradeLevelTooLowMsg.send(player)
                    return@bindTo
                }

                if (playerData.dzCurrency < cost) {
                    DarkzoneModule.cantAffordUpgradeMsg.send(player)
                    return@bindTo
                }

                playerData.dzCurrency -= cost
                playerData.upgradeLevels.compute(type) { _, v ->
                    if (v == null) 1 else v + 1
                }
                DarkzoneModule.boughtUpgradeMsg.send(player)
            }
        }

        return builder.buildSimple()
    }
}