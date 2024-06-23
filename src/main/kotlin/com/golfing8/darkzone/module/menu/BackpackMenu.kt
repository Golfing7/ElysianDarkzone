package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.PlayerMenuContainer
import org.bukkit.entity.Player

/**
 * A menu for viewing your own backpack.
 */
class BackpackMenu(player: Player, private val canSell: Boolean) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("backpack-menu")

    override fun loadMenu(): Menu {
        val builder = MenuBuilder(section)
        if (canSell) {
            builder.bindTo("sell-button") {

            }
        }
        return builder.buildSimple()
    }
}