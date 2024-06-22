package com.golfing8.darkzone.module.struct.upgrades

import com.golfing8.darkzone.module.sub.EntitySubModule
import org.bukkit.entity.LivingEntity

class IncreasedBossDamageUpgrade(type: UpgradeType) : IncreasedDamageUpgrade(type) {
    override fun worksOn(entity: LivingEntity): Boolean {
        return EntitySubModule.isBoss(entity)
    }
}