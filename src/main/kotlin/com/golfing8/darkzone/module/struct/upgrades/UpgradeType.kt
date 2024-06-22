package com.golfing8.darkzone.module.struct.upgrades

import java.util.function.Function

/**
 * Stores all different types of upgrade a player can unlock
 */
enum class UpgradeType(private val supplier: Function<UpgradeType, Upgrade>) {
    INCREASED_BOSS_DAMAGE(::IncreasedBossDamageUpgrade),
    REDUCED_MOB_DAMAGE(::ReducedMobDamageUpgrade),
    INCREASED_MOB_DAMAGE(::IncreasedMobDamageUpgrade),
    MORE_LOOT(::MoreLootUpgrade),
    LARGER_BACKPACK(::LargerBackpackUpgrade),
    AOE_DAMAGE(::AOEDamageUpgrade),
    AUTO_SELL(::AutoSellUpgrade),
    ;

    private lateinit var instance: Upgrade

    fun get(): Upgrade {
        return instance
    }

    companion object {
        fun startup() {
            for (upgrade in entries) {
                upgrade.instance = upgrade.supplier.apply(upgrade)
                upgrade.get().enable()
            }
        }

        fun shutdown() {
            for (upgrade in entries) {
                upgrade.get().disable()
            }
        }
    }
}