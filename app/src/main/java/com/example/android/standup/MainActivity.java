package com.example.android.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public static final int NOTIFICATION_ID = 0;
    public static final String NOTIFICATION_CHANNEL = "notification_channel";

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggle = findViewById(R.id.alarmToggle);

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        // Check if alarm is already set
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        toggle.setChecked(alarmUp);

        final PendingIntent notifyPI = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Intent alarmClockIntent = new Intent(this, MainActivity.class);
        final PendingIntent alarmClockPI = PendingIntent.getActivity(this, NOTIFICATION_ID,
                alarmClockIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/

        // Inexact, repeating alarm
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String toastMessage;
                if (isChecked) {
                    toastMessage = getString(R.string.stand_up_alarm_on);

                    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    long triggerTime = SystemClock.elapsedRealtime();
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime,
                                repeatInterval,
                                notifyPI);

                        /*AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(
                                triggerTime, alarmClockPI);
                        alarmManager.setAlarmClock(info, notifyPI);*/
                    }
                }
                else {
                    toastMessage = getString(R.string.stand_up_alarm_off);

                    //Cancel alarm when toggle is off
                    if (alarmManager != null)
                        alarmManager.cancel(notifyPI);
                    nm.cancelAll();
                }

                // Show the toast message to say alarm is turned on or off
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });

        createNotificationChannel();

        Button nextAlarm = findViewById(R.id.next);
        nextAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (alarmManager != null) {
                    AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();

                    if (info != null) {
                        String nextAlarmInfo = "Next Alarm is in: " + info.getTriggerTime()
                                + "miliseconds";
                        Toast.makeText(MainActivity.this, nextAlarmInfo, Toast.LENGTH_LONG)
                                .show();
                    }
                }*/
            }
        });
    }

    public void createNotificationChannel() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL,
                    "Alarm",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setDescription(getString(R.string.channel_description));
            nm.createNotificationChannel(channel);
        }
    }
}
