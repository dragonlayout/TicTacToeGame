package maged.csi5230.edu.tictactoegame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import maged.csi5230.edu.tictactoegame.utils.SmsUtils;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{

    private SMSMessageBroadcastReceiver mReceiver;
    private Context mContext;

    private EditText mEditTextPhoneNumber;
    private Button mButtonJoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mContext = this;

        mEditTextPhoneNumber = findViewById(R.id.edit_text_phone_number);
        mButtonJoin = findViewById(R.id.btn_join);
        mButtonJoin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the broadcast receiver
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_join) {
            // 判断输入的 对方手机号码
            if (!TextUtils.isEmpty(mEditTextPhoneNumber.getText())) {
                // 判断全是数字
                if (TextUtils.isDigitsOnly(mEditTextPhoneNumber.getText())) {
                    // 弹出对话框 输入自己姓名
                    View view = LayoutInflater.from(mContext).inflate(R.layout.view_welcome_dialog_layout, null);
                    final EditText editTextName = view.findViewById(R.id.edit_text_name);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Input your name please.")
                            .setView(view)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 判断输入了内容
                                    if (!TextUtils.isEmpty(editTextName.getText())) {
                                        // save your name to sharedPreferences
                                        long opponentPhoneNumber = Integer.valueOf(mEditTextPhoneNumber.getText().toString());
                                        // 保存进 sharedPreferences
                                        SharedPreferences sf = getSharedPreferences("opponent_info", MODE_PRIVATE);
                                        sf.edit().putLong("phone_number", opponentPhoneNumber).apply();
                                        sf.edit().putString("name", editTextName.getText().toString()).apply();
                                        // send sms to opponent
                                        SmsUtils.sendMessage(opponentPhoneNumber, "0,-1," + editTextName.getText().toString());
                                    }
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // dismiss the dialog
                                }
                            })
                            .show();
                } else {
                    System.out.println("not all number 包含非数字");
                    // todo toast
                }
            } else {
                System.out.println("请输入电话号码");
                // todo toast
            }
        }
    }
}
