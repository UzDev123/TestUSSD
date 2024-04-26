package com.hs.testussd.sms

data class Sms(
    val date: String,
    val address: String?,
    val msg: String?,
    val type: String?,
    val simCard: String? = null,
    val creator: String? = null,


    )