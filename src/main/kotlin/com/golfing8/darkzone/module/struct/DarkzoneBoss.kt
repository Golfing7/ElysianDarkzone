package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.entity.EntityDefinition

/**
 * Structure for a spawned darkzone boss
 */
class DarkzoneBoss : CASerializable {
    var xpPerKill: Int = 0
    var minimumDamagePercentageForDrops: Double = 20.0
    lateinit var entityDefinition: EntityDefinition
}