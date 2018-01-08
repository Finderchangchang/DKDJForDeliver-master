package liuliu.dkdjfordeliver.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import net.tsz.afinal.view.TitleBar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/16.
 */

public class CameraActivity extends BaseActivity {
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.iv3)
    ImageView iv3;
    @Bind(R.id.title_bar)
    TitleBar title_bar;
    @Bind(R.id.bottom_btn)
    Button bottomBtn;
    String orderid;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_camera);
        ButterKnife.bind(this);
        title_bar.setLeftClick(() -> finish());
        bottomBtn.setOnClickListener(view -> finish());
        orderid = getIntent().getStringExtra("orderid");
    }

    @Override
    public void initEvents() {
        iv1.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 3));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            Map<String, String> map = new HashMap<>();
            map.put("ext", "jpg");
            map.put("type", requestCode + "");
            map.put("orderid", orderid);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            Observable.create(subscriber -> {
                subscriber.onNext(communication01("http://www.dakedaojia.com/APP/Android/Deliver/androidupload.ashx", map, baos.toByteArray()));
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(str -> ToastShort("提交成功"), error -> {
                        String s = "";
                    });

            switch (requestCode) {
                case 3:
                    iv1.setImageBitmap(bitmap);// 将图片显示在ImageView里
                    iv2.setImageResource(R.mipmap.camera_click);// 将图片显示在ImageView里
                    iv1.setOnClickListener(v -> {
                    });
                    iv2.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 4));
                    break;
                case 4:
                    iv2.setImageBitmap(bitmap);// 将图片显示在ImageView里
                    iv3.setImageResource(R.mipmap.camera_click);
                    iv1.setOnClickListener(v -> {
                    });
                    iv2.setOnClickListener(v -> {
                    });
                    iv3.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 5));
                    break;
                case 5:
                    iv1.setOnClickListener(v -> {
                    });
                    iv2.setOnClickListener(v -> {
                    });
                    iv3.setOnClickListener(v -> {
                    });
                    iv3.setImageBitmap(bitmap);// 将图片显示在ImageView里
                    break;
            }
        }
    }

    public String communication01(String urlString, Map<String, String> map, byte[] bytt) {
        String result = "";

        String end = "\r\n";
        if (!urlString.equals("")) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setDoInput(true);// 允许输入
                conn.setDoOutput(true);// 允许输出
                conn.setUseCaches(false);// 不使用Cache
                conn.setConnectTimeout(6000);// 6秒钟连接超时
                conn.setReadTimeout(6000);// 6秒钟读数据超时
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                //StringBuilder localStringBuilder1 = new StringBuilder();
                Iterator localIterator = map.entrySet().iterator();

                while (true) {
                    if (!localIterator.hasNext()) {
                        break;
                    }

                    Map.Entry localEntry = (Map.Entry) localIterator.next();
                    conn.setRequestProperty((String) localEntry.getKey(), URLEncoder.encode((String) localEntry.getValue(),
                            "UTF-8"));
                }
                /*
                 * conn.setRequestProperty("id", "1");
				 * conn.setRequestProperty("type", "1");
				 * conn.setRequestProperty("ext", "jpg");
				 */
                // / type=1 表示上传商家图片 id 表示商家编号
                // / type=2 表示上传菜品图片 id 表示菜品编号
                // / ext=jpg 表示后缀名

                DataOutputStream dos = new DataOutputStream(
                        conn.getOutputStream());
                dos.write(bytt, 0, bytt.length);
                dos.writeBytes(end);
                dos.flush();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                result = br.readLine();
            } catch (Exception e) {
                result = "{\"ret\":\"898\"}";
            }
        }
        return result;

    }
}
