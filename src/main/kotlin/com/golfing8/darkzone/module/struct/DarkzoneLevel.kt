package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable

/**
 * Represents the structure of a darkzone level.
 */
class DarkzoneLevel : CASerializable {
    lateinit var _key: String
    lateinit var displayName: String
    var xpRequired: Long = 0
    @Transient
    var level: Int = 0
}