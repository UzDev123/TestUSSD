package com.hs.testussd.sim

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log

object SimCardInfoManager {
    @SuppressLint("MissingPermission", "NewApi")
    fun showSimCardsInfo(context: Context){
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Check if the device has SIM cards
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
            // Get SubscriptionManager
            val subscriptionManager = SubscriptionManager.from(context)

            // Get a list of active SIM cards
            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
            if (activeSubscriptions != null) {
                for (subscriptionInfo in activeSubscriptions) {
                  subscriptionInfo.apply {
                      Log.d("mlog", "Sim info: \n" +
                              "subscriptionId= $subscriptionId \n" +
                              "carrierId=$carrierId\n" +
                              "carrierName=$carrierName\n" +
                              "displayName=$displayName\n" +
                              "countryIso=$countryIso\n" +
                              "isEmbedded=$isEmbedded\n" +
                              "cardId=$cardId\n" +
                              ""


                      )

                  }



                }
            }
        }
    }


}