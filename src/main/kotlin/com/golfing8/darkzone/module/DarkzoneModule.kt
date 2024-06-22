package com.golfing8.darkzone.module

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.SpawnerSubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.module.Module
import com.golfing8.kcommon.module.ModuleInfo
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataType

@ModuleInfo(
    name = "darkzone"
)
object DarkzoneModule : Module() {
    override fun onEnable() {
        addSubModule(EntitySubModule)
        addSubModule(SpawnerSubModule)
    }

    override fun onDisable() {

    }
}