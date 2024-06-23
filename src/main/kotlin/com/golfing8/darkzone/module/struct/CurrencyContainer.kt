package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable

class CurrencyContainer : CASerializable {
    var vaultCurrency = 0.0
    var darkzoneCurrency = 0

    constructor() {
        vaultCurrency = 0.0
        darkzoneCurrency = 0
    }

    constructor(vaultCurrency: Double, darkzoneCurrency: Int) {
        this.vaultCurrency = vaultCurrency
        this.darkzoneCurrency = darkzoneCurrency
    }
}