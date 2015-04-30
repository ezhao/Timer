package com.herokuapp.ezhao.timer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.todddavies.components.progressbar.ProgressWheel;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.pwHabitProgress) ProgressWheel pwHabitProgress;
    private enum SpinStatus {
        STOPPED, INDEFINITE, GOAL_SET
    }
    SpinStatus spinStatus;
    long startTime;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        spinStatus = SpinStatus.STOPPED;
        pwHabitProgress.setProgress(0);
    }

    @OnClick(R.id.pwHabitProgress)
    public void onWheelTap(View view) {
        final ProgressWheel progressWheel = (ProgressWheel) view;

        if (spinStatus == SpinStatus.INDEFINITE) {
            spinStatus = SpinStatus.STOPPED;
            pwHabitProgress.stopSpinning();
            if (timer != null) {
                timer.cancel();
            }
        } else {
            spinStatus = SpinStatus.INDEFINITE;
            pwHabitProgress.spin();
            startTime = System.currentTimeMillis();

            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int diff = (int) (System.currentTimeMillis() - startTime);
                    int hundredths = (diff / 10) % 100;
                    int seconds = (diff / 1000) % 60;
                    int minutes = (diff/ (1000 * 60)) % 60;
                    progressWheel.setText(String.format("%02d:%02d:%02d", minutes, seconds, hundredths));
                }
            }, 0, 9);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
