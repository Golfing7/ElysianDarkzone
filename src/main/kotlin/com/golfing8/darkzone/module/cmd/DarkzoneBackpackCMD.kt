package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.menu.BackpackMenu
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArguments
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DarkzoneBackpackCMD(commandName: String) : MCommand<DarkzoneModule>(DarkzoneModule, commandName, emptyList(), true) {
    override fun onRegister() {
        description = "Open your backpack"
        commandPermission = generatedCommandPermission
        addArgument("player", CommandArguments.PLAYER, CommandSender::getName)
    }

    override fun execute(context: CommandContext) {
        val target = if (checkPermissionExtension(context.sender, "other"))
            context.next()
        else
            context.player!!

        BackpackMenu(context.player!!, target, checkPermissionExtension(context.player, "sell-anywhere"))
            .open()
    }
}