package com.wbh.day42_qrcode;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zxing.library.activity.CaptureActivity;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

/**
 * CaptrueActivity:
 * 1. SurfaceView + Camera:二维码扫描界面(自定义相机的扩充)
 * 2. SurfaceView操作累SurfaceHolder以及SurfaceHolder.Callback
 * 3. Activity的生命周期:
 * <p/>
 * 备注: Camera:聚焦, 闪光灯, 震动, 音频;
 * <p/>
 * ViewfinderView:
 * 1. 所在的包com.zxing.library.view.ViewfinderView;
 * 2. 二维码扫描界面上回执的线条等图;
 * 3. 自定义View: 一般重写: 构造方法, onMeasure(), onLayout(), onDraw(), onTouchEvent();
 * <p/>
 * 备注:invalidata()刷新界面, 重新调用onDraw(), 重新绘制;
 * 重点是:onDraw():画8个角,
 * <p/>
 * <p/>
 * DecodeHandler处理二维码扫描的解码信息:
 * 重点是  private void decode(byte[] data, int width, int height) :该方法中重点是for双层循环
 */
public class MainActivity extends AppCompatActivity {


    private boolean flag = true;
    private EditText code_et;
    private ImageView code_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code_et = (EditText) findViewById(R.id.code_et);
        code_image = (ImageView) findViewById(R.id.code_image);
        int code = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (code == -1) {
            //授权失败
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            //授权成功
            flag = true;
            Log.e("Bing", "******手机授权成功: ******");
        }

    }

    //点击生成二维码
    public void generateQRCode(View view) {
        //普通二维码
//         Bitmap bmp = generateQRCode(code_et.getText().toString(),300,300);

//        //生成二维码中心填充的图
        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(),R.mipmap.ewq);
//        //生成带图的二维码
        Bitmap bmp =  GenerateQRCode(code_et.getText().toString(),400,bmp1);
        code_image.setImageBitmap(bmp);

    }
    public void scanQRCode(View view) {
        if (flag) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(MainActivity.this, "没有权限操作", Toast.LENGTH_SHORT).show();
        }
    }


    public Bitmap generateQRCode(String message, int w, int h) {

        //生成二维码
        //先根据素具的字符串信息得到BitMatrix位图矩阵
        /**
         * 参数一: 要生成的二维码的字符串信息, 文本输入
         * 参数二: 码的格式: 条形码, 二维码, 一维码;
         * 参数三: 生成位图矩阵的宽;
         * 参数四: 生成位图矩阵的高;
         * 参数五: 配置生成的二维码矩阵的信息;
         * */
//        String message = code_et.getText().toString();
        Map<EncodeHintType, Object> map = new Hashtable<>();
        //设置纠错界别(只含有重要信息)
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置编码
        map.put(EncodeHintType.CHARACTER_SET, "urf-8");
        try {
            //bit表示含有机器码0和1
            BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, w, h, map);
            Log.e("Bing", "******: ******"+message);
            //0和1转化成黑点和白点
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] value = new int[width * height];

            //控制列数
            for (int i = 0; i < width; i++) {
                //控制行数
                for (int j = 0; j < height; j++) {
                    //黑色代表有值—1，白色代表无值—0；
                    if (bitMatrix.get(i, j)) {
                        value[j + i * height] = Color.BLACK;
                    } else {
                        value[j + i * height] = Color.WHITE;
                    }
                }
            }
            /**
             * 参数1:把一位数组中的颜色值写入到bitmap
             * 参数2:写入颜色值数组中的第一个的索引index,从0开始
             * 参数3:一行的颜色个数
             * 参数4:一维数组中颜色值矩阵的宽度
             * 参数5:一维数组中颜色值矩阵的高度
             * 参数6:配置颜色基数Config
             */
            //方式1；
            Bitmap bitmap = Bitmap.createBitmap(value, 0, w, w, h, Bitmap.Config.ARGB_8888);


            //方式2；
//            //先创建一个空的Bitmap
//            /**
//             *参数1:生成bitmap的宽度
//             * 参数2:生成bitmap的高度
//             * 参数3:配置颜色基数Config
//             */
//            Bitmap bitmap = Bitmap.createBitmap(300,300, Bitmap.Config.ARGB_8888);
//            //把颜色值当作像素点绘制在上次的空的Bitmap
//            /**
//             * 参数1:把一位数组中的颜色值写入到bitmap
//             * 参数2:写入颜色值数组中的第一个的索引index,从0开始
//             * 参数3:一行的颜色个数
//             * 参数4:写入bitmap得x起点位置
//             * 参数5:写入bitmap得y起点位置
//             * 参数6:一维数组中颜色值矩阵的宽度
//             * 参数7:一维数组中颜色值矩阵的高度
//             */
//            bitmap.setPixels(value,0,w,0,0,300,300);

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * 生成中心带图片的二维码
     * @param text：需要生成二维码的文字信息
     * @param widthAndHeight：生成二维码的宽和高
     * @param mBitmap：二维码中心的图片
     * @return 返回中心带图片的二维码的bitmap
     */
    public Bitmap GenerateQRCode(String text, int widthAndHeight, Bitmap mBitmap) {
        //中心图的宽高
        int IMAGE_HALFWIDTH = 40;
        Matrix m = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getHeight();
        m.setScale(sx, sy);//设置缩放信息
        //将logo图片按martix设置的信息缩放
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight(), m, false);


        //imageView.setBackgroundDrawable(new BitmapDrawable(mBitmap));
        Hashtable<EncodeHintType, Object> hashtable = new Hashtable<>();
        hashtable.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hashtable.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(code_et.getText().toString(), BarcodeFormat.QR_CODE,
                            widthAndHeight, widthAndHeight);
            int[] pixles = new int[widthAndHeight * widthAndHeight];
            int halfW = widthAndHeight / 2;
            int halfH = widthAndHeight / 2;
            //控制行数
            for (int i = 0; i < widthAndHeight; i++) {
                //控制列数
                for (int j = 0; j < widthAndHeight; j++) {
                    if (i > halfW - IMAGE_HALFWIDTH && i < halfW + IMAGE_HALFWIDTH
                            && j > halfH - IMAGE_HALFWIDTH
                            && j < halfH + IMAGE_HALFWIDTH) {
                        //该位置用于存放图片信息
                        //记录图片每个像素信息
                        pixles[i * widthAndHeight + j] = mBitmap.getPixel
                                (j - halfH + IMAGE_HALFWIDTH,
                                        i - halfW + IMAGE_HALFWIDTH);
                    } else {
                        if (bitMatrix.get(i, j)) {//如果有黑块点，记录信息
                            pixles[i * widthAndHeight + j] = Color.BLACK;//记录黑块信息
                        }
                    }
                }
            }
            Bitmap bmp1 = Bitmap.createBitmap(widthAndHeight, widthAndHeight, Bitmap.Config.ARGB_8888);
            bmp1.setPixels(pixles, 0, widthAndHeight, 0, 0, widthAndHeight, widthAndHeight);
            return bmp1;

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (100 == requestCode) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    Log.e("Bing", "******扫描结果: ******" + result);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (101 == requestCode) {
            if (grantResults[0] == 0) {
                //授权成功
                flag = true;
            } else {
                //授权失败
                flag = false;
            }
        }
    }

}
