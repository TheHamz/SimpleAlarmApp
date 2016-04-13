package com.example.sanja.simplealarmapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class MainActivity extends FragmentActivity implements TimePickerDialog.OnTimeSetListener {
    int hour, minutes;
    TextView textView;
    Switch singleAlarm, repeatingAlarm;
    DataBaseHelper db = new DataBaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleAlarm = (Switch) findViewById(R.id.switch1);
        repeatingAlarm = (Switch) findViewById(R.id.switch2);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(db.getAlarm().getHour() + ":" + db.getAlarm().getMinute());

        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", Activity.MODE_PRIVATE);
        singleAlarm.setChecked(sharedPreferences.getBoolean("Switch1", false));
        repeatingAlarm.setChecked(sharedPreferences.getBoolean("Switch2", false));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boolean checked = bundle.getBoolean("SwitchState");
            singleAlarm.setChecked(checked);
            switchOneState(checked);
        }

        singleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    setSingleAlarm(alarmTriggerTimer());
                    addNotification(getApplicationContext(), (String) textView.getText());
                    switchOneState(true);
                } else {
                    cancelSingleAlarm();
                    cancelNotification(getApplicationContext());
                    switchOneState(false);
                }
            }
        });

        repeatingAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    setRepeatingAlarm(alarmTriggerTimer());
                    addNotification(getApplicationContext(), (String) textView.getText());
                    switchTwoState(true);
                } else {
                    cancelRepeatingAlarm();
                    cancelNotification(getApplicationContext());
                    switchTwoState(false);
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getFragmentManager(), "TimePicker");
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", Activity.MODE_PRIVATE);
        singleAlarm.setChecked(sharedPreferences.getBoolean("Switch1", false));
        repeatingAlarm.setChecked(sharedPreferences.getBoolean("Switch2", false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", Activity.MODE_PRIVATE);
        singleAlarm.setChecked(sharedPreferences.getBoolean("Switch1", false));
        repeatingAlarm.setChecked(sharedPreferences.getBoolean("Switch2", false));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour = hourOfDay;
        minutes = minute;
        singleAlarm.setChecked(false);
        repeatingAlarm.setChecked(false);
        Alarm alarm = new Alarm(hourOfDay, minute);
        db.updateAlarm(alarm);
        String a = String.valueOf(db.getAlarm().getHour());
        String b = String.valueOf(db.getAlarm().getMinute());
        if (db.getAlarm().getHour() < 10) {
            a = "0" + String.valueOf(db.getAlarm().getHour());
        }
        if (db.getAlarm().getMinute() < 10) {
            b = "0" + String.valueOf(db.getAlarm().getMinute());
        }
        textView.setText(a + ":" + b);
    }

    public void setRepeatingAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("Time", alarmTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 67, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    public void setSingleAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SingleAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    public long alarmTriggerTimer() {
        DataBaseHelper helper = new DataBaseHelper(this);
        Alarm alarm = helper.getAlarm();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DATE, 1);
        }
        return calendar.getTimeInMillis();
    }

    public void cancelRepeatingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 33, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void cancelSingleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SingleAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void addNotification(Context context, String time) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 24, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_build_black_24dp)
                .setContentTitle("Alarm time")
                .setContentText(time)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        notificationManager.notify(34, builder.build());
    }

    public void cancelNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    public void switchOneState(boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Switch1", state);
        editor.commit();
    }

    public void switchTwoState(boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Switch2", state);
        editor.commit();
    }
}
