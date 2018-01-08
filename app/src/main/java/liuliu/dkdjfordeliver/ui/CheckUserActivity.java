package liuliu.dkdjfordeliver.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

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
import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.model.MessageModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 验证骑士身份
 * Created by Administrator on 2016/12/14.
 */

public class CheckUserActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.true_name_et)
    EditText trueNameEt;
    @Bind(R.id.id_num_et)
    EditText idNumEt;
    @Bind(R.id.qian_ll)
    ImageView qianLl;
    @Bind(R.id.hou_ll)
    ImageView houLl;
    @Bind(R.id.check_btn)
    Button checkBtn;
    boolean oneImg = false;
    boolean twoImg = false;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_chekc_user);
        ButterKnife.bind(this);
        titleBar.setLeftClick(() -> finish());
    }

    @Override
    public void initEvents() {
        checkBtn.setOnClickListener(v -> {
            checkBtn.setClickable(false);
            String trueName = trueNameEt.getText().toString().trim();
            String idNum = idNumEt.getText().toString().trim();
            if (("").equals(trueName)) {
                ToastShort("姓名不能为空");
            } else if (("").equals(idNum)) {
                ToastShort("证件号码不能为空");
            } else if (!oneImg || !twoImg) {
                ToastShort("两张照片不能为空");
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("id", Utils.getCache(Config.user_id));
                map.put("Name", Utils.URLEncodeImage(trueNameEt.getText().toString().trim()));
                map.put("cardid", idNumEt.getText().toString().trim());
                HttpUtil.load().checkUser(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            if (model != null) {
                                if (("1").equals(model.getState())) {
                                    ToastShort("提交成功");
                                    setResult(11);
                                    finish();
                                } else {
                                    ToastShort("提交失败");
                                }
                            }
                        }, error -> {
                            ToastShort("请检查网络连接");
                        });
            }
        });
        qianLl.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1));
        houLl.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 2));
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
            Map<String, String> map = new HashMap<>();
            map.put("id", Utils.getCache(Config.user_id));
            map.put("type", requestCode + "");
            switch (requestCode) {
                case 1:
                    qianLl.setImageBitmap(bitmap);
                    oneImg = true;
                    break;
                case 2:
                    twoImg = true;
                    houLl.setImageBitmap(bitmap);
                    break;
            }
            map.put("ext", "jpg");
            map.put("Name", trueNameEt.getText().toString().trim());
            map.put("cardid", idNumEt.getText().toString().trim());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            Observable.create(subscriber -> {
                subscriber.onNext(communication01("http://kuaipao.myejq.com/APP/Android/Deliver/androidupload.ashx", map, baos.toByteArray()));
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(str -> {
                        checkBtn.setClickable(true);
                        MessageModel model = new Gson().fromJson(str.toString(), MessageModel.class);
                        if (("1").equals(model.getState())) {

                        } else {
                            check(requestCode);
                        }
                    }, error -> {
                        checkBtn.setClickable(true);
                        check(requestCode);
                    });
        }
    }

    private void check(int position) {
        switch (position) {
            case 1:
                oneImg = false;
                qianLl.setImageResource(R.mipmap.camera_click);
                break;
            case 2:
                twoImg = false;
                houLl.setImageResource(R.mipmap.camera_click);
                break;
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
                String s = "";
//                listener.action(260, result, Tag);
            } catch (Exception e) {
                result = "{\"ret\":\"898\"}";
//                listener.action(258, result, Tag);
            }
        }
        return result;

    }
}
