package liuliu.dkdjfordeliver.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import net.tsz.afinal.annotation.view.CodeNote;
import net.tsz.afinal.view.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.UserListener;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.view.ILoginView;

import static liuliu.dkdjfordeliver.R.id.login_btn;

/**
 * Created by Administrator on 2016/12/12.
 */

public class LoginActivitys extends BaseActivity implements ILoginView {
    @CodeNote(id = R.id.title_bar)
    TitleBar title_bar;
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.pwd_et)
    EditText pwdEt;
    @Bind(login_btn)
    Button loginBtn;
    @Bind(R.id.find_pwd_tv)
    TextView findPwdTv;
    UserListener listener;
    String user_id;
    public static LoginActivitys mInstance;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_login);
        ButterKnife.bind(this);
        listener = new UserListener(this);
        mInstance = this;
    }

    AlertDialog.Builder builder;

    @Override
    public void initEvents() {
        builder = new AlertDialog.Builder(LoginActivitys.mInstance);
        title_bar.setLeftClick(() -> finish());
        //title_bar.setRightClick(() -> Utils.IntentPost(RegUserActivity.class, intent -> intent.putExtra("state", "账号注册")));
        loginBtn.setOnClickListener(v -> {
            String tel = telEt.getText().toString().trim();
            String pwd = pwdEt.getText().toString().trim();
            if (("").equals(tel)) {
                ToastShort("手机号码不能为空");
            } else if (("").equals(pwd)) {
                ToastShort("密码不能为空");
            } else {
                listener.doLogin(tel, pwd);
            }
        });
        findPwdTv.setOnClickListener(v -> Utils.IntentPost(RegUserActivity.class, intent -> intent.putExtra("state", "找回密码")));
        PushManager.getInstance().initialize(this, null);
        //阻止一登陆就闪退。//ytx改
       /* if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            builder.setTitle("提示");
            builder.setMessage("请允许以下权限，否则软件会出现崩溃！！");
            builder.setNegativeButton("确定", (dialog, which) -> {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            });
            builder.show();
        } else {*/
            load();
        /*}*/
    }

    @Override
    public void loginResult(String msg) {
        if (msg == "") {//登录成功
            Utils.IntentPost(MainActivitys.class);
            finish();
        } else {
            ToastShort(msg);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    load();
                } else {
                    finish();
                }
                break;
        }
    }

    private void load() {
        user_id = Utils.getCache(Config.user_id);
        if (!("").equals(user_id)) {
            Utils.IntentPost(MainActivitys.class);
            finish();
        }
    }
}
