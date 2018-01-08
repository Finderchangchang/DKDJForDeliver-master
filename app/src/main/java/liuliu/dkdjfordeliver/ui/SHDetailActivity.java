package liuliu.dkdjfordeliver.ui;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.tsz.afinal.view.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.UserListener;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.view.ISHView;

/**
 * 审核信息显示页面
 * Created by Administrator on 2016/12/20.
 */

public class SHDetailActivity extends BaseActivity implements ISHView {
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.state_btn)
    Button stateBtn;
    @Bind(R.id.auc_ll1)
    LinearLayout aucLl1;
    @Bind(R.id.id_num_tv)
    TextView idNumTv;
    @Bind(R.id.auc_ll2)
    LinearLayout aucLl2;
    @Bind(R.id.check1_iv)
    ImageView check1Iv;
    @Bind(R.id.check2_iv)
    ImageView check2Iv;
    UserListener listener;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_sh_detail);
        ButterKnife.bind(this);
        listener = new UserListener(this);
        listener.getSHDetail();
    }

    @Override
    public void initEvents() {
        titleBar.setLeftClick(() -> finish());
    }

    @Override
    public void getUser(MessageModel model) {
        if (model != null) {
            userNameTv.setText(model.getName());
            idNumTv.setText(model.getCardid());
            switch (model.getIsApproved()) {
                case "0":
                    stateBtn.setText("审核通过");
                    break;
                case "1":
                    stateBtn.setText("审核中");
                    break;
                case "2":
                    stateBtn.setText("资料未提交");
                    break;
                case "3":
                    stateBtn.setText("审核未通过");
                    break;
            }
            if (!("").equals(model.getCardimgZiPai())) {
                Glide.with(this)
                        .load(model.getCardimgZiPai())
                        .into(check1Iv);
            }
            if (!("").equals(model.getCardimg1())) {
                Glide.with(this)
                        .load(model.getCardimg1())
                        .into(check2Iv);
            }
        }
    }
}
