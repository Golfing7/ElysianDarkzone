package com.golfing8.darkzone.module.struct.region

import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.struct.blocks.WeightedCollection
import com.golfing8.kcommon.struct.region.Region
import org.bukkit.Location

/**
 * Represents a region of the darkzone.
 */
class DarkzoneRegion : CASerializable {
    var levelRequirement = 0
    lateinit var region: Region
    lateinit var bossSpawnLocation: Location
    lateinit var potentialBossSpawns: WeightedCollection<String>
}