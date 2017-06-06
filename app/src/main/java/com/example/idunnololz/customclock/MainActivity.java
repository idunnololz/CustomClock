package com.example.idunnololz.customclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long startTime;

    private int scale = 1;

    private TextView text;

    private boolean stopTimer = false;
    private Thread timerThread = new Thread() {
        @Override
        public void run() {
            super.run();

            while (!stopTimer) {
                if (stopTimer) return;

                try {
                    Thread.sleep(1000 / scale);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (stopTimer) return;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long elapsed = (System.currentTimeMillis() - startTime) * scale;
                        long secs = (elapsed / 1000) % 60;
                        long mins = (elapsed / 1000) / 60;

                        text.setText(String.format(Locale.CANADA, "%02d:%02d", mins, secs));
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        final TextView error = (TextView) findViewById(R.id.text);
        final EditText scaleEditText = (EditText) findViewById(R.id.scale);

        final Button startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.getText().equals(getString(R.string.stop))) {
                    startButton.setText(R.string.start);
                    stopTimer();
                } else {
                    startButton.setText(R.string.stop);
                    // validate scale...
                    String scaleText = scaleEditText.getText().toString();
                    try {
                        int s = Integer.valueOf(scaleText);

                        if (s <= 0) {
                            error.setText(getString(R.string.error_non_positive_scale));
                        } else {
                            error.setText("");
                            scale = s;
                            stopTimer = false;
                            startTime = System.currentTimeMillis();
                            timerThread.start();
                        }
                    } catch (Exception e) {
                        error.setText(getString(R.string.error_not_a_number));
                    }
                }
            }
        });
    }

    private void stopTimer() {
        stopTimer = true;
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopTimer();
    }
}
