package com.golfing8.darkzone.module

import com.golfing8.darkzone.ElysianDarkzone
import com.golfing8.darkzone.module.struct.PlayerDarkzoneData
import com.golfing8.darkzone.module.struct.region.DarkzoneRegion
import com.golfing8.darkzone.module.struct.upgrades.UpgradeType
import com.golfing8.darkzone.module.sub.EntitySubModule
import com.golfing8.darkzone.module.sub.SpawnerSubModule
import com.golfing8.kcommon.config.generator.Conf
import com.golfing8.kcommon.data.DataManagerContainer
import com.golfing8.kcommon.module.Module
import com.golfing8.kcommon.module.ModuleInfo
import com.golfing8.kcommon.struct.entity.EntityDefinition
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataType

@ModuleInfo(
    name = "darkzone"
)
object DarkzoneModule : Module(), DataManagerContainer {
    @Conf
    lateinit var regions: HashMap<String, DarkzoneRegion>
        private set

    override fun onEnable() {
        addDataManager("darkzone-data", PlayerDarkzoneData::class.java)

        addSubModule(EntitySubModule)
        addSubModule(SpawnerSubModule)

        UpgradeType.startup()
    }

    override fun onDisable() {
        UpgradeType.shutdown()
    }

    /**
     * Checks if the given location is within a darkzone area.
     *
     * @param location the location to check.
     * @return if it's in a darkzone area.
     */
    fun inDarkzoneArea(location: Location): Boolean {
        return regions.values.find {
            it.region.isPositionWithin(location)
        } != null
    }

    /**
     * Gets the darkzone region from the given location.
     *
     * @param location the location to check.
     * @return the region the location is in.
     */
    fun getDarkzoneRegion(location: Location): DarkzoneRegion? {
        return regions.values.find {
            it.region.isPositionWithin(location)
        }
    }
}