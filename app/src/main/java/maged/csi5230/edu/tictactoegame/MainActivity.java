package maged.csi5230.edu.tictactoegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static String CIRCLE = "〇";
    private static String PLUS = "＋";

    Button btn1, btn2, btn3,
    btn4, btn5, btn6,
    btn7, btn8, btn9;
    Button btnStart, btnStop;

    TextView textViewInfo;
    TextView textViewTitle;

    // 游戏的棋盘 3*3
    private int[][] mTable;
    private boolean mCircleTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
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
        btnStop.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void initData() {
        mTable = new int[3][3];
        mCircleTurn = true;

        String opponentPlayerName = getIntent().getStringExtra("opponent_player_name");
        textViewTitle.setText("The game with " + opponentPlayerName);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_start:
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
                Button btnSelected = findViewById(id);
                if (checkResult() == 3) {
                    System.out.println("棋盘没有空闲位置 game over with no winner");
                    break;
                }
                if (mCircleTurn) {
                    mTable[getCoordinateX(btnSelected.getTag())][getCoordinateY(btnSelected.getTag())] = 1;
                    btnSelected.setText(CIRCLE);
                    mCircleTurn = false;
                } else {
                    mTable[getCoordinateX(btnSelected.getTag())][getCoordinateY(btnSelected.getTag())] = -1;
                    btnSelected.setText(PLUS);
                    mCircleTurn = true;
                }
                switch (checkResult()) {
                    case 0:
                        System.out.println("circle wins");
                        break;
                    case 1:
                        System.out.println("plus wins");
                        break;
                    case 2:
                        System.out.println("continue to play");
                        break;
                }
                break;
        }
    }

    /**
     *
     * @return 0 => circle win, 1 => plus win, 2 => continue to play, 3 => game over with no winner
     */
    private int checkResult() {
        // todo 提取出来 逻辑调整
        // 检查 当前棋盘有空闲位置
        boolean canContinue = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (mTable[i][j] == 0) {
                    canContinue = true;
                }
            }
        }
        if (!canContinue) {
            return 3;
        }
        // circle => 1 point
        // plus => -1 point
        // 横向检查
        for (int i = 0; i < 3; i++) {
            int pointSum = mTable[i][0] + mTable[i][1] + mTable[i][2];
            if (pointSum == 3) {
                return 0;
            } else if (pointSum == -3) {
                return 1;
            }
        }
        for (int i = 0;i < 3; i++) {
            int pointSum = mTable[0][i] + mTable[1][i] + mTable[2][i];
            if (pointSum == 3) {
                return 0;
            } else if (pointSum == -3) {
                return 1;
            }
        }
        if (mTable[0][0] + mTable[1][1] + mTable[2][2] == 3) {
            return 0;
        } else if (mTable[0][0] + mTable[1][1] + mTable[2][2] == -3) {
            return 1;
        }
        if (mTable[0][2] + mTable[1][1] + mTable[2][0] == 3) {
            return 0;
        } else if (mTable[0][2] + mTable[1][1] + mTable[2][0] == -3) {
            return 1;
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
}
