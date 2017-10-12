package com.example.phone;

/**
 * Created by suhu on 2017/8/2.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class TouchImageView extends AppCompatImageView {

    /**
     * 小圆的半径
     * */
    private static final int RADIUS = 10;

    /**
     * 基础缩放比例，不能比这个更小
     * */
    private float basis_scale =1.0f;
    /**
     * 放缩后图片宽度
     * */
    private float scalingW;
    /**
     * 放缩后图片高度
     * */
    private float scalingH;

    /**
     * 经度与宽度比例
     * */
    private double ratioX;

    /**
     * 纬度与高度比例
     * */
    private double ratioY;

    /**
     *  亦庄图
     * 116.507807,39.814279
     *
     *  云狐图
     *  116.57792,39.796808
     *
     * */
    private Latitude origin = new Latitude(116.507807,39.814279);

    /**
     *  亦庄图
     * 116.671515,39.814168
     *
     *  云狐图
     *  116.588206,39.796794
     *
     *
     * */
    private Latitude point_x = new Latitude(116.671515,39.814168);

    /**
     *  亦庄图
     * 116.508095,39.749625
     *
     *  云狐图
     *  116.577947,39.792678
     *
     *
     * */
    private Latitude point_Y = new Latitude(116.508095,39.749625);

    /**
     *亦庄图
     *  锋创科技园坐标
     *  116.584559,39.785231
     *
     *  京东大厦坐标
     *  116.570042,39.792439
     *
     *  同济南路
     *  116.546385,39.77919
     *
     *  徐庄桥
     *  116.636153,39.802102
     *
     *
     *云狐图
     *  云狐时代D座
     *  116.582088,39.794569
     *
     *  云时代A座
     *  116.583072,39.796032
     *
     * */
    private Latitude test = new Latitude(116.584559,39.785231);



    /**
     * 背景图片的宽度
     * */
    private int width;

    /**
     * 背景图片的高度
     * */
    private int height;

    /**
     * 画笔
     * */
    private Paint paint;





    float x_down = 0;
    float y_down = 0;
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    boolean matrixCheck = false;

    int widthScreen;
    int heightScreen;

    Bitmap gintama,bitmap;

    private Context context;

    public TouchImageView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        matrix = new Matrix();
        paint = new Paint();
        paint.setColor(Color.RED);

        gintama = BitmapFactory.decodeResource(getResources(), R.mipmap.yizhuang);
        DisplayMetrics display = getResources().getDisplayMetrics();
        widthScreen = display.widthPixels;
        heightScreen = display.heightPixels;

        width = gintama.getWidth();
        height = gintama.getHeight();

        if (width > widthScreen || height > heightScreen) {
            float scaleW = width * 1.0f / widthScreen;
            float scaleH = height * 1.0f /heightScreen;

            //按照宽边进行放缩
            if (scaleW > scaleH) {
                basis_scale = 1/scaleW;
                matrix.postScale(basis_scale, basis_scale);
                matrix.postTranslate(0,(heightScreen-height/scaleW)/2);
            } else {
                basis_scale = 1/scaleH;
                matrix.postScale(basis_scale, basis_scale);
                //平移指的是将坐标原点平移
                matrix.postTranslate((widthScreen-width/scaleH)/2,0);
            }

        }
        //获得变化后宽高
        scalingW = width*basis_scale;
        scalingH = height*basis_scale;

        //获得比例
        measureRatio(point_x,point_Y,scalingW,scalingH);
        bitmap = gintama;

    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawBitmap(bitmap, matrix, null);
        canvas.restore();

    }


    /**
     *@method 触碰按键（手机，pad端）
     *@author suhu
     *@time 2017/10/12 13:27
     *
    */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;

                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放

                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }


    /**
     *@method 边界计算
     *@author suhu
     *@time 2017/10/12 13:26
     *
    */
    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];
        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < widthScreen / 3 || width > widthScreen * 3) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }


    /**
     *@method 触碰两点间距离
     *@author suhu
     *@time 2017/10/12 13:25
     *@param event
     *
    */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     *@method 取手势中心点
     *@author suhu
     *@time 2017/10/12 13:25
     *@param point
     *@param event
     *
    */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    /**
     *@method 取旋转角度
     *@author suhu
     *@time 2017/10/12 13:25
     *@param event
     *
    */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }



    /**
     *@method 获得XY比例
     *@author suhu
     *@time 2017/8/3 14:27
     *@param point_x ：x轴坐标点
     *@param point_y ：y轴坐标点
     *@param width ：图像宽度
     *@param height ：图像高度
     *
    */
    private void measureRatio(Latitude point_x,Latitude point_y,float width,float height){
        ratioX = width/(point_x.x-point_y.x);
        ratioY = height/(point_y.y-point_x.y);
    }

    /**
     *@method 单点坐标转换
     *@author suhu
     *@time 2017/8/3 14:38
     *@param oldPoint ：要转换的点
     *
    */
    private Latitude coordinateTransformation(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        newPoint.x =Math.abs((oldPoint.x-origin.x)*ratioX);
        newPoint.y =Math.abs((oldPoint.y-origin.y)*ratioY);
        return newPoint;
    }


    /**
     *@method 整个集合转换
     *@author suhu
     *@time 2017/8/3 14:47
     *@param list
     *
    */
    private List<Latitude> transformationList(List<Latitude> list){
        List<Latitude> newList = new ArrayList<>();
        for (Latitude point : list) {
            newList.add(coordinateTransformation(point));
        }
        return newList;
    }


    /**
     *@method 绘画单点
     *@author suhu
     *@time 2017/8/4 11:16
     *@param point
     *
    */
    public void drawPoint(Latitude point){
        Bitmap bm = Bitmap.createBitmap(width,height,Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(gintama,0,0,null);
        Latitude latitude = coordinateTransformation(point);
        canvas.drawCircle((float) latitude.x,(float) latitude.y,RADIUS,paint);
        canvas.save();
        bitmap = bm;
        invalidate();
    }



    /**
     *@method 绘画点集
     *@author suhu
     *@time 2017/8/4 11:20
     *@param pointList
     *
    */
    public void drawPoint(List<Latitude> pointList){
        Bitmap bm = Bitmap.createBitmap(width,height,Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(gintama,0,0,null);
        List<Latitude> list = transformationList(pointList);
        for (Latitude latitude : list) {
            canvas.drawCircle((float) latitude.x,(float) latitude.y,RADIUS,paint);
        }
        canvas.save();
        bitmap = bm;
        //通知
        invalidate();
    }

    


}
