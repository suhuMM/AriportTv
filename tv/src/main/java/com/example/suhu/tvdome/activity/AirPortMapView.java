package com.example.suhu.tvdome.activity;

/**
 * @author suhu
 * @data 2017/10/18.
 * @description 地图显示展示View
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
import android.view.MotionEvent;

import com.example.suhu.tvdome.Latitude;
import com.example.suhu.tvdome.R;

import java.util.ArrayList;
import java.util.List;

public class AirPortMapView extends AppCompatImageView {

    /**
     * 小圆的半径
     */
    private static final int RADIUS = 10;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    /**
     * 文字大小
     */
    private static final int TEXT_SIZE = 30;

    /**
     * 基础缩放比例，不能比这个更小
     */
    private float basisScale = 1.0f;
    /**
     * 放缩后图片宽度
     */
    private float scalingW;
    /**
     * 放缩后图片高度
     */
    private float scalingH;
    /**
     * 经度与宽度比例
     */
    private double ratioX;
    /**
     * 纬度与高度比例
     */
    private double ratioY;
    /**
     * 亦庄图
     * 116.507807,39.814279
     * <p>
     * 云狐图
     * 116.57792,39.796808
     */
    private Latitude origin = new Latitude(116.507807, 39.814279);
    /**
     * 亦庄图
     * 116.671515,39.814168
     * <p>
     * 云狐图
     * 116.588206,39.796794
     */
    private Latitude pointX = new Latitude(116.671515, 39.814168);
    /**
     * 亦庄图
     * 116.508095,39.749625
     * <p>
     * 云狐图
     * 116.577947,39.792678
     */
    private Latitude pointY = new Latitude(116.508095, 39.749625);
    /**
     * 亦庄图
     * 锋创科技园坐标
     * 116.584559,39.785231
     * <p>
     * 京东大厦坐标
     * 116.570042,39.792439
     * <p>
     * 同济南路
     * 116.546385,39.77919
     * <p>
     * 徐庄桥
     * 116.636153,39.802102
     * <p>
     * <p>
     * 云狐图
     * 云狐时代D座
     * 116.582088,39.794569
     * <p>
     * 云时代A座
     * 116.583072,39.796032
     */
    private Latitude test = new Latitude(116.584559, 39.785231);
    /**
     * 背景图片的宽度
     */
    private int width;
    /**
     * 背景图片的高度
     */
    private int height;
    /**
     * 画笔
     */
    private Paint paint;
    private float xDown = 0;
    private float yDown = 0;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float oldRotation = 0;
    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix savedMatrix = new Matrix();
    /**
     * 基础的矩阵
     */
    private Matrix basicsMatrix = new Matrix();
    private int mode = NONE;
    private boolean matrixCheck = false;
    private int widthScreen;
    private int heightScreen;

    private Bitmap gintama, bitmap;
    private boolean isMove = true;


    public AirPortMapView(Context context) {
        super(context);
        init();
    }

    public AirPortMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AirPortMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(TEXT_SIZE);

        gintama = BitmapFactory.decodeResource(getResources(), R.mipmap.yizhuang);

    }


    private void initView() {
        widthScreen = getWidth();
        heightScreen = getHeight();

        width = gintama.getWidth();
        height = gintama.getHeight();

        if (width > widthScreen || height > heightScreen) {
            float scaleW = width * 1.0f / widthScreen;
            float scaleH = height * 1.0f / heightScreen;

            //按照宽边进行放缩
            if (scaleW > scaleH) {
                basisScale = 1 / scaleW;
                matrix.postTranslate(0, (heightScreen - height * basisScale) / 2);
            } else {
                basisScale = 1 / scaleH;
                //平移指的是将坐标原点平移
                matrix.postTranslate((widthScreen - width * basisScale) / 2, 0);
            }
        } else {
            float scaleW = widthScreen * 1.0f / (width * 1.0f);
            float scaleH = heightScreen * 1.0f / (height * 1.0f);
            if (scaleW < scaleH) {
                basisScale = scaleW;
                matrix.postTranslate(0, (heightScreen - height * basisScale) / 2);
            } else {
                basisScale = scaleH;
                matrix.postTranslate((widthScreen - width * basisScale) / 2, 0);
            }
        }


        //获得变化后宽高
        scalingW = width * basisScale;
        scalingH = height * basisScale;

        //缩放图片大小，得到新图片
        basicsMatrix.postScale(basisScale, basisScale);
        gintama = Bitmap.createBitmap(gintama, 0, 0, width, height, basicsMatrix, true);

        //获得比例
        measureRatio(pointX, pointY, scalingW, scalingH);
        bitmap = gintama;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isMove){
            initView();
            isMove = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawBitmap(bitmap, matrix, null);
        canvas.restore();

    }


    /**
     * @method 触碰按键（手机，pad端）
     * @author suhu
     * @time 2017/10/12 13:27
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                xDown = event.getX();
                yDown = event.getY();
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
                    matrix1.postTranslate(event.getX() - xDown, event.getY()
                            - yDown);// 平移
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
            default:
        }
        return true;
    }


    /**
     * @method 边界计算
     * @author suhu
     * @time 2017/10/12 13:26
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
     * @param event
     * @method 触碰两点间距离
     * @author suhu
     * @time 2017/10/12 13:25
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * @param point
     * @param event
     * @method 取手势中心点
     * @author suhu
     * @time 2017/10/12 13:25
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    /**
     * @param event
     * @method 取旋转角度
     * @author suhu
     * @time 2017/10/12 13:25
     */
    private float rotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }


    /**
     * @param pointX ：x轴坐标点
     * @param pointY ：y轴坐标点
     * @param width   ：图像宽度
     * @param height  ：图像高度
     * @method 获得XY比例
     * @author suhu
     * @time 2017/8/3 14:27
     */
    private void measureRatio(Latitude pointX, Latitude pointY, float width, float height) {
        ratioX = width / (pointX.x - pointY.x);
        ratioY = height / (pointY.y - pointX.y);
    }

    /**
     * @param oldPoint ：要转换的点
     * @method 单点坐标转换
     * @author suhu
     * @time 2017/8/3 14:38
     */
    private Latitude coordinateTransformation(Latitude oldPoint) {
        Latitude newPoint = new Latitude();
        newPoint.x = Math.abs((oldPoint.x - origin.x) * ratioX);
        newPoint.y = Math.abs((oldPoint.y - origin.y) * ratioY);
        return newPoint;
    }


    /**
     * @param list
     * @method 整个集合转换
     * @author suhu
     * @time 2017/8/3 14:47
     */
    private List<Latitude> transformationList(List<Latitude> list) {
        List<Latitude> newList = new ArrayList<>();
        for (Latitude point : list) {
            newList.add(coordinateTransformation(point));
        }
        return newList;
    }


    /**
     * @param point
     * @method 绘画单点
     * @author suhu
     * @time 2017/8/4 11:16
     */
    public void drawPoint(Latitude point) {
        Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(gintama, 0, 0, null);
        Latitude latitude = coordinateTransformation(point);
        canvas.drawCircle((float) latitude.x, (float) latitude.y, RADIUS, paint);
        canvas.save();
        bitmap = bm;
        invalidate();
    }


    /**
     * @param pointList
     * @method 绘画点集
     * @author suhu
     * @time 2017/8/4 11:20
     */
    public void drawPoint(List<Latitude> pointList) {
        Bitmap bm = Bitmap.createBitmap(gintama.getWidth(), gintama.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(gintama, 0, 0, null);
        List<Latitude> list = transformationList(pointList);
        for (Latitude latitude : list) {
            canvas.drawCircle((float) latitude.x, (float) latitude.y, RADIUS, paint);
            String text = "消防车";
            float textSize = paint.measureText(text);
            canvas.drawText(text, (float) latitude.x - textSize / 2, (float) latitude.y - 2 * RADIUS, paint);
        }
        canvas.save();
        bitmap = bm;
        //通知重绘
        invalidate();
    }


}
