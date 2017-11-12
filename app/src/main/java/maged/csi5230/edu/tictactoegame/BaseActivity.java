package maged.csi5230.edu.tictactoegame;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import maged.csi5230.edu.tictactoegame.utils.ActivityController;

/**
 * Created by dragonlayout on 2017/11/11.
 */

public class BaseActivity extends AppCompatActivity{
    // private

    protected SMSMessageBroadcastReceiver mReceiver;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ActivityController.add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new SMSMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(SMSMessageBroadcastReceiver.SMS_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.remove(this);
    }
}
