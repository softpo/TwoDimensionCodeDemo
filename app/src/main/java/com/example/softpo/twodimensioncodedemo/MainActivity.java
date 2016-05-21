package com.example.softpo.twodimensioncodedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dtr.zxing.activity.CaptureActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnScan(View view) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE==requestCode&&resultCode==RESULT_OK){
            if (data != null) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    String result = (String) extras.get("result");
                    Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\s]*");

                    Matcher matcher = pattern.matcher(result);

                    boolean b = matcher.find();

                    if(b){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(result));
                        startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this,"result: "+result,Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    public void btnGenerate(View view) {
        String data = ((EditText) findViewById(R.id.input)).getText().toString().trim();
        if (data != null) {
            Bitmap qrCode = createQRCode(data, 300, 300);
            if (qrCode != null) {
                ((ImageView)findViewById(R.id.showImage)).setImageBitmap(qrCode);
            }else {
                ((ImageView)findViewById(R.id.showImage)).setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
    private Bitmap createQRCode(String content, int width, int height) {

        Bitmap bitmap = null;
        //1、实例化生成二维码的类
        QRCodeWriter writer = new QRCodeWriter();

        //2、指定字符集
        HashMap<EncodeHintType,String> map = new HashMap<EncodeHintType, String>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        try {
            //3、使用writer创建一个矩阵
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, map);
            //4、实例化一个int类型数组，接收矩阵中数据
            int[] pixels = new int[width*height];

            //5、将矩阵中数据写到数组中
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    if(matrix.get(x,y)){//代表矩阵中有数据
                        pixels[x*width+y] =0xFF0000FF;//代表蓝色
                    }else {
                        //8个F，前两个带透明度，第二组红色，第三组绿色，第四组蓝色
                        pixels[x*width+y] = 0xFFFFFFFF;//代表白色
                    }
                }
            }
            //6、生成一个空的Bitmap
            bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);

            //7、创建二维码
            /**
             * 参数1:写到位图中的颜色值
             * 参数2:从一维数组pixels中读取的第一个颜色值的索引
             * 参数3:位图的宽度
             * 参数4:被写入位图中第一个像素的X坐标
             * 参数5:被写入位图中第一个像素的Y坐标
             * 参数6:从pixels[]中拷贝的每行的颜色个数
             * 参数7：写入到位图中的行数
             */
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return  bitmap;

    }

    public void btnGenerateBitmapQr(View view) {
        Bitmap qrCode = createQRCode("你好，宇宙", 300, 300);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.p16);
        Bitmap bitmap1 = addLogo(qrCode, bitmap);

        ((ImageView)findViewById(R.id.showImage)).setImageBitmap(bitmap1);
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
//        如果图片比较大，还要继续缩小，不然二维码无法识别
        float scaleFactor = srcWidth * 1.0f / 10 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }
}
