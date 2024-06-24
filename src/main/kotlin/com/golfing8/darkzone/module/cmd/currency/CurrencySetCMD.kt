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
import org.bukkit.entity.Player

@Cmd(
    name = "set",
    description = "Set a player's currency amount"
)
class CurrencySetCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var setBalanceMsg = Message("&aSet &e{PLAYER}'s &adracma balance to &e{BALANCE}&a.")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER)
        addArgument("balance", CommandArguments.NON_NEGATIVE_INTEGER)
    }

    override fun execute(context: CommandContext) {
        val player: Player = context.next()
        val balance: Int = context.next()

        val data = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        data.dzCurrency = balance.toLong()
        setBalanceMsg.send(context.sender, "PLAYER", player.name, "BALANCE", StringUtil.parseCommas(balance))
    }
}