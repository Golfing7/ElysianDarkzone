package com.golfing8.darkzone

import com.golfing8.kcommon.KPlugin
import org.bukkit.plugin.java.JavaPlugin

class ElysianDarkzone : KPlugin() {
    companion object {
        lateinit var INSTANCE: ElysianDarkzone
    }

    override fun onEnableInner() {
        INSTANCE = this
    }
}