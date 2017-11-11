package maged.csi5230.edu.tictactoegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import maged.csi5230.edu.tictactoegame.utils.ActivityController;

/**
 * Created by dragonlayout on 2017/11/11.
 */

public class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityController.add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.remove(this);
    }
}
