package com.golfing8.darkzone.module.struct

import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.blocks.WeightedCollection
import com.golfing8.kcommon.struct.region.Region
import org.bukkit.Location

/**
 * Represents a region of the darkzone.
 */
class DarkzoneRegion : CASerializable {
    var levelRequirement = 0
    lateinit var region: Region
    private lateinit var bossSpawnLocation: Location
    private lateinit var potentialBossSpawns: WeightedCollection<String>

    /**
     * Spawns a boss in this region.
     */
    fun spawnBoss(): DarkzoneBossWrapper {
        val typeID = potentialBossSpawns.get()
        val bossType = EntitySubModule.bosses[typeID] ?: throw IllegalArgumentException("Boss $typeID doesn't exist.")

        return EntitySubModule.spawnBoss(bossSpawnLocation, bossType)
    }
}