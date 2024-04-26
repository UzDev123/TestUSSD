package com.hs.testussd.sim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import java.util.List;

public class CallBySlot {
    private Context ctx;

    public CallBySlot(Context context) {
        this.ctx = context;
    }

    private final static String[] simSlotName = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

    //    void call(String number) {
//
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("com.android.phone.force.slot", true);
//        intent.putExtra("Cdma_Supp", true);
//        //Add all slots here, according to device.. (different device require different key so put all together)
//        for (String s : simSlotName)
//            intent.putExtra(s, 0); //0 or 1 according to sim.......
//
//        //works only for API >= 21
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", (Parcelable) " here You have to get phone account handle list by using telecom manger for both sims:- using this method getCallCapablePhoneAccounts()");
//
//        context.startActivity(intent);
//    }
    @SuppressLint("MissingPermission")
   public void call(String number, Integer simselected) {
        TelecomManager telecomManager = (TelecomManager) ctx.getSystemService(Context.TELECOM_SERVICE);
        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();


        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected == 0) {   //0 for sim1
            for (String s : simSlotName)
                intent.putExtra(s, 0); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && !phoneAccountHandleList.isEmpty())
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));

        } else {

            for (String s : simSlotName)
                intent.putExtra(s, 1); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));

        }
        ctx.startActivity(intent);
    }
}
