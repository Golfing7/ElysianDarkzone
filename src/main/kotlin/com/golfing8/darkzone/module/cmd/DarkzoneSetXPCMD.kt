package com.golfing8.darkzone.module.cmd

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
    name = "setxp",
    description = "Set the darkzone XP of a player"
)
class DarkzoneSetXPCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var setXpMsg = Message("&aSet &e{PLAYER}'s &axp to &e{XP}&a.")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER)
        addArgument("xp", CommandArguments.NON_NEGATIVE_INTEGER)
    }

    override fun execute(context: CommandContext) {
        val player: Player = context.next()
        val xp: Int = context.next()

        val data = DarkzoneModule.getOrCreate(player.uniqueId, PlayerDarkzoneData::class.java)
        data.darkzoneXP = xp.toLong()
        setXpMsg.send(context.sender, "PLAYER", player.name, "XP", StringUtil.parseCommas(xp))
    }
}