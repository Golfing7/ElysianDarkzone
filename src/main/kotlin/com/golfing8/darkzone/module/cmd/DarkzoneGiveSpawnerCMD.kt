package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.DarkzoneMob
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.SpawnerSubModule
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArgument
import com.golfing8.kcommon.command.argument.CommandArguments
import com.golfing8.kcommon.config.lang.LangConf
import com.golfing8.kcommon.config.lang.Message
import com.golfing8.kcommon.struct.entity.EntityDefinition
import com.golfing8.kcommon.util.PlayerUtil
import org.bukkit.entity.Player

/**
 * Gives a darkzone spawner.
 */
@Cmd(
    name = "givespawner",
    description = "Give a darkzone spawner to someone"
)
class DarkzoneGiveSpawnerCMD : MCommand<DarkzoneModule>() {
    @LangConf
    private var giveMsg = Message("&aGave &e{PLAYER} &aa &e{ENTITY} &aspawner.")
    @LangConf
    private var receivedMsg = Message("&aReceived a &e{ENTITY} &aspawner.")

    override fun onRegister() {
        addArgument("player", CommandArguments.PLAYER)
        addArgument("entity", CommandArgument.fromMap("entity", EntitySubModule.entities))
    }

    override fun execute(context: CommandContext) {
        val player: Player = context.next()
        val entity: DarkzoneMob = context.next()

        val spawnerItem = SpawnerSubModule.generateSpawnerItem(entity)
        PlayerUtil.givePlayerItemSafe(player, spawnerItem)
        giveMsg.send(context.sender, "PLAYER", player.name, "ENTITY", entity._key)
        receivedMsg.send(player, "ENTITY", entity._key)
    }
}