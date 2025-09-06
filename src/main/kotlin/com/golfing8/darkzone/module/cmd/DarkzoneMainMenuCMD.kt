package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.menu.DarkzoneMainMenu
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArguments
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * Opens the main menu of the darkzone plugin.
 */
@Cmd(
    name = "menu",
    description = "Opens the darkzone main menu"
)
class DarkzoneMainMenuCMD : MCommand<DarkzoneModule>() {
    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER, CommandSender::getName)
    }

    override fun execute(context: CommandContext) {
        val player = if (hasPermission(context.sender, "other"))
            Bukkit.getPlayer(context.arguments[0])
        else
            context.player

        if (player == null) {
            sendDefaultMessage(context.sender, "must-specify-player", "&cYou must specify a player!")
            return
        }

        DarkzoneMainMenu(player).open()
    }
}