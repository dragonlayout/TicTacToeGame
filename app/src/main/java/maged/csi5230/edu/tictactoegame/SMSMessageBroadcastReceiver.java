package maged.csi5230.edu.tictactoegame;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.Objects;

/**
 * Created by dragonlayout on 2017/11/10.
 */

public class SMSMessageBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    // todo 采用动态注册广播

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // sms receive action
        if (Objects.equals(intent.getAction(), SMS_ACTION)) {
            // get sms message from intent
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object pdusData[] = (Object[]) bundle.get("pdus"); // pdu: protocol data unit
                //解析短信
                // for this application 长度是一个短信可以包含的
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdusData[0]);
                String messageBody = msg.getMessageBody();
                System.out.println("message body: " + messageBody);
                // 处理 自定义的协议

                // todo parser
                // the protocol is string, interval with ',' number like x,y,z
                // for the x is the flag of the player's action
                // x => 0 join, y has three options, -1, 0, 1,
                //                                      -1 for receive a invitation,
                //                                      0 for the receiver say no to the invitation
                //                                      1 for the receiver say yes to the game
                //              z stands for the player's name like John
                //   => 1 start
                //   => 2 stop
                //   => 3 resume
                //   => 4 move
                //   => 5 game over
                int flag = Integer.valueOf(messageBody.split(",")[0]);
                switch (flag) {
                    case 0:
                        // 判断 y 是
                        int y = Integer.valueOf(messageBody.split(",")[1]);
                        if (y == -1) {
                            // you got a game invitation
                            // now z represent the player's name(who invite you to join the game)
                            // popup a dialog to ask you say yes or no
                            final String name = messageBody.split(",")[2];
                            View view = LayoutInflater.from(context).inflate(R.layout.view_welcome_dialog_layout, null);
                            final EditText editTextName = view.findViewById(R.id.edit_text_name);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Game Invitation")
                                    .setMessage(name + " " + "invite you to join the TicTacToe Game!")
                                    .setView(view)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!TextUtils.isEmpty(editTextName.getText())) {
                                                // save to sharedPreferences

                                                // go to the mainActivity
                                                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                                                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mainActivityIntent.putExtra("opponent_player_name", name);
                                                context.startActivity(mainActivityIntent);
                                                // todo send sms back to be ready for the game
                                            }
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // close the dialog
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            dialog.show();

                        } else if (y == 0) {

                        } else {

                        }
                        // 弹出对话框 显示是否加
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
