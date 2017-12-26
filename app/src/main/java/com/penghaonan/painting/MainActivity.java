package com.penghaonan.painting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.penghaonan.appframework.utils.BitmapUtils;
import com.penghaonan.painting.base.BaseActivity;

public class MainActivity extends BaseActivity {
    boolean first = true;
    private DrawOutlineView drawOutlineView;
    private Bitmap sobelBm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawOutlineView = (DrawOutlineView) findViewById(R.id.outline);

        //将Bitmap压缩处理，防止OOM
        Bitmap bm = BitmapUtils.getRatioBitmap(this, R.drawable.test, 100, 100);
        //返回的是处理过的Bitmap
        sobelBm = SobelUtils.Sobel(bm);

        Bitmap paintBm = BitmapUtils.getRatioBitmap(this, R.drawable.paint, 10, 20);
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
}