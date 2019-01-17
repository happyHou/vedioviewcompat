package com.example.vedioviewcompat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.vedioviewcompat.MediaController.MediaPlayerControl;
import com.example.vedioviewcompat.MediaController.onClickIsFullScreenListener;

import java.util.HashMap;


public class MainActivity extends Activity implements onClickIsFullScreenListener {

    private MediaController mController;
    private boolean fullscreen = false;
    private VideoView viv;
    private ProgressBar progressBar;
    private RelativeLayout rlDD;
    private RelativeLayout rlTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        viv = (VideoView) findViewById(R.id.videoView);
        rlDD = (RelativeLayout) findViewById(R.id.rl_dd);
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
        viv.start();
        vvv(url);
    }


    private void vvv(String url) {
        Bitmap bitmap=null;
        try {
             bitmap = retriveVideoFrameFromVideo(url);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (bitmap!=null) {
            viv.setBackgroundDrawable(new BitmapDrawable(bitmap));

        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickIsFullScreen() {
        // TODO Auto-generated method stub
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//ÉèÖÃRelativeLayoutµÄÈ«ÆÁÄ£Ê½
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("info", "ºáÆÁ");
//			rlDD.setVisibility(View.GONE);
            rlTop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            Log.e("info", "ÊúÆÁ");
//			rlDD.setVisibility(View.VISIBLE);
//			rlTop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rlTop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenDensityUtil.dip2px(this, 210)));

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

    /**
     * ÊÓÆµurlËõÂÔÍ¼
     * @param videoPath ÍøÂçµØÖ·
     * @return
     * @throws Throwable
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
}
