package com.example.smarthomegesture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {
    private Map<Integer, String> map;
    private int iGestureNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        iGestureNo = getIntent().getIntExtra("GestureNo", 0);
        Button btnPractice = (Button) findViewById(R.id.practice_button);
        Button btnReplay = (Button) findViewById(R.id.replay_button);
        createMap();

        // initiate a Video view
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        int resId = this.getResources().getIdentifier(map.get(iGestureNo), "raw", this.getPackageName());
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resId));
        videoView.start();


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(getApplicationContext(), "Press replay button to replay the video", Toast.LENGTH_SHORT).show();
            }
        });

        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Replaying the video", Toast.LENGTH_SHORT).show();
                videoView.start();
            }
        });

        btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PracticeActivity.class);
                intent.putExtra("GestureNo", iGestureNo);
                startActivity(intent);
            }
        });
    }

    private  void createMap(){
        map = new HashMap<Integer, String>();

        map.put(1, "light_on");
        map.put(2, "light_off");
        map.put(3, "fan_on");
        map.put(4, "fan_off");
        map.put(5, "increase_fan_speed");
        map.put(6, "decrease_fan_speed");
        map.put(7, "set_thermo");
        map.put(8, "num_0");
        map.put(9, "num_1");
        map.put(10, "num_2");
        map.put(11, "num_3");
        map.put(12, "num_4");
        map.put(13, "num_5");
        map.put(14, "num_6");
        map.put(15, "num_7");
        map.put(16, "num_8");
        map.put(17, "num_9");
    }

}