package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SVMReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        MySVM_Async mySVM = new MySVM_Async();
        mySVM.execute();
    }
}
