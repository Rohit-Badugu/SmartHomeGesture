package com.example.smarthomegesture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PracticeActivity extends AppCompatActivity {

    private int PERMISSIONS_ALL = 1;
    private Uri fileUri;
    private static final int REQUEST_ID = 1;
    private static final int VIDEO_TIME = 5;
    public static final String MYPREF = "practice_number_pref";
    private static final String USER_LNAME = "Badugu";
    String practiceFile_GestureName;
    String practiceFile_fullName;
    private Map<Integer, String> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        int iGestureNo = getIntent().getIntExtra("GestureNo", 0);

        getPermissions();
        createMap();
        setPracticeFileName(iGestureNo);

        Button btnUpload = (Button) findViewById(R.id.upload_button);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Upload video to flask server
                uploadToServer();
            }
        });

        startRecording();
    }

    private void getPermissions(){
        //Request permission to access Camera
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        //Request permission to write
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSIONS_ALL);
    }

    private void setPracticeFileName(int iGestureNo){
        practiceFile_GestureName = map.get(iGestureNo);
    }

    private int getPracticeNumber(String practiceFile_gestureName) {
        SharedPreferences pref = getSharedPreferences(MYPREF,MODE_PRIVATE);

        int pracNum=0;

        if(!pref.contains(practiceFile_gestureName)) {
            pracNum = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(practiceFile_gestureName, pracNum);
            editor.apply();
        }
        else if(pref.contains(practiceFile_gestureName)) {
            pracNum = pref.getInt(practiceFile_gestureName,0)+1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(practiceFile_gestureName, pracNum);
            editor.apply();
        }

        return pracNum;
    }

    private void startRecording(){
        practiceFile_fullName = practiceFile_GestureName + "_PRACTICE_" + getPracticeNumber(practiceFile_GestureName) + "_" + USER_LNAME + ".mp4";
        File fileVideo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + practiceFile_fullName);

        Intent record = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        record.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_TIME);

        Uri vvURI_Practice = FileProvider.getUriForFile(getApplicationContext(),
                getPackageName() + ".provider", fileVideo);
        record.putExtra(MediaStore.EXTRA_OUTPUT, vvURI_Practice);

        startActivityForResult(record, REQUEST_ID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID && resultCode == RESULT_OK) {
            Toast.makeText(this, "Video saved at loc: /Internal Storage/" + practiceFile_fullName,Toast.LENGTH_LONG).show();
        }
    }

    public void uploadToServer() {
        File videoPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        File sourceFile = new File(videoPath, practiceFile_fullName);

        MultipartBody.Builder builder=new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("myfile",sourceFile.getName().toString(), RequestBody.create(MediaType.parse("/"),sourceFile));
        MultipartBody multipartBody = builder.build();

        String urlStr = "http://" + "192.168.1.97" + ":" + 5000 + "/api/upload";
        Request request = new Request
                .Builder()
                .post(multipartBody)
                .url(urlStr)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getApplicationContext() ,response.body().string(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private  void createMap(){
        map = new HashMap<Integer, String>();

        map.put(1, "LightOn");
        map.put(2, "LightOff");
        map.put(3, "FanoOn");
        map.put(4, "FanOff");
        map.put(5, "FanUp");
        map.put(6, "FanDown");
        map.put(7, "SetThermo");
        map.put(8, "Num0");
        map.put(9, "Num1");
        map.put(10, "Num2");
        map.put(11, "Num3");
        map.put(12, "Num4");
        map.put(13, "Num5");
        map.put(14, "Num6");
        map.put(15, "Num7");
        map.put(16, "Num8");
        map.put(17, "Num9");
    }
}