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
    name = "give",
    description = "Give a player currency"
)
class CurrencyGiveCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var gaveMsg = Message("&aGave &e{PLAYER} {GIVEN} Dracma&a.")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER)
        addArgument("give", CommandArguments.NON_NEGATIVE_INTEGER)
    }

    override fun execute(context: CommandContext) {
        val player: Player = context.next()
        val give: Int = context.next()

        val data = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        data.dzCurrency += give.toLong()
        gaveMsg.send(context.sender, "PLAYER", player.name, "GIVEN", StringUtil.parseCommas(give))
    }
}