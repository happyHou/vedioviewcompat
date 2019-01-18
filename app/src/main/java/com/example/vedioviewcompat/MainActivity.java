package com.example.vedioviewcompat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.vedioviewcompat.MediaController.onClickIsFullScreenListener;

import java.util.HashMap;


public class MainActivity extends Activity implements onClickIsFullScreenListener, MediaPlayer.OnPreparedListener, View.OnClickListener, MediaPlayer.OnInfoListener {
    private static final String TAG = "MainActivity";

    private MediaController mController;
    private VideoView viv;
    private ProgressBar progressBar;
    private RelativeLayout rlDD;
    private RelativeLayout rlTop;
    private ImageView preView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        viv = (VideoView) findViewById(R.id.videoView);
        rlDD = (RelativeLayout) findViewById(R.id.rl_dd);
        preView=(ImageView)findViewById(R.id.preview);
        rlTop = (RelativeLayout) findViewById(R.id.toprl);
//		progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        mController = new MediaController(this);
        mController.setClickIsFullScreenListener(this);

        viv.setMediaController(mController);
//		progressBar.setVisibility(View.VISIBLE);
//		viv.setVideoURI(Uri.parse("android.resource://" + getPackageName()
//		+ "/" + R.raw.apple));
        String url = "https://file.bte.top/goods/video/201812067449944082.mp4";
        viv.setVideoURI(Uri.parse(url));
        viv.requestFocus();
        viv.setOnPreparedListener(this);
        viv.setOnInfoListener(this);
        setBackGround(url);
    }


    private void setBackGround(String url) {
        Bitmap bitmap=null;
        try {
             bitmap = retriveVideoFrameFromVideo(url);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (bitmap!=null) {
            preView.setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));

        }

    }


    @Override
    public void setOnClickIsFullScreen() {
        // TODO Auto-generated method stub
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//设置RelativeLayout的全屏模式
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("info", "横屏");
            rlTop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viv.setFullScreen();
            enterFullScreen();
        } else {
            Log.e("info", "竖屏");
            rlTop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenDensityUtil.dip2px(this, 210)));
            viv.exitFullScreen();
            exitFullScreen();
        }
        super.onConfigurationChanged(newConfig);
        viv.refreshDrawableState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void enterFullScreen() {
        //todo api<16 未测试
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyCode) {
           Log.e(TAG, "onKeyDown: " + keyCode.getKeyCode());
        if (keyCode.getKeyCode() == KeyEvent.KEYCODE_BACK && keyCode.getAction()==KeyEvent.ACTION_UP) {
            int orientation = getRequestedOrientation();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            } else {
                finish();
                return true;

            }
        }
        return super.dispatchKeyEvent(keyCode);
    }

    public void exitFullScreen() {
        //todo api<16 未测试
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }

    /**
     * 视频url缩略图
     *
     * @param videoPath 网络地址
     */
    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        Log.e(TAG, "onPrepared: " );
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.player:
//                if (viv.isPlaying()) {
//                    player.setText("暂停");
//                    viv.pause();
//                }else {
//                    viv.start();
//                    player.setVisibility(View.GONE);
//                }
//                break;
//        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(TAG, "onInfo: " );
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // Here the video starts
            Log.e(TAG, "onInfo: "+ "开始播放");
            preView.setVisibility(View.GONE);
            return true;
        }

        return false;
    }
}
