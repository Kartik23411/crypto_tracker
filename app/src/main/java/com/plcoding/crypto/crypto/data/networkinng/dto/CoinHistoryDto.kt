package com.plcoding.cryptotracker.crypto.data.networkinng.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinHistoryDto(
    val data: List<CoinPriceDto>
)
