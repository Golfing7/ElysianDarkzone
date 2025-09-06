package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArguments
import com.golfing8.kcommon.config.lang.LangConf
import com.golfing8.kcommon.config.lang.Message
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@Cmd(
    name = "level",
    description = "Check your darkzone level"
)
class DarkzoneLevelCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var levelMessage = Message("&aYou are at level &e{LEVEL}, {LEVEL_DISPLAY}&a!")
    @LangConf
    private var levelOtherMessage = Message("&e{PLAYER} &ais at level &e{LEVEL}, {LEVEL_DISPLAY}&a!")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER, CommandSender::getName)
    }

    override fun execute(context: CommandContext) {
        val target = if (hasPermission(context.sender, "other"))
            Bukkit.getPlayer(context.arguments[0])
        else
            context.player

        if (target == null) {
            sendDefaultMessage(context.sender, "must-specify-player", "&cYou must specify a player!")
            return
        }

        val data = DarkzoneModule.getOrCreate(target.uniqueId, PlayerDarkzoneData::class.java)
        val level = data.getLevel()
        if (target !== context.player) {
            levelOtherMessage.send(context.sender, "LEVEL", level.level, "LEVEL_DISPLAY", level.displayName, "PLAYER", target.name)
        } else {
            levelMessage.send(target, "LEVEL", level.level, "LEVEL_DISPLAY", level.displayName)
        }
    }
}