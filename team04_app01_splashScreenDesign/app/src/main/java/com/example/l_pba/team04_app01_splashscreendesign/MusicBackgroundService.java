/**
 * @author group04
 * @version 1.0
 * MusicBackgroundService, the music service
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * MusicBackgroundService for playing appintrosong
 */
public class MusicBackgroundService extends Service {
    private MediaPlayer player;

    /**
     *
     * @param intent : Intent
     * @return iBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     * @param intent : Intent
     * @param flags : int
     * @param startId : int
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //set Music to play
        player = MediaPlayer.create(this, R.raw.splash_screen_music);

        player.setLooping(false); //disable looping playing
        player.start(); //start player
        return START_STICKY;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop(); //stop player
    }
}
