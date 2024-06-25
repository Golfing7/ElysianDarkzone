package com.golfing8.darkzone.module.struct

import com.golfing8.kcommon.config.adapter.CASerializable
import com.golfing8.kcommon.struct.item.ItemStackBuilder

/**
 * Represents an item that is paired with its sell value
 */
class DarkzoneItem : CASerializable {
    lateinit var item: ItemStackBuilder
    lateinit var sellValue: CurrencyContainer
}