package com.example.phone.activity;

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
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.phone.Latitude;
import com.example.phone.R;

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
    private static final int TEXT_SIZE = 20;

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
     *
     * 新测
     *116.57245166666668,39.788425
     * 39.788423333333334
     */
    private Latitude origin = new Latitude(116.57245166666668,39.788423333333334);
    /**
     * 亦庄图
     * 116.671515,39.814168
     * <p>
     * 云狐图
     * 116.588206,39.796794
     *
     * 新测：
     * 116.56781833333335,39.788806666666666
     */
    private Latitude pointX = new Latitude(116.56781833333335,39.788806666666666);
    /**
     * 亦庄图
     * 116.508095,39.749625
     * <p>
     * 云狐图
     * 116.577947,39.792678
     *
     * 新测：
     * Long:116.57052333333334 ,39.79045
     */
    private Latitude pointY = new Latitude(116.57052333333334 ,39.79045);
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
    private Paint textPaint;
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

    private double tanA = 0d;
    private double arcA = 0d;

    private double ratioJ = 0d;

    private int state =-1;


    private int strokeWidth;    // 边框线宽
    private int strokeColor;    // 边框颜色
    private int cornerRadius;   // 圆角半径


    private RectF textRectF;



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

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(25);

        gintama = BitmapFactory.decodeResource(getResources(), R.mipmap.ariport_3390);

        textRectF = new RectF();
        strokeWidth = 1;
        strokeColor = Color.RED;
        cornerRadius = 5;
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
        //measureRatio(pointX, pointY, scalingW, scalingH);
        //measureRatio(pointX, pointY,origin,scalingW,scalingH);
        measure0();
        measureRatioNew(pointX, pointY,origin,scalingW,scalingH);

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

    private void measureRatio(Latitude pointX, Latitude pointY,Latitude originO, float width, float height) {
        ratioX = width / (pointX.x - originO.x);
        ratioY = height / (pointY.y - originO.y);

    }

    private void measure0(){
        Latitude pB = new Latitude(116.109029,37.482775);
        //Latitude pB = new Latitude(116.109060,37.482765);
        //Latitude pB = new Latitude(116.108998,37.482785);
        Latitude pD = new Latitude(116.124648,37.500543);
        double dx = pD.x-pB.x;
        double dy = pD.y-pB.y;
        double dkm = 2360d;

        double k = dy/dx;
        double arcK = Math.atan(k);
        double dh = dkm*Math.sin(arcK);
        double ratioH = dy/dh;
        double dl = dkm*Math.cos(arcK);
        double ratioL = dx/dl;

        ratioJ = ratioH;

        double dc = Math.sqrt(dx*dx+dy*dy);

        double dBkm = 450+140;

        double ratio = dc/dkm;

        //边界点G点
        double dBy = dBkm/dkm*dy;
        double dBx = dBkm/dkm*dx;

        Latitude p1 = new Latitude();
        p1.x = pB.x-dBx;
        p1.y = pB.y-dBy;

        //原点坐标
        double d = 270;
        double k1 = -1/k;
        Latitude p2 = new Latitude();
        p2.x =p1.x- (d*Math.cos(-k1))*ratioL;
        p2.y =p1.y+(d*Math.sin(-k1))*ratioH;

        //y轴顶点坐标
        double ddy = 1216-270;
        Latitude p3 = new Latitude();
        p3.x = p1.x+(ddy*Math.cos(-k1))*ratioL;
        p3.y = p1.y-(ddy*Math.sin(-k1))*ratioH;

        //边界值Q点
        Latitude p4 = new Latitude();
        double dq = 440;
        p4.x = pD.x+dq/dkm*dx;
        p4.y = pD.y+dq/dkm*dy;

        //x轴顶点
        Latitude p5 = new Latitude();
        double ddx = 270;
        double k2 = k1;
        p5.x = p4.x-(ddx*Math.cos(-k2))*ratioL;
        p5.y = p4.y+(ddx*Math.sin(-k2))*ratioH;

        origin = p2;
        pointX = p5;
        pointY = p3;
    }

    private void measure1(){
        Latitude pB = new Latitude(116.109029,37.482775);
        //Latitude pB = new Latitude(116.109060,37.482765);
        //Latitude pB = new Latitude(116.108998,37.482785);
        Latitude pD = new Latitude(116.124648,37.500543);
        double dx = pD.x-pB.x;
        double dy = pD.y-pB.y;
        double dkm = 2412.5d;

        double k = dy/dx;
        double arcK = Math.atan(k);
        double dh = dkm*Math.sin(arcK);
        double ratioH = dy/dh;
        double dl = dkm*Math.cos(arcK);
        double ratioL = dx/dl;

        ratioJ = ratioH;

        double dc = Math.sqrt(dx*dx+dy*dy);

        double dBkm = 450+87.5;

        double ratio = dc/dkm;

        //边界点G点
        double dBy = dBkm/dkm*dy;
        double dBx = dBkm/dkm*dx;

        Latitude p1 = new Latitude();
        p1.x = pB.x-dBx;
        p1.y = pB.y-dBy;

        //原点坐标
        double d = 270;
        double k1 = -1/k;
        Latitude p2 = new Latitude();
        p2.x =p1.x- (d*Math.cos(-k1))*ratioL;
        p2.y =p1.y+(d*Math.sin(-k1))*ratioH;

        //y轴顶点坐标
        double ddy = 1216-270;
        Latitude p3 = new Latitude();
        p3.x = p1.x+(ddy*Math.cos(-k1))*ratioL;
        p3.y = p1.y-(ddy*Math.sin(-k1))*ratioH;

        //边界值Q点
        Latitude p4 = new Latitude();
        double dq = 440;
        p4.x = pD.x+dq/dkm*dx;
        p4.y = pD.y+dq/dkm*dy;

        //x轴顶点
        Latitude p5 = new Latitude();
        double ddx = 270;
        double k2 = k1;
        p5.x = p4.x-(ddx*Math.cos(-k2))*ratioL;
        p5.y = p4.y+(ddx*Math.sin(-k2))*ratioH;

        origin = p2;
        pointX = p5;
        pointY = p3;
    }



    /**
     *@method 测量基准点
     *@author suhu
     *@time 2017/11/8 16:33
     *@param
     *
    */
    private void measure(){
        Latitude pB = new Latitude(116.109029,37.482775);
        //Latitude pB = new Latitude(116.109060,37.482765);
        //Latitude pB = new Latitude(116.108998,37.482785);
        Latitude pD = new Latitude(116.124648,37.500543);
        double dx = pD.x-pB.x;
        double dy = pD.y-pB.y;
        double dkm = 2360d;

        double k = dy/dx;
        double arcK = Math.atan(k);
        double aaaa = arcK*180/Math.PI;
        double dh = dkm*Math.sin(arcK);
        double ratioH = dy/dh;
        double dl = dkm*Math.cos(arcK);
        double ratioL = dx/dl;

        ratioJ = ratioH;
        double dc = Math.sqrt(dx*dx+dy*dy);

        double dBkm = 450+140;

        double ratio = dc/dkm;

        //边界点G点
        double dBy = dBkm/dkm*dy;
        double dBx = dBkm/dkm*dx;

        Latitude p1 = new Latitude();
        p1.x = pB.x-dBx;
        p1.y = pB.y-dBy;

        //原点坐标
        double d = 280;
        double k1 = -1/k;
        Latitude p2 = new Latitude();
        p2.x =p1.x- (d*Math.cos(-k1))*ratioL;
        p2.y =p1.y+(d*Math.sin(-k1))*ratioH;

        //y轴顶点坐标
        double ddy = 1132-260-250;
        Latitude p3 = new Latitude();
        p3.x = p1.x+(ddy*Math.cos(-k1))*ratioL;
        p3.y = p1.y-(ddy*Math.sin(-k1))*ratioH;

        //边界值Q点
        Latitude p4 = new Latitude();
        double dq = 440;
        p4.x = pD.x+dq/dkm*dx;
        p4.y = pD.y+dq/dkm*dy;

        //x轴顶点
        Latitude p5 = new Latitude();
        double ddx = 260;
        double k2 = k1;
        p5.x = p4.x-(ddx*Math.cos(-k2))*ratioL;
        p5.y = p4.y+(ddx*Math.sin(-k2))*ratioH;

        origin = p2;
        pointX = p5;
        pointY = p3;
        int a = 11;
    }



    private void measureRatioNew(Latitude point_x,Latitude point_y,Latitude origin,float widthW,float heightH){
        double dx = Math.sqrt((point_x.x-origin.x)*(point_x.x-origin.x)+(point_x.y-origin.y)*(point_x.y-origin.y));
        ratioX = widthW/dx;
        double dy = Math.sqrt((point_y.x-origin.x)*(point_y.x-origin.x)+(point_y.y-origin.y)*(point_y.y-origin.y));
        ratioY = heightH/dy;
        double x = point_x.x-origin.x;
        double y = point_x.y-origin.y;

        double c = point_y.x-origin.x;
        double f = point_y.y-origin.y;

        double h = point_x.y-point_y.y;

        tanA =Math.abs ((point_x.y-origin.y)/(point_x.x-origin.x));

        if (x>0 && y<0){
            state = 1;
            arcA = Math.atan(tanA);
        }
        if (x>0 && y >0){
            state = 2;
            arcA = Math.atan(tanA);
        }
        if (x<0 && y>0){
            state =3;
            double g = Math.atan(tanA);
            arcA = Math.PI/2 - g;

        }

        if (x<0 && y<0){
            state = 4;
            double g = Math.atan(tanA);
            arcA = Math.PI/2 - g;
        }
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

    private Latitude transformation(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double l =oldPoint.x - origin.x ;
        double h =oldPoint.y - origin.y ;

        switch (state){
            case 1:
                newPoint.x =Math.abs(((h-l*(tanA))*Math.sin(arcA)+l*Math.cos(arcA))*ratioX) ;
                newPoint.y = Math.abs((h-l*(tanA))*Math.cos(arcA)*ratioY);
                break;
            case 2:

                newPoint = fun0(oldPoint);
//                newPoint.x = Math.abs((l-h*tanA)*Math.cos(arcA)*ratioX);
//                newPoint.y =Math.abs((h/(Math.cos(arcA))+(l-h*tanA)*(Math.sin(arcA)))*ratioY);
                break;
            case 3:
                newPoint = fun0(oldPoint);

                break;
            case 4:
                newPoint.x = Math.abs((h-l*tanA)*Math.cos(arcA)*ratioX);
                newPoint.y = Math.abs((l/(Math.cos(arcA))+(h-l*tanA)*Math.sin(arcA))*ratioY);

                break;
                default:
        }

        return newPoint;
    }

    /**
     *@method 第一种算法
     *@author suhu
     *@time 2017/11/7 10:26
     *@param oldPoint
     * 1.利用两条直线夹角分别计算出点与新X轴的夹角A，与新Y轴的夹角B
     * 2.x = r*cos(A)
     *   y = r*cos(B)
     *
     */
    private Latitude fun0(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double l =Math.abs((oldPoint.x - origin.x)) ;
        double h =Math.abs((oldPoint.y - origin.y)) ;
        double d = Math.sqrt((l*l+h*h));

        double kx = (pointX.y-origin.y)/(pointX.x-origin.x);
        double ky = (pointY.y-origin.y)/(pointY.x-origin.x);
        double k2 = (oldPoint.y-origin.y)/(oldPoint.x-origin.x);
        double k3 = Math.abs((kx-k2)/(1+kx*k2));
        double k4 = Math.abs((ky-k2)/(1+ky*k2));

        double arcH = Math.atan(k3);
        double arcY = Math.atan(k4);

        newPoint.x =Math.abs(d*Math.cos(arcH))*ratioX;
        newPoint.y =Math.abs(d*Math.sin(arcH))*ratioY;
        return newPoint;
    }

    /**
     *@method 第一种算法
     *@author suhu
     *@time 2017/11/7 10:26
     *@param oldPoint
     * 1.利用两条直线夹角分别计算出点与新X轴的夹角A，与新Y轴的夹角B
     * 2.x = r*cos(A)
     *   y = r*cos(B)
     *
     */
    private Latitude fun1(Latitude oldPoint){
        Latitude newPoint = new Latitude();

        double baseX = scalingW/3390;
        double baseY = scalingH/1216;

        double l =Math.abs((oldPoint.x - origin.x))/ratioJ*baseX ;
        double h =Math.abs((oldPoint.y - origin.y))/ratioJ*baseY ;
        double d = Math.sqrt((l*l+h*h));

        double kx = (pointX.y-origin.y)/(pointX.x-origin.x);
        double ky = (pointY.y-origin.y)/(pointY.x-origin.x);
        double k2 = (oldPoint.y-origin.y)/(oldPoint.x-origin.x);
        double k3 = Math.abs((kx-k2)/(1+kx*k2));
        double k4 = Math.abs((ky-k2)/(1+ky*k2));

        double arcH = Math.atan(k3);
        double arcY = Math.atan(k4);

        newPoint.x =Math.abs(d*Math.cos(arcH));
        newPoint.y =Math.abs(d*Math.sin(arcH));
        return newPoint;
    }


    /**
     *@method 第二种算法
     *@author suhu
     *@time 2017/11/7 10:26
     *@param oldPoint
     * 1.算出点与新X轴的夹角A
     * 2.x = r*cos(A)
     *   y = r*sin(A)
     *
    */
    private Latitude fun2 (Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double l =Math.abs(oldPoint.x - origin.x) ;
        double h =Math.abs(oldPoint.y - origin.y) ;
        double d = Math.sqrt((l*l+h*h));

        double tanB = l/h;
        double arcB = Math.atan(tanB);
        double arcC = arcB - arcA;

        newPoint.x = Math.abs(d*Math.cos(arcC)*ratioX);
        newPoint.y = Math.abs(d*Math.sin(arcC)*ratioY);
        return newPoint;
    }


    /**
     *@method 第三种算法
     *@author suhu
     *@time 2017/11/7 10:23
     *@param oldPoint
     * A:坐标系旋转角度
     * x = x0*cos(A)-y0*sin(A)
     * y = y0*cos(A)+x0*sin(A)
     *
    */
    private Latitude fun3(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double arcC = Math.atan2(oldPoint.y-origin.y,oldPoint.x-origin.x);
        newPoint.x =Math.abs(oldPoint.x*Math.cos(arcC)+oldPoint.y*Math.sin(arcC))*ratioX;
        newPoint.y =Math.abs (oldPoint.y*Math.cos(arcA)+oldPoint.x*Math.sin(arcC))*ratioY;
        return newPoint;
    }

    /**
     *@method 第四种算法
     *@author suhu
     *@time 2017/11/7 10:29
     *@param oldPoint
     * 利用三角函数计算对应边长
     *
    */
    private Latitude fun4(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double l =Math.abs(oldPoint.x - origin.x) ;
        double h =Math.abs(oldPoint.y - origin.y) ;

        newPoint.x = Math.abs((l-h*tanA)*Math.cos(arcA)*ratioX);
        newPoint.y =Math.abs((h/(Math.cos(arcA))+(l-h*tanA)*(Math.sin(arcA)))*ratioY);
        return newPoint;
    }


    /**
     *@method 第五种算法
     *@author suhu
     *@time 2017/11/7 10:29
     *@param oldPoint
     * 直接减，不做任何处理
     *
     */
    private Latitude fun5(Latitude oldPoint){
        Latitude newPoint = new Latitude();
        double l =Math.abs(oldPoint.x - origin.x) ;
        double h =Math.abs(oldPoint.y - origin.y) ;
        newPoint.x =Math.abs(l*ratioX);
        newPoint.y = Math.abs(h*ratioY);
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
            //newList.add(coordinateTransformation(point));
            newList.add(transformation(point));
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
        if (pointList.size()>0){
            List<Latitude> list = transformationList(pointList);
            for (Latitude latitude : list) {
                canvas.drawCircle((float) latitude.x, (float) latitude.y, RADIUS, paint);
                drawText(canvas,"驱鸟员",latitude);
            }
        }

        canvas.save();
        bitmap = bm;
        //通知重绘
        invalidate();
    }

    private void drawText(Canvas canvas,String text,Latitude latitude){

        float length = paint.measureText(text);
        float x = (float) latitude.x+10;
        float y = (float) latitude.y-10;

        textRectF.left = x-5;
        textRectF.top = y-20;
        textRectF.right = x+length+5;
        textRectF.bottom = y+5;

        canvas.drawRoundRect(textRectF, cornerRadius, cornerRadius, textPaint);
        canvas.drawText(text,x,y,paint);


    }

}
