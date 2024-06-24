package com.golfing8.darkzone.module.cmd.currency

import com.golfing8.darkzone.module.DarkzoneModule
import com.golfing8.kcommon.command.MCommand

/**
 * The main controlling command for the darkzone currency.
 */
class CurrencyCMD(name: String) : MCommand<DarkzoneModule>(DarkzoneModule, name, listOf(), false) {
    override fun onRegister() {
        description = "Main currency command"

        addSubCommand(CurrencyBalanceCMD())
        addSubCommand(CurrencySetCMD())
        addSubCommand(CurrencyGiveCMD())
        addSubCommand(CurrencyTakeCMD())
    }
}