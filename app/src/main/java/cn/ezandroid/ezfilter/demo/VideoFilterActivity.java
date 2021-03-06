package cn.ezandroid.ezfilter.demo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import cn.ezandroid.ezfilter.EZFilter;
import cn.ezandroid.ezfilter.core.RenderPipeline;
import cn.ezandroid.ezfilter.core.output.BitmapOutput;
import cn.ezandroid.ezfilter.demo.render.BWRender;
import cn.ezandroid.ezfilter.environment.SurfaceFitView;
import cn.ezandroid.ezfilter.video.VideoInput;
import cn.ezandroid.ezfilter.video.player.IMediaPlayer;

/**
 * VideoFilterActivity
 *
 * @author like
 * @date 2017-09-16
 */
public class VideoFilterActivity extends BaseActivity {

    private SurfaceFitView mRenderView;
    private ImageView mPreviewImage;

    private Uri uri1;
    private Uri uri2;

    private Uri mCurrentUri;

    private RenderPipeline mRenderPipeline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_filter);
        mRenderView = $(R.id.render_view);
        mPreviewImage = $(R.id.preview_image);

//        uri1 = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        uri2 = Uri.parse("http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4");
        uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
//        uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test2);

        changeVideo();

        $(R.id.change_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVideo();
            }
        });

        $(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRenderPipeline.output(new BitmapOutput.BitmapOutputCallback() {
                    @Override
                    public void bitmapOutput(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPreviewImage.setImageBitmap(bitmap);
                            }
                        });
                    }
                }, true);
            }
        });
    }

    private void changeVideo() {
        if (mCurrentUri == uri1) {
            mCurrentUri = uri2;
        } else {
            mCurrentUri = uri1;
        }

        new Thread() {
            public void run() {
                mRenderPipeline = EZFilter.input(mCurrentUri)
                        .setLoop(false)
                        .addFilter(new BWRender(VideoFilterActivity.this))
                        .setPreparedListener(new IMediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(IMediaPlayer var1) {
                                Log.e("VideoFilterActivity", "onPrepared");
                            }
                        })
                        .setCompletionListener(new IMediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(IMediaPlayer var1) {
                                Log.e("VideoFilterActivity", "onCompletion");
                            }
                        })
                        .into(mRenderView);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRenderPipeline != null) {
            ((VideoInput) mRenderPipeline.getStartPointRender()).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRenderPipeline != null) {
            ((VideoInput) mRenderPipeline.getStartPointRender()).pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
