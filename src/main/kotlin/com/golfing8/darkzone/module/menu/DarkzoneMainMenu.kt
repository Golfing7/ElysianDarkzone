package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.PlayerMenuContainer
import org.bukkit.entity.Player

/**
 * The main NPC menu for the darkzone
 */
class DarkzoneMainMenu(player: Player) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("main-menu")

    override fun loadMenu(): Menu {
        val builder = MenuBuilder(section)

        builder.bindTo("backpack-menu") {
            BackpackMenu(player, player, true).open()
        }

        builder.bindTo("upgrade-menu") {
            UpgradeMenu(player).open()
        }

        return builder.buildSimple()
    }
}