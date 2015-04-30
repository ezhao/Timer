package com.herokuapp.ezhao.timer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment.HmsPickerDialogHandler;
import com.todddavies.components.progressbar.ProgressWheel;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity implements HmsPickerDialogHandler {
    @InjectView(R.id.pwHabitProgress) ProgressWheel pwHabitProgress;
    @InjectView(R.id.tvGoalTime) TextView tvGoalTime;
    private enum SpinStatus {
        STOPPED, INDEFINITE, GOAL_SET
    }
    SpinStatus spinStatus;
    long startTime;
    int currentDiff;
    long goalTime;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        getSupportActionBar().hide();
        spinStatus = SpinStatus.STOPPED;
        pwHabitProgress.setProgress(0);
    }

    @OnClick(R.id.pwHabitProgress)
    public void onWheelTap() {
        if (spinStatus == SpinStatus.INDEFINITE) {
            spinStatus = SpinStatus.STOPPED;
            this.pwHabitProgress.stopSpinning();
            if (timer != null) {
                timer.cancel();
            }
        } else if (spinStatus == SpinStatus.GOAL_SET) {
            spinStatus = SpinStatus.STOPPED;
            if (timer != null) {
                timer.cancel();
            }
        } else {
            startTime = System.currentTimeMillis();
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();

            if (goalTime > 0) {
                spinStatus = SpinStatus.GOAL_SET;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        currentDiff = (int) (System.currentTimeMillis() - startTime);
                        pwHabitProgress.setProgress((int) (360*currentDiff/goalTime));
                        pwHabitProgress.setText(getTimeString(currentDiff));
                    }
                }, 0, 15);
            } else {
                spinStatus = SpinStatus.INDEFINITE;
                this.pwHabitProgress.spin();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        currentDiff = (int) (System.currentTimeMillis() - startTime);
                        pwHabitProgress.setText(getTimeString(currentDiff));
                    }
                }, 0, 9);
            }
        }
    }

    @OnClick(R.id.tvGoalTime)
    public void onGoalTimeTap() {
        HmsPickerBuilder hpb = new HmsPickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment);
        hpb.show();
    }

    @OnClick(R.id.btnSetGoal)
    public void onSetGoalTap() {
        goalTime = currentDiff;
        tvGoalTime.setText(getTimeString(currentDiff));
    }

    private String getTimeString(int diff) {
        int hundredths = (diff / 10) % 100;
        int seconds = (diff / 1000) % 60;
        int minutes = (diff/ (1000 * 60)) % 60;
        return String.format("%02d:%02d:%02d", minutes, seconds, hundredths);
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

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        if (hours == 0) {
            tvGoalTime.setText(String.format("%02d:%02d:00", minutes, seconds));
        } else {
            tvGoalTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }
        goalTime = 1000 * (((hours * 60) + minutes) * 60 + seconds);
    }
}
