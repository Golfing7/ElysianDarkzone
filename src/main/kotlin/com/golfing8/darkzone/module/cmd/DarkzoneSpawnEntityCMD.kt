package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.darkzone.module.struct.DarkzoneMob
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.customEntityKey
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.CommandContext
import com.golfing8.kcommon.command.MCommand
import com.golfing8.kcommon.command.argument.CommandArgument
import org.bukkit.persistence.PersistentDataType

@Cmd(
    name = "spawnentity",
    description = "Spawns a darkzone mob."
)
class DarkzoneSpawnEntityCMD : MCommand<DarkzoneModule>() {
    override fun onRegister() {
        addArgument("type", CommandArgument.fromMap("entity definition", EntitySubModule.entities))
    }

    override fun execute(context: CommandContext) {
        val def: DarkzoneMob = context.next()

        val entity = def.entityDefinition.spawnEntity(context.player!!.location)
        entity.persistentDataContainer.set(customEntityKey, PersistentDataType.STRING, def._key)
    }
}