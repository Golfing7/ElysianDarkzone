package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.entity.EntityDefinition

/**
 * Represents a darkzone mob.
 */
class DarkzoneMob : CASerializable {
    lateinit var _key: String
    lateinit var entityDefinition: EntityDefinition
        private set
    var xpPerKill = 0
}