package maged.csi5230.edu.tictactoegame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import maged.csi5230.edu.tictactoegame.utils.Constants;
import maged.csi5230.edu.tictactoegame.utils.SmsUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static String CIRCLE = "〇";
    private static String PLUS = "＋";

    Button btn1, btn2, btn3,
    btn4, btn5, btn6,
    btn7, btn8, btn9;
    List<Button> btnList;
    Button btnStart, btnStop;

    TextView textViewInfo;
    TextView textViewTitle;

    // 游戏的棋盘 3*3
    private int[][] mTable;

    // 0 => host(plus), 1 => guest(circle first to move)
    private int hostOrGuest = -1;
    private boolean mIsYourTurn = false;

    private String mOpponentPlayerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver.setOnSmsReceivedListener(new SMSMessageBroadcastReceiver.OnSmsReceivedListener() {
            @Override
            public void agreeToStartTheGame() {
                // what to do when start the game
                // enable all board button
                enableAllBoardButton();
                // I am the guest first to move
                hostOrGuest = Constants.GUEST;
                // display whose turn it is
                textViewInfo.setText("It's your turn.");
                mIsYourTurn = true;
            }

            @Override
            public void getStartGameConfirmed(String opponentName) {
                hostOrGuest = Constants.HOST;
                textViewInfo.setText("It's " + opponentName + " turn.");
                mIsYourTurn = false;
                mOpponentPlayerName = opponentName;
            }

            @Override
            public void showOpponentMove(int x, int y) {
                // enable board buttons
                enableAllBoardButton();
                // show the move of the opponent x, y => 0, 1
                Button movedBtn = getMovedButton(x, y);
                if (movedBtn != null) {
                    if (hostOrGuest == Constants.HOST) {
                        movedBtn.setText(CIRCLE);
                    } else if (hostOrGuest == Constants.GUEST){
                        movedBtn.setText(PLUS);
                    }
                }
            }
        });
    }

    private void initViews() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnList = new ArrayList<>();
        btnList.add(btn1);
        btnList.add(btn2);
        btnList.add(btn3);
        btnList.add(btn4);
        btnList.add(btn5);
        btnList.add(btn6);
        btnList.add(btn7);
        btnList.add(btn8);
        btnList.add(btn9);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        textViewInfo = findViewById(R.id.text_view_info);
        textViewTitle = findViewById(R.id.text_view_game_title);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        disableAllBoardButton();
    }

    private void initData() {
        mTable = new int[3][3];

        String opponentPlayerName = getIntent().getStringExtra(Constants.OPPONENT_PLAYER_NAME);
        textViewTitle.setText("The game with " + opponentPlayerName);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        SharedPreferences sf = getSharedPreferences(Constants.SF_FILE_NAME, MODE_PRIVATE);
        long opponentPhoneNumber = sf.getLong(Constants.PHONE_NUMBER, 0000);
        String name = sf.getString(Constants.NAME, "default name");
        switch (id) {
            case R.id.btn_start:
                // 谁点击 start button 都将发送 sms 给对手 请求开始游戏
                // get opponent phone number from sharedPreferences
                SmsUtils.sendMessage(opponentPhoneNumber, "1,-1," + name);
                break;
            case R.id.btn_stop:
                break;
            // 在 对应的button 设置符号
            // 并执行对游戏对判断
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
            case R.id.btn6:
            case R.id.btn7:
            case R.id.btn8:
            case R.id.btn9:
                //  先判断当前棋盘状态 当 guest 点击棋盘按钮时，发送 sms 4,0,1,name
                // 先占位
                Button btnSelected = findViewById(id);
                int x = getCoordinateX(btnSelected.getTag());
                int y = getCoordinateY(btnSelected.getTag());
                if (hostOrGuest == Constants.HOST) {
                    // plus
                    mTable[x][y] = -1;
                    btnSelected.setText(PLUS);
                } else if (hostOrGuest == Constants.GUEST){
                    mTable[x][y] = 1;
                    btnSelected.setText(CIRCLE);
                }
                // 1. 判断游戏是否结束
                int result = checkResult();
                switch (result) {
                    case 0:
                    case 1:
                        // 弹窗
                        String dialogMessage = "";
                        if (result == hostOrGuest) {
                            // you win
                            dialogMessage = "Congrats you win the game.";
                        } else {
                            // opponent wins
                            dialogMessage = mOpponentPlayerName + " wins the game.";
                        }
                        // pop up a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Game Over")
                                .setMessage(dialogMessage)
                                .setPositiveButton("Restart the game", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // todo restart
                                    }
                                })
                                .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // todo leave the game
                                    }
                                })
                                .show();
                        SmsUtils.sendMessage(opponentPhoneNumber, "4,-1," + result + "," + dialogMessage);
                        break;
                    case 2:
                        // no winner yet 继续游戏
                        //  正常通信 发送坐标
                        SmsUtils.sendMessage(opponentPhoneNumber, "4," + x + "," + y + "," + name);
                        // disable the board button
                        disableAllBoardButton();
                        // display whose turn it is
                        textViewInfo.setText("It's " + mOpponentPlayerName + " turn.");
                        break;
                    default:
                        break;
                }
                // 2. 判断当前棋盘是否满了
                if (isBoardFull()) {
                    //  通知双方 棋盘满了 no winner, end game
                    // 发送 game over
                    // pop up a dialog
                    String dialogMessage = "Oh, no winner.";
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Game Over")
                            .setMessage(dialogMessage)
                            .setPositiveButton("Restart the game", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // todo restart
                                }
                            })
                            .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // todo leave the game
                                }
                            })
                            .show();
                    SmsUtils.sendMessage(opponentPhoneNumber, "4,-1,-1" + "," + dialogMessage);
                } else {
                    // 继续游戏
                    SmsUtils.sendMessage(opponentPhoneNumber, "4," + x + "," + y + "," + name);
                    // disable the board button
                    disableAllBoardButton();
                    // display whose turn it is
                    textViewInfo.setText("It's " + mOpponentPlayerName + " turn.");
                }
                break;
        }
    }

    /**
     *
     * @return true => no free position, false => at least have one free position
     */
    private boolean isBoardFull() {
        boolean canContinue = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (mTable[i][j] == 0) {
                    canContinue = true;
                }
            }
        }
        return canContinue;
    }

    /**
     *
     * @return 0 => plus win, 1 => circle win, 2 => no winner yet
     */
    private int checkResult() {
        // circle => 1 point
        // plus => -1 point
        // 横向检查
        for (int i = 0; i < 3; i++) {
            int pointSum = mTable[i][0] + mTable[i][1] + mTable[i][2];
            if (pointSum == 3) {
                return 1;
            } else if (pointSum == -3) {
                return 0;
            }
        }
        for (int i = 0;i < 3; i++) {
            int pointSum = mTable[0][i] + mTable[1][i] + mTable[2][i];
            if (pointSum == 3) {
                return 1;
            } else if (pointSum == -3) {
                return 0;
            }
        }
        if (mTable[0][0] + mTable[1][1] + mTable[2][2] == 3) {
            return 1;
        } else if (mTable[0][0] + mTable[1][1] + mTable[2][2] == -3) {
            return 0;
        }
        if (mTable[0][2] + mTable[1][1] + mTable[2][0] == 3) {
            return 1;
        } else if (mTable[0][2] + mTable[1][1] + mTable[2][0] == -3) {
            return 0;
        }
        return 2;
    }

    private int getCoordinateX(Object buttonTag) {
        String tag = buttonTag.toString();
        return Integer.valueOf(tag.split(",")[0]);
    }

    private int getCoordinateY(Object buttonTag) {
        String tag = buttonTag.toString();
        return Integer.valueOf(tag.split(",")[1]);
    }

    private void disableAllBoardButton() {
        for (Button btn : btnList) {
            btn.setEnabled(false);
        }
    }

    private void enableAllBoardButton() {
        for (Button btn : btnList) {
            btn.setEnabled(true);
        }
    }

    private Button getMovedButton(int x, int y) {
        String tag = "" + x + "," + y;
        for (Button btn : btnList) {
            if (btn.getTag().equals(tag)) {
                return btn;
            }
        }
        return null;
    }
}
