package com.golfing8.darkzone.module.menu

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.menu.Menu
import com.golfing8.kcommon.menu.MenuBuilder
import com.golfing8.kcommon.menu.PlayerMenuContainer
import com.golfing8.kcommon.struct.placeholder.Placeholder
import com.golfing8.kcommon.util.PlayerUtil
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import kotlin.math.min

/**
 * A menu for viewing your own backpack.
 */
class BackpackMenu(player: Player, private val targetPlayer: Player, private val canSell: Boolean) : PlayerMenuContainer(player) {
    private val section = DarkzoneModule.getConfig("menus").getConfigurationSection("backpack-menu")

    override fun loadMenu(): Menu {
        val targetData = DarkzoneModule.getOrCreate(targetPlayer.uniqueId, PlayerDarkzoneData::class.java)
        val builder = MenuBuilder(section)
        builder.globalPlaceholders(Placeholder.curly("CAPACITY", StringUtil.parseCommas(targetData.getBackpackSize())))
        if (canSell) {
            builder.bindTo("sell") {
                targetData.sellBackpackContents()
                getMenu().refreshSpecialItems()
            }
        }

        for (item in DarkzoneModule.itemDefinitions.keys) {
            builder.bindTo(item) {
                if (!canSell)
                    return@bindTo

                val totalItem = targetData.backpackContents[item] ?: 0
                if (totalItem <= 0)
                    return@bindTo

                val takeAmount = if (it.click.isLeftClick) 1 else min(totalItem, DarkzoneModule.backpackRightClickTakeAmount)
                val itemStack = DarkzoneModule.itemDefinitions[item]!!.item.buildFromTemplate()
                itemStack.amount = takeAmount
                PlayerUtil.givePlayerItemSafe(player, itemStack)
                targetData.takeItems(item, takeAmount)
                getMenu().refreshSpecialItems()
            }
            builder.specialPlaceholders(item) {
                Placeholder.compileCurly(
                    "TOTAL", targetData.backpackContents[item] ?: 0
                )
            }
        }
        return builder.buildSimple()
    }
}