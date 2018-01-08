package liuliu.dkdjfordeliver.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.tsz.afinal.view.TitleBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/20.
 */

public class JvBaoActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.name_et)
    EditText nameEt;
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.rb1)
    RadioButton rb1;
    @Bind(R.id.rb2)
    RadioButton rb2;
    @Bind(R.id.rb3)
    RadioButton rb3;
    @Bind(R.id.rb4)
    RadioButton rb4;
    @Bind(R.id.rb5)
    RadioButton rb5;
    int position = 1;
    @Bind(R.id.rg)
    RadioGroup rg;
    @Bind(R.id.commit_btn)
    Button commit_btn;
    @Bind(R.id.yy_et)
    EditText yy_et;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_jb);
        ButterKnife.bind(this);
    }

    @Override
    public void initEvents() {
        titleBar.setLeftClick(() -> finish());
        loadUi(1);
        tv1.setOnClickListener(v -> loadUi(1));
        tv2.setOnClickListener(v -> loadUi(2));
        tv3.setOnClickListener(v -> loadUi(3));
        commit_btn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            if (("").equals(name)) {
                ToastShort("名字不能为空");
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("DeliverID", Utils.getCache(Config.user_id));
                map.put("type", position + "");
                map.put("Name", nameEt.getText().toString().trim());
                map.put("Phone", telEt.getText().toString().trim());
                String yy = yy_et.getText().toString().trim();
                if (("").equals(yy)) {
                    switch (rg.getCheckedRadioButtonId()) {
                        case R.id.rb1:
                            map.put("YuanYin", rb1.getText().toString());
                            break;
                        case R.id.rb2:
                            map.put("YuanYin", rb2.getText().toString());
                            break;
                        case R.id.rb3:
                            map.put("YuanYin", rb3.getText().toString());
                            break;
                        case R.id.rb4:
                            map.put("YuanYin", rb4.getText().toString());
                            break;
                        case R.id.rb5:
                            map.put("YuanYin", rb5.getText().toString());
                            break;
                    }

                } else {
                    map.put("YuanYin", "");
                    map.put("QiTaYuanYin", position + "");
                }
                HttpUtil.load().qs_jb(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            if (model != null) {
                                if (("1").equals(model.getSuccess())) {
                                    ToastShort("举报成功");
                                    finish();
                                } else {
                                    ToastShort("提交失败");
                                }
                            }
                        }, error -> {
                            ToastShort("提交失败");
                        });
            }
        });
    }

    private void loadUi(int wz) {
        position = wz;
        rb1.setChecked(true);
        nameEt.setText("");
        telEt.setText("");
        switch (position) {
            case 1:
                tv1.setTextColor(getResources().getColor(R.color.white));
                tv2.setTextColor(getResources().getColor(R.color.black));
                tv3.setTextColor(getResources().getColor(R.color.black));
                tv1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv2.setBackgroundColor(getResources().getColor(R.color.white));
                tv3.setBackgroundColor(getResources().getColor(R.color.white));
                nameEt.setHint("骑士姓名(必填)");
                telEt.setHint("骑士手机号");
                rb1.setText("态度不好");
                rb2.setText("虚假订单，没有真实送达");
                rb3.setText("报虚假位置");
                rb4.setVisibility(View.GONE);
                rb5.setVisibility(View.GONE);
                break;
            case 2:
                tv2.setTextColor(getResources().getColor(R.color.white));
                tv1.setTextColor(getResources().getColor(R.color.black));
                tv3.setTextColor(getResources().getColor(R.color.black));
                tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv1.setBackgroundColor(getResources().getColor(R.color.white));
                tv3.setBackgroundColor(getResources().getColor(R.color.white));
                nameEt.setHint("商家姓名(必填)");
                telEt.setHint("商家手机号");
                rb1.setText("地址错误");
                rb2.setText("虚假订单，没有真实送达");
                rb3.setText("骑士接单后立即取消");
                rb4.setText("商家地址错误");
                rb5.setText("虚假注册");
                rb4.setVisibility(View.VISIBLE);
                rb5.setVisibility(View.VISIBLE);
                break;
            case 3:
                tv3.setTextColor(getResources().getColor(R.color.white));
                tv2.setTextColor(getResources().getColor(R.color.black));
                tv1.setTextColor(getResources().getColor(R.color.black));
                tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv2.setBackgroundColor(getResources().getColor(R.color.white));
                tv1.setBackgroundColor(getResources().getColor(R.color.white));
                nameEt.setHint("工作人员姓名(必填)");
                telEt.setHint("工作人员手机号");
                rb1.setText("业务员联合商户作弊");
                rb2.setText("业务员收钱");
                rb3.setText("业务员联系不上");
                rb4.setVisibility(View.GONE);
                rb5.setVisibility(View.GONE);
                break;
        }
    }
}
