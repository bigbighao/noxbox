package live.noxbox.performing;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import live.noxbox.R;
import live.noxbox.model.Profile;
import live.noxbox.state.ProfileStorage;
import live.noxbox.tools.Task;

public class PerformingActivity extends AppCompatActivity {

    private static int seconds;
    private static boolean running = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seconds = 0;
        running = false;
        setContentView(R.layout.activity_state_performing);
        ProfileStorage.listenProfile(PerformingActivity.class.getName(), new Task<Profile>() {
            @Override
            public void execute(Profile profile) {
                draw(profile);
                runTimer();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void draw(final Profile profile) {
        drawStart(profile);
        drawPrice(profile);
        drawComplete(profile);
    }


    private void drawStart(Profile profile) {
        running = true;
    }

    private void drawPrice(Profile profile) {
        ((TextView)findViewById(R.id.currency)).setText(profile.getCurrent().getPrice());
    }

    private void drawComplete(final Profile profile) {
        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.getCurrent().setTimeCompleted(System.currentTimeMillis());
                profile.getCurrent().setTimeServiceExecution(TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS));
                running = false;
            }
        });
    }


    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%d:%02d:%02d", hours, minutes, secs);
                ((TextView) findViewById(R.id.timeView)).setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }


}
