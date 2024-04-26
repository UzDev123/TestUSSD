package com.hs.testussd.sms

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Telephony
import android.util.Log

class SmsModule(private val context: Context) {


    @SuppressLint("NewApi")
    fun show() {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            Log.d("mlog", "*********************************************************************: ")
            cursor.columnNames.forEach {
                if (cursor.moveToFirst()) {
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(it))
                    Log.d("mlog", "$it: data: $data")
                }
            }
            Log.d("mlog", "*********************************************************************: ")

            cursor.close()

        } else {
            Log.d("mlog", "No message to show! ");
        }
    }

    @SuppressLint("Recycle")
    fun deleteSms(id: String) {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
//        contentResolver.delete(Telephony.Sms.CONTENT_URI.toString() + "/id","date=?",cursor.getString(cursor.))
    }

    fun showCarrierInfo() {
        val contentResolver = context.contentResolver
        val cursor =
            contentResolver.query(Telephony.Carriers.CONTENT_URI, null, null, null, null)
        val totalSMS: Int
        if (cursor != null) {
            cursor.columnNames.forEach {
                val data = cursor.getString(cursor.getColumnIndexOrThrow(it))
                Log.d("mlog", "$it: $data")
            }
            cursor.close()

//            totalSMS = cursor.count
//            if (cursor.moveToFirst()) {
//                for (j in 0 until totalSMS) {
//                    val smsDate = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
//                    val address =
//                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
//                    val msg = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
//
//                    val simCardId =
            cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.SUBSCRIPTION_ID))
//                    val creator =
//                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.CREATOR))
//                    val type =
//                        when (cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
//                            .toInt()) {
//                            Telephony.Sms.MESSAGE_TYPE_INBOX -> MessageType.INBOX
//                            Telephony.Sms.MESSAGE_TYPE_SENT -> MessageType.SENT
//                            Telephony.Sms.MESSAGE_TYPE_OUTBOX -> MessageType.OUTBOX
//                            Telephony.Sms.MESSAGE_TYPE_FAILED -> MessageType.FAILED
//                            Telephony.Sms.MESSAGE_TYPE_DRAFT -> MessageType.DRAFT
//                            else -> {
//                                MessageType.UNKNOWN
//                            }
//                        }
//
//                    cursor.moveToNext()
//                }
//            }
//            cursor.close()
        } else {
            Log.d("mlog", "No message to show! ");
        }
    }


}