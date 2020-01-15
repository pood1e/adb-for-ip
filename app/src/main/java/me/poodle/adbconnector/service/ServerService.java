package me.poodle.adbconnector.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import me.poodle.adbconnector.net.AcceptServer;

public class ServerService extends Service {

    public static final String TAG = "ForegroundService";
    private final int PID = android.os.Process.myPid();
    private ServiceConnection mConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ForegroundService is running");
        startForeground(PID, getNotification());// 正常启动前台服务
        setForeground();// 启动前台服务,并隐藏前台服务的通知
    }

    public void setForeground() {
        if (null == mConnection) {
            mConnection = new CoverServiceConnection();
        }
        this.bindService(new Intent(this, HelpService.class), mConnection,
                Service.BIND_AUTO_CREATE);
    }

    @SuppressLint("NewApi")
    private Notification getNotification() {
        String NOTIFICATION_CHANNEL_ID = "me.poodle.monitor";
        String channelName = "My Background Service";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        return notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AcceptServer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CoverServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ForegroundService: onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "ForegroundService: onServiceConnected");
            Service helpService = ((HelpService.LocalBinder) binder)
                    .getService();
            ServerService.this.startForeground(PID, getNotification());
            helpService.startForeground(PID, getNotification());
            helpService.stopForeground(true);
            ServerService.this.unbindService(mConnection);
            mConnection = null;
        }
    }
}
