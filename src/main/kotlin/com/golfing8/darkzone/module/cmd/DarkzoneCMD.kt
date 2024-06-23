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
class DarkzoneCMD : MCommand<DarkzoneModule>() {
    override fun onRegister() {
        addSubCommand(DarkzoneGiveSpawnerCMD())
    }
}