package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.menu.BackpackMenu
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * An internal command to force open another player's backpack.
 */
class DarkzoneBackpackNPCCMD(commandName: String) : MCommand<DarkzoneModule>(DarkzoneModule, "${commandName}npc", emptyList(), false) {
    override fun onRegister() {
        description = "Have an NPC open someone else's backpack"
        addArgument("player", CommandArguments.PLAYER)
    }

    override fun execute(context: CommandContext) {
        val target: Player = context.next()

        BackpackMenu(target, target, true)
            .open()
    }
}