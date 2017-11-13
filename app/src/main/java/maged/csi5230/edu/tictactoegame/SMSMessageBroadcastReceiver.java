package maged.csi5230.edu.tictactoegame;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.Objects;

import maged.csi5230.edu.tictactoegame.utils.Constants;
import maged.csi5230.edu.tictactoegame.utils.SmsUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dragonlayout on 2017/11/10.
 */

public class SMSMessageBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private OnSmsReceivedListener mListener;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // sms receive action
        if (Objects.equals(intent.getAction(), SMS_ACTION)) {
            // get sms message from intent
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object pdusData[] = (Object[]) bundle.get("pdus"); // pdu: protocol data unit
                // parse the sms
                // for this application, one single sms can contain the protocol
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdusData[0]);
                String messageBody = msg.getMessageBody();
                final long phoneNumber = Long.valueOf(msg.getDisplayOriginatingAddress());
                // handle the self-defined protocol
                // the protocol is string, interval with ',' number like x,y,z
                // for the x is the flag of the player's action
                // x => 0 join, y has three options, -1, 0, 1,
                //                                      -1 for you send a invitation,
                //                                      0 for the receiver say no to the invitation
                //                                      1 for the receiver say yes to the game
                //              z stands for the player's name like John
                //   => 1 start
                //             y has three options, -1, 0, 1
                //                                    -1 for you send a start request
                //                                     0 for you say no the the start request
                //                                     1 for you say yes the the start request
                //             z stands for the player's name like John
                //
                //   => 2 stop button
                //              y has three options -1, 0, 1
                //                                  -1 for request for stopping the game
                //                                   0 for refuse to stop the game
                //                                   1 for agree to stop the game
                //              z => name
                //   => 4 move  i.e. => 4,0,1,chenlong
                //              y position data(0,1), z => opponent's name
                //              y => -1,1 guest wins circle
                //              y => -1,0 host wins plus
                //              y => -1,-1 no winner
                int flag = Integer.valueOf(messageBody.split(",")[0]);
                switch (flag) {
                    case 0:
                        int joinY = Integer.valueOf(messageBody.split(",")[1]);
                        final String joniName = messageBody.split(",")[2];
                        if (joinY == -1) {
                            // you got a game invitation
                            // now z represent the player's name(who invite you to join the game)
                            // popup a dialog to ask you say yes or no
                            View view = LayoutInflater.from(context).inflate(R.layout.view_welcome_dialog_layout, null);
                            final EditText editTextName = view.findViewById(R.id.edit_text_name);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Game Invitation")
                                    .setMessage(joniName + " " + "invite you to join the TicTacToe Game!")
                                    .setView(view)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!TextUtils.isEmpty(editTextName.getText())) {
                                                // save to sharedPreferences
                                                SharedPreferences sf = context.getSharedPreferences(Constants.SF_FILE_NAME, MODE_PRIVATE);
                                                sf.edit().putLong(Constants.PHONE_NUMBER, phoneNumber).apply();
                                                sf.edit().putString(Constants.NAME, editTextName.getText().toString()).apply();
                                                // go to the mainActivity
                                                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                                                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mainActivityIntent.putExtra(Constants.OPPONENT_PLAYER_NAME, joniName);
                                                context.startActivity(mainActivityIntent);

                                                SmsUtils.sendMessage(phoneNumber, "0,1," + editTextName.getText().toString());
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

                        } else if (joinY == 1) {
                            //  join accepted， jump to mainActivity
                            Intent mainActivityIntent = new Intent(context, MainActivity.class);
                            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainActivityIntent.putExtra(Constants.OPPONENT_PLAYER_NAME, joniName);
                            context.startActivity(mainActivityIntent);
                        } else {
                            // todo toast 对方未接受 join请求
                        }
                        break;
                    case 1:
                        int startY = Integer.valueOf(messageBody.split(",")[1]);
                        final String startName = messageBody.split(",")[2];
                        if (startY == -1) {
                            // pop up a dialog
                            // get a game start request
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Start the game")
                                    .setMessage("You want to start the game with " + startName)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // send sms back
                                            SharedPreferences sf = context.getSharedPreferences(Constants.SF_FILE_NAME, MODE_PRIVATE);
                                            long opponentPhoneNumber = sf.getLong(Constants.PHONE_NUMBER, 0);
                                            String name = sf.getString(Constants.NAME, "default name");
                                            SmsUtils.sendMessage(opponentPhoneNumber, "1,1," + name);
                                            // 通知
                                            mListener.agreeToStartTheGame(startName);
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // dismiss the dialog
                                        }
                                    })
                                    .show();
                        } else if (startY == 1) {
                            // say yes
                            // not your turn just display whose turn it is
                            mListener.getStartGameConfirmed(startName);
                        } else {
                            // todo toast not agree to start the game
                        }
                        break;
                    case 2:
                        // stop button
                        int stopY = Integer.valueOf(messageBody.split(",")[1]);
                        SharedPreferences sf = context.getSharedPreferences(Constants.SF_FILE_NAME, MODE_PRIVATE);
                        final long opponentPhoneNumber = sf.getLong(Constants.PHONE_NUMBER, 0);
                        final String name = sf.getString(Constants.NAME, "default name");
                        if (stopY == -1) {
                            // 对手提出结束游戏 弹出对话框 统一或者拒绝
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Stop the game.")
                                    .setMessage("Do you agree to stop the game?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //  send back sms
                                            SmsUtils.sendMessage(opponentPhoneNumber, "2,1," + name);
                                            //  close the main activity
                                            mListener.finishMainActivity();
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //  send back sms no agree to stop the game

                                            SmsUtils.sendMessage(opponentPhoneNumber, "2,0," + name);
                                        }
                                    })
                                    .show();
                        } else if (stopY == 0) {
                            //  对手拒绝 结束游戏 继续下去
                            // todo toast
                        } else {
                           // 对手统一结束游戏 关闭 main activity
                            mListener.finishMainActivity();
                        }
                        break;
                    case 4:
                        // move part 4,0,0,name
                        int moveFlag = Integer.valueOf(messageBody.split(",")[1]);
                        if (moveFlag < 0) {
                            // got a winner or no winner pop up a dialog
                            int result = Integer.valueOf(messageBody.split(",")[2]);
                            String winnerName = messageBody.split(",")[3];
                            String dialogMessage = "";
                            int hostOrGuest = mListener.hostOrGuest();
                            if (hostOrGuest == result) {
                                dialogMessage = "Congrats you win the game.";
                            } else {
                                dialogMessage = winnerName + " wins the game.";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Game Over")
                                    .setMessage(dialogMessage)
//                                    .setPositiveButton("Restart the game", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // todo restart the game
//
//                                        }
//                                    })
                                    .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // leave the game
                                            mListener.finishMainActivity();
                                        }
                                    })
                                    .show();

                        } else {
                            // send back sms
                            // get to display the opponent's move
                            int x = Integer.valueOf(messageBody.split(",")[1]);
                            int y = Integer.valueOf(messageBody.split(",")[2]);
                            mListener.showOpponentMove(x, y);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setOnSmsReceivedListener(OnSmsReceivedListener listener) {
        mListener = listener;
    }

    public interface OnSmsReceivedListener {
        // agree to start the game
        void agreeToStartTheGame(String opponentName);
        // host get the confirm of starting the game
        void getStartGameConfirmed(String opponentName);
        // display the opponent's move
        void showOpponentMove(int x, int y);
        // finish mainActivity
        void finishMainActivity();

        int hostOrGuest();
    }
}
