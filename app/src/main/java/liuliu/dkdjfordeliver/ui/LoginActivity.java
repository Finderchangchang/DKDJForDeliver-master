package liuliu.dkdjfordeliver.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import net.tsz.afinal.annotation.view.CodeNote;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.UserListener;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.view.ILoginView;

/**
 * 登录页面
 * Created by Administrator on 2016/10/13.
 */

public class LoginActivity extends BaseActivity implements ILoginView {
    public static LoginActivity mInstance;
    @CodeNote(id = R.id.user_name_tv)
    AutoCompleteTextView user_name_tv;
    @CodeNote(id = R.id.pwd_tv)
    TextView pwd_tv;
    @CodeNote(id = R.id.login_btn)
    Button login_btn;
    UserListener listener;
    String user_id;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_login);
        mInstance = this;
        listener = new UserListener(this);
    }

    @Override
    public void initEvents() {
        PushManager.getInstance().initialize(this, null);
        if (ContextCompat.checkSelfPermission(LoginActivity.mInstance,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.mInstance,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        } else {
            load();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                load();
                break;
        }
    }

    private void load() {
        user_id = Utils.getCache(Config.user_id);
        if (("").equals(user_id) || user_id == null) {
            login_btn.setOnClickListener(v -> {
                String name = user_name_tv.getText().toString().trim();
                String pwd = pwd_tv.getText().toString().trim();
                if (("").equals(name)) {
                    ToastShort("用户名不能为空");
                } else if (("").equals(pwd)) {
                    ToastShort("密码不能为空");
                } else {
                    listener.doLogin(name, pwd);
                }
            });
        } else {//记住当前账号，直接跳转到登录页面
            Utils.IntentPost(MainActivity.class);
            mInstance.finish();
        }
    }

    @Override
    public void loginResult(String msg) {
        if (msg == "") {//登录成功
            Utils.IntentPost(MainActivity.class);
            mInstance.finish();
        } else {
            ToastShort(msg);
        }
    }
}
