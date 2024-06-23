package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.PlayerMenuContainer
import com.golfing8.kcommon.struct.placeholder.Placeholder
import org.bukkit.entity.Player

/**
 * A menu for viewing your own backpack.
 */
class BackpackMenu(player: Player, private val targetPlayer: Player, private val canSell: Boolean) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("backpack-menu")

    override fun loadMenu(): Menu {
        val targetData = DarkzoneModule.getOrCreate(targetPlayer.uniqueId, PlayerDarkzoneData::class.java)
        val builder = MenuBuilder(section)
        if (canSell) {
            builder.bindTo("sell-button") {
                targetData.sellBackpackContents()
            }
        }

        for (item in DarkzoneModule.itemWorths.keys) {
            builder.bindTo(item) {}
            builder.specialPlaceholders(item, Placeholder.compileCurly(
                "TOTAL", targetData.backpackContents[item] ?: 0
            ))
        }
        return builder.buildSimple()
    }
}