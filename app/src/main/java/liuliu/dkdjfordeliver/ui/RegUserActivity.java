package liuliu.dkdjfordeliver.ui;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.view.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.UserListener;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.view.IReg;

/**
 * Created by Administrator on 2016/12/12.
 */

public class RegUserActivity extends BaseActivity implements IReg {
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.code_et)
    EditText codeEt;
    @Bind(R.id.send_code_tv)
    TextView sendCodeTv;
    @Bind(R.id.pwd_et)
    EditText pwdEt;
    @Bind(R.id.qr_pwd_et)
    EditText qrPwdEt;
    @Bind(R.id.reg_btn)
    Button regBtn;
    @Bind(R.id.tk_ll)
    LinearLayout tkLl;
    @Bind(R.id.select_city_ll)
    LinearLayout select_city_ll;
    @Bind(R.id.city_et)
    EditText city_et;
    @Bind(R.id.select_city_btn)
    Button select_city_btn;
    int recLen = 60;
    UserListener listener;
    String codeTel = "";
    String codeNormal = "";
    String title = "";
    String cz_type = "0";//0，注册。1.找回密码
    String cityId = "";

    @Override
    public void initViews() {
        setContentView(R.layout.ac_reg_user);
        ButterKnife.bind(this);
        titleBar.setLeftClick(() -> finish());
        titleBar.setRightClick(() -> finish());
        listener = new UserListener(this);
        tkLl.setOnClickListener(v -> {//打开条款

        });
        select_city_btn.setOnClickListener(v -> {
            startActivityForResult(new Intent(RegUserActivity.this, CityActivity.class), 10);
        });
        title = getIntent().getStringExtra("state");
        titleBar.setCenter_str(title);
    }

    @Override
    public void initEvents() {
        switch (title) {
            case "账号注册":
                cz_type = "0";
                pwdEt.setHint("密码");
                qrPwdEt.setHint("确认密码");
                break;
            case "找回密码":
                cz_type = "1";
                regBtn.setText("确定");
                pwdEt.setHint("新密码");
                qrPwdEt.setHint("确认密码");
                select_city_ll.setVisibility(View.GONE);
                tkLl.setVisibility(View.GONE);
                break;
        }
        regBtn.setOnClickListener(v -> {
            String tel = Utils.getEditText(telEt);
            String code = Utils.getEditText(codeEt);
            String pwd = Utils.getEditText(pwdEt);
            String config_pwd = Utils.getEditText(qrPwdEt);
            String city = Utils.getEditText(city_et);
            if (("").equals(tel)) {
                ToastShort("手机号码不能为空");
            } else if (!Utils.isMobileNo(tel)) {
                ToastShort("手机号码格式不正确");
            } else if (!codeTel.equals(tel) || !code.equals(codeNormal)) {
                ToastShort("验证码不正确");
            } else if (!pwd.equals(config_pwd)) {
                ToastShort("前后密码不一致");
            } else if (title.equals("账号注册") && ("").equals(city)) {
                ToastShort("请选择当前城市");
            } else {
                listener.regUser(cz_type, tel, pwd, cityId);
            }
        });
        sendCodeTv.setOnClickListener(v -> {
            recLen = 60;
            handler.postDelayed(runnable, 1000);
            String tel = telEt.getText().toString().trim();
            if (Utils.isMobileNo(tel)) {
                codeTel = tel;
                listener.getCode(cz_type, tel);
                sendCodeTv.setClickable(false);
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 0) {
                recLen--;
                sendCodeTv.setEnabled(false);
                sendCodeTv.setText(recLen + "s");
                handler.postDelayed(this, 1000);
            } else {
                sendCodeTv.setEnabled(true);
                sendCodeTv.setText("重新发送");
            }
        }
    };

    @Override
    public void getCodeResult(boolean result, String message) {
        if (!result) {
            recLen = 0;
            sendCodeTv.setClickable(true);
            sendCodeTv.setText("重新发送");
            codeNormal = "";
            ToastShort(message);
        } else {
            codeNormal = message;
        }
    }

    @Override
    public void getRegUserResult(boolean result, String message) {
        if (result) {//成功
            Utils.IntentPost(MainActivitys.class);
            if (("账号注册").equals(title)) {
                Utils.putBooleanCache("istg", false);
            }
            LoginActivitys.mInstance.finish();
            finish();
        } else {//注册失败
            ToastShort(message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 77) {
            city_et.setText(data.getStringExtra("city"));
            cityId = data.getStringExtra("cityid");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
