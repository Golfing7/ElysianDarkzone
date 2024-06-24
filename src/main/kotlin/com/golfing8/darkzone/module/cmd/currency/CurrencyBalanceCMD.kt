package com.golfing8.darkzone.module.cmd.currency

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.data.PlayerDarkzoneData
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArguments
import com.golfing8.kcommon.config.lang.LangConf
import com.golfing8.kcommon.config.lang.Message
import com.golfing8.kcommon.util.StringUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@Cmd(
    name = "balance",
    description = "Check your balance"
)
class CurrencyBalanceCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var balanceMsg = Message("&aYou have &e{BALANCE} Dracma&a!")
    @LangConf
    private var balanceOtherMsg = Message("&e{PLAYER} &ahas &e{BALANCE} Dracma&a!")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER, CommandSender::getName)
    }

    override fun execute(context: CommandContext) {
        val player = if (checkPermissionExtension(context.sender, "other"))
            Bukkit.getPlayer(context.arguments[0])
        else
            context.player

        if (player == null) {
            sendDefaultMessage(context.sender, "must-specify-player", "&cYou must specify a player!")
            return
        }

        val data = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        if (player !== context.player) {
            balanceOtherMsg.send(context.sender, "PLAYER", player.name, "BALANCE", StringUtil.parseCommas(data.dzCurrency))
        } else {
            balanceMsg.send(context.sender, "BALANCE", StringUtil.parseCommas(data.dzCurrency))
        }
    }
}