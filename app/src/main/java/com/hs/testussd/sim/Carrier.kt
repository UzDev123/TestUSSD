package com.hs.testussd.sim

data class Carrier(
    val id: Int,
    val operator: String,
    val ussd: String,
    val carrierId: Int,
    val subscriptionId: Int,
)
