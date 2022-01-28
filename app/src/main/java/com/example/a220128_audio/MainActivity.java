package com.example.a220128_audio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private File mFile;

    // 오디오 기록에 관한 변수
    private Button mBtnRecord;
    private MediaRecorder mRecorder;
    private  boolean mIsRecording;

    // 오디오 재생에 대한 변수
    private Button mBtnPlay;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File sdcard = Environment.getExternalStorageDirectory();
        mFile = new File(sdcard.getAbsolutePath() + "/redord.3gp");

        mBtnRecord = (Button) findViewById(R.id.btnRecord);
        mBtnPlay = (Button) findViewById(R.id.btnPlay);
    }

    // 현재 화면이 종료하면 Recorder, player 객체를  메모리에서 정리한다.
    protected  void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord: //녹음 시작 or 중지
                if (!mIsRecording) { //녹음 중이 아니면 녹음을 시작
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                    } else
                        mRecorder.reset();
                    try { // 1 부분 제외하고 나머지는 모든 안드로이드 폰에서 동일함
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 1
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 1
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB); // 1
                        mRecorder.setOutputFile(mFile.getAbsolutePath());
                        mRecorder.prepare();
                        mRecorder.start();
                        mIsRecording = true;
                        mBtnRecord.setText("녹음 중지");
                        mBtnPlay.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { //녹음 중이면 녹음 중지
                    mRecorder.start();
                    mIsRecording = false;
                    mBtnRecord.setText("녹음 시작");
                    mBtnPlay.setEnabled(true);
                } break;

            case R.id.btnPlay: // 재생 시작
               if(!mFile.exists()) {
                    Toast.makeText(this, "녹음 파일 없음", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPlayer == null) {
                    mPlayer = new MediaPlayer();
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mBtnRecord.setEnabled(true);
                            mBtnPlay.setEnabled(true);
                        }
                    });
                } else {
                    mPlayer.reset();
                }

                try {
                    mPlayer.setDataSource(getApplicationContext(), Uri.fromFile(mFile));
                    mPlayer.prepare();
                    mPlayer.start();
                    mBtnRecord.setEnabled(false);
                    mBtnPlay.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                } break;
        }
    }
}