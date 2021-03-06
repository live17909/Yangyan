package com.xxp.yangyan.pro.splash.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.tapadoo.alerter.Alerter;
import com.xxp.yangyan.R;
import com.xxp.yangyan.mvp.view.MvpLceView;
import com.xxp.yangyan.pro.MainActivity;
import com.xxp.yangyan.pro.base.BaseActivity;
import com.xxp.yangyan.pro.entity.SplashInfo;
import com.xxp.yangyan.pro.splash.presenter.Presenter;
import com.xxp.yangyan.pro.utils.IOUtils;
import com.xxp.yangyan.pro.utils.JudgeUtils;
import com.xxp.yangyan.pro.utils.SettingUtils;
import com.xxp.yangyan.pro.utils.UIUtils;
import com.xxp.yangyan.pro.view.CircleProgressbar;

import butterknife.BindView;
import butterknife.OnClick;


public class SplashActivity extends BaseActivity<Presenter>
        implements Animation.AnimationListener, MvpLceView<SplashInfo> {

    private static final String TAG = "SplashActivity";
    private static CircleProgressbar cpbSplash;
    @BindView(R.id.activity_splash)
    View splashView;

    //设置壁纸
    private boolean isFristOnClick = true;

    //发送消息的次数
    private int tiem = 180;
    //发送消息的延时
    private final long DELAYMILLIS = 50;


    private static final int WHAT_START_COUNT_DOWN = 0x11;

    private static final int WHAT_PAUSE_COUNT_DOWN = 0x12;

    private long exitTime;
    //Splash的背景的路径
    private String splashImgPath;
    //启动图片的存储名字
    private final String SPLASH_IMG_NAME = "bg_splash.png";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_PAUSE_COUNT_DOWN:
                    break;
                case WHAT_START_COUNT_DOWN:
                    if (tiem <= 0) {
                        goMain();
                    }
                    cpbSplash.setProgress(tiem--);
                    break;
            }
        }
    };
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startAnim();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    private void initView() {
        //全屏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cpbSplash = (CircleProgressbar) findViewById(R.id.cpbSplash);
        cpbSplash.setMaxProgress(tiem);
        cpbSplash.setProgress(tiem);
    }


    /**
     * 设置为壁纸
     */
    @OnClick({R.id.activity_splash, R.id.cpbSplash})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_splash:
                if ((System.currentTimeMillis() - exitTime) > 1000) {
                    exitTime = System.currentTimeMillis();
                } else {
                    if (isFristOnClick) {
                        isFristOnClick = false;
                        //设置壁纸
                        SettingUtils.setWallpaper(UIUtils.DrawableToBitmap(splashView.getBackground()));
                    }
                }
                break;
            case R.id.cpbSplash:
                goMain();
                break;
        }


    }


    private void initData() {
        splashImgPath = IOUtils.getSDPath() + SPLASH_IMG_NAME;
        presenter.loadData();

    }


    //开始动画
    private void startAnim() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        splashView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(this);
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    //在动画结束后,开始计时
    @Override
    public void onAnimationEnd(Animation animation) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPause) {
                    mHandler.sendEmptyMessage(WHAT_START_COUNT_DOWN);
                }
                mHandler.postDelayed(this, DELAYMILLIS);
            }
        }, DELAYMILLIS);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    //设置下载的壁纸
    private void setLocalImage() {
        //返回本地的存储的Splash的背景图片
        Drawable drawable = Drawable.createFromPath(splashImgPath);
        if (drawable != null) {
            splashView.setBackground(drawable);
        } else {
            splashView.setBackground(UIUtils.getDrawable(R.drawable.bg_splash));
        }
    }


    //到主界面
    private void goMain() {
        MainActivity.startActivity(this);
        finish();
        isPause = true;
    }


    @Override
    public void showLoading(boolean type) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable throwable) {
        //如果网络访问失败设置当前储存卡的壁纸
        setLocalImage();
        Alerter
                .create(this)
                .setBackgroundColor(R.color.colorPrimary)
                .setDuration(3000)
                .setTitle(R.string.load_splash_img_error)
                .show();
    }

    @Override
    public void showData(SplashInfo data) {
        if (JudgeUtils.isSplashImgUpdate(data.getDate())) {
            //图片跟换了
            Log.i(TAG, "showData: 下载新的启动图");
            IOUtils.downloadNewImage(splashImgPath, data.getSplashUrl());
        } else {
            setLocalImage();
        }
    }

    @Override
    protected Presenter bindPresenter() {
        return new Presenter();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPause = true;
    }
}
