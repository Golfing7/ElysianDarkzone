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
import kotlin.math.max

@Cmd(
    name = "take",
    description = "Take a player's currency"
)
class CurrencyTakeCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var takeMsg = Message("&aTook &e{TAKEN} Dracma&a from &e{PLAYER}&a.")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER)
        addArgument("give", CommandArguments.NON_NEGATIVE_INTEGER)
    }

    override fun execute(context: CommandContext) {
        val player: Player = context.next()
        val task: Int = context.next()

        val data = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        data.dzCurrency = max(0, data.dzCurrency - task.toLong())
        takeMsg.send(context.sender, "PLAYER", player.name, "TAKEN", StringUtil.parseCommas(task))
    }
}