package com.golfing8.darkzone.module.cmd

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.kcommon.command.Cmd
import com.golfing8.kcommon.command.MCommand

/**
 * Main controlling command for the darkzone.
 */
@Cmd(
    name = "darkzone",
    description = "The main command for the darkzone"
)
class DarkzoneCMD(commandName: String) : MCommand<DarkzoneModule>(DarkzoneModule, commandName, listOf(), false) {
    override fun onRegister() {
        commandPermission = generatedCommandPermission

        addSubCommand(DarkzoneGiveSpawnerCMD())
        addSubCommand(DarkzoneBackpackCMD(DarkzoneModule.backpackCommandName))
        addSubCommand(DarkzoneBackpackNPCCMD(DarkzoneModule.backpackCommandName))
        addSubCommand(DarkzoneMainMenuCMD())
        addSubCommand(DarkzoneLevelCMD())
        addSubCommand(DarkzoneSetXPCMD())
    }
}