package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.Range

/**
 * Represents data for a custom spawner
 */
class DarkzoneSpawner : CASerializable {
    lateinit var _key: String
    var ticksPerSpawn = Range(200.0, 800.0)
    var maxOfTypeNearby = 10
    var nearbyRange = 32.0
}