package com.example.group_2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundMusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
        } else {
            Log.e("MusicService", "Error creating MediaPlayer. Check R.raw.background exists and is correctly named.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MusicService", "Service onStartCommand");
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Log.d("MusicService", "Music started");
            }
        } else {
            Log.e("MusicService", "MediaPlayer is null in onStartCommand, could not start music.");
        }
        return START_STICKY; // Giúp Service được khởi động lại nếu bị hệ thống kill
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MusicService", "Service onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("MusicService", "Music stopped and MediaPlayer released");
        }
    }
}
