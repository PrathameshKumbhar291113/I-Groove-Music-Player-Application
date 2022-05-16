package com.example.igroove;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    AppCompatButton buttonPlay  , buttonNext , buttonPrevious;
    TextView textSongName ;
    SeekBar seekMusicBar;
    ImageView imageView;
    String songName;
    public static final String EXTRA_NAME = "com.example.igroove.songName";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        getSupportActionBar().hide();

        buttonPrevious = findViewById(R.id.previousButton);
        buttonNext = findViewById(R.id.nextButton);
        buttonPlay = findViewById(R.id.playButton);

        textSongName = findViewById(R.id.textSong);

        seekMusicBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.musicPlayerImage);

        if(mediaPlayer !=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songList");
        String sName = intent.getStringExtra("songName");
        position = intent.getIntExtra("position" ,0);
        textSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        textSongName.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusicBar.setProgress(currentPosition);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
        seekMusicBar.getThumb().setColorFilter(getResources().getColor(R.color.green),PorterDuff.Mode.SRC_IN);

        seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,delay);
            }
        },delay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(mediaPlayer.isPlaying()){
                     buttonPlay.setBackgroundResource(R.drawable.ic_play);
                     mediaPlayer.pause();

                     TranslateAnimation moveAnimation = new TranslateAnimation(-25,25,-25,25);
                     moveAnimation.setInterpolator(new AccelerateInterpolator());
                     moveAnimation.setDuration(500);
                     moveAnimation.setFillEnabled(true);
                     moveAnimation.setFillAfter(true);
                     moveAnimation.setRepeatMode(Animation.REVERSE);
                     moveAnimation.setRepeatCount(1);
                     imageView.startAnimation(moveAnimation);
                 }else{
                     buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                     mediaPlayer.start();

                     TranslateAnimation moveAnimation = new TranslateAnimation(-25,25,-25,25);
                     moveAnimation.setInterpolator(new AccelerateInterpolator());
                     moveAnimation.setDuration(500);
                     moveAnimation.setFillEnabled(true);
                     moveAnimation.setFillAfter(true);
                     moveAnimation.setRepeatMode(Animation.REVERSE);
                     moveAnimation.setRepeatCount(1);
                     imageView.startAnimation(moveAnimation);
                 }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                buttonNext.performClick();
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =((position+1)%mySongs.size());
                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(MusicPlayerActivity.this,uri);
                songName = mySongs.get(position).getName();
                textSongName.setText(songName);
                mediaPlayer.start();
                buttonPlay.setBackgroundResource(R.drawable.ic_pause);

                startAnimation(imageView,360f);
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):position-1;
                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(MusicPlayerActivity.this,uri);
                songName = mySongs.get(position).getName();
                textSongName.setText(songName);
                mediaPlayer.start();
                buttonPlay.setBackgroundResource(R.drawable.ic_pause);

                startAnimation(imageView,-360f);
            }
        });
    }
    public void startAnimation(View view ,Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,degree);
        objectAnimator.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time ="";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time + min+":";
        if(sec<10){
            time= time + "0";
        }
        time = time + sec;
        return time;
    }
}