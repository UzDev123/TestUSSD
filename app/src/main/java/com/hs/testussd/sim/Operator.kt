package com.hs.testussd.sim

sealed class Operator( val operatorName: String) {
    data object BeelineUz : Operator("Beeline Uz")
    data object HumansUz : Operator("HUMANS")
    data object MobiUz : Operator("MOBIUZ")
    data object Ucell : Operator("Ucell")
    data object UzMobile : Operator("UzMobile")
    data object Uztelecom : Operator("Uztelecom")
    data object PerfectumMobile : Operator("PERFECTUM MOBILE")
    data object OQ : Operator("Oq")
    data object NoService : Operator("No service")
}
