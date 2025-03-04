package com.plcoding.cryptotracker.crypto.data.networkinng.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double
)
// dto stands for data transfer object, it is a kotlin representation of the json data
// we can use coin class also here but that will violate the our architecture and have some disadvantages that if we want to use date and time instances when making call then it may cause error in serialization , so we make them different
