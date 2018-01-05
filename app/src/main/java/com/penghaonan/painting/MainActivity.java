package com.penghaonan.painting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;

import com.penghaonan.appframework.utils.BitmapUtils;
import com.penghaonan.appframework.utils.CollectionUtils;
import com.penghaonan.appframework.utils.CommonUtils;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.painting.base.BaseActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_CHOOSE = 1000;

    boolean first = true;

    @BindView(R.id.outline)
    DrawOutlineView drawOutlineView;

    private Bitmap sobelBm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        CommonUtils.checkPermission(this, Utils.getPermissions());
        Bitmap paintBm = BitmapUtils.getRatioBitmap(R.drawable.paint, 10, 20);
        drawOutlineView.setPaintBm(paintBm);
    }

    //根据Bitmap信息，获取每个位置的像素点是否需要绘制
    //使用boolean数组而不是int[][]主要是考虑到内存的消耗
    private boolean[][] getArray(Bitmap bitmap) {
        boolean[][] b = new boolean[bitmap.getWidth()][bitmap.getHeight()];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                if (bitmap.getPixel(i, j) != Color.WHITE)
                    b[i][j] = true;
                else
                    b[i][j] = false;
            }
        }
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (first) {
            first = false;
            drawOutlineView.beginDraw(getArray(sobelBm));
        } else
            drawOutlineView.reDraw(getArray(sobelBm));
        return true;
    }

    @OnClick(R.id.bt_pick)
    void onPickClick() {
        Matisse.from(this).choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .maxSelectable(1).imageEngine(new GlideEngine()).forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> result = Matisse.obtainResult(data);
            if (CollectionUtils.size(result) == 0) {
                return;
            }
            final Uri uri = result.get(0);
            Observable.fromCallable(new Callable<Bitmap>() {
                @Override
                public Bitmap call() throws Exception {
                    Bitmap originBitmap = BitmapUtils.loadBitmap(uri);
                    Bitmap bitmap = sobelBitmap(originBitmap);
                    return bitmap;
                }
            })
                    .observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Logger.i("onSubscribe");
                        }

                        @Override
                        public void onNext(Bitmap o) {
                            Logger.i("onNext");
                            first = false;
                            drawOutlineView.beginDraw(getArray(o));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.i("onError");
                        }

                        @Override
                        public void onComplete() {
                            Logger.i("onComplete");
                        }
                    });
        }
    }

    private Bitmap sobelBitmap(Bitmap bitmap) {

        //将Bitmap压缩处理，防止OOM
        Bitmap bm = BitmapUtils.getRatioBitmap(R.drawable.test, 100, 100);
        //返回的是处理过的Bitmap
        sobelBm = SobelUtils.Sobel(bm);
        return sobelBm;
    }
}