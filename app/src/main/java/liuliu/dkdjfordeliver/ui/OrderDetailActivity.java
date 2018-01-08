package liuliu.dkdjfordeliver.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import net.tsz.afinal.annotation.view.CodeNote;

import java.util.List;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.OrderDetailListener;
import liuliu.dkdjfordeliver.method.CommonAdapter;
import liuliu.dkdjfordeliver.method.CommonViewHolder;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.GoodModel;
import liuliu.dkdjfordeliver.model.OrderModel;
import liuliu.dkdjfordeliver.view.IOrderDetailView;

/**
 * 订单详情页面
 * Created by Administrator on 2016/10/15.
 */

public class OrderDetailActivity extends BaseActivity implements IOrderDetailView {
    public static OrderDetailActivity mInstance;
    OrderDetailListener mListener;
    @CodeNote(id = R.id.order_id_tv)
    TextView order_id_tv;
    @CodeNote(id = R.id.toolbar)
    Toolbar toolbar;
    @CodeNote(id = R.id.order_time_tv)
    TextView order_time_tv;
    CommonAdapter<GoodModel> mAdapter;
    @CodeNote(id = R.id.good_list_lv)
    ListView good_list_lv;
    @CodeNote(id = R.id.remark_tv)
    TextView remark_tv;
    @CodeNote(id = R.id.change_order_state_cv)
    CardView change_order_state_cv;
    @CodeNote(id = R.id.shangjia_name_tel_tv)
    TextView shangjia_name_tel_tv;
    @CodeNote(id = R.id.shangjia_address_tv)
    TextView shangjia_address_tv;
    @CodeNote(id = R.id.shangjia_tel_ll)
    LinearLayout shangjia_tel_ll;
    @CodeNote(id = R.id.guke_name_tel_tv)
    TextView guke_name_tel_tv;
    @CodeNote(id = R.id.guke_address_tv)
    TextView guke_address_tv;
    @CodeNote(id = R.id.guke_tel_ll)
    LinearLayout guke_tel_ll;
    @CodeNote(id = R.id.change_state_tv)
    TextView change_state_tv;//更改状态按钮
    @CodeNote(id = R.id.chakan_ditu_ll)
    LinearLayout chakan_ditu_ll;
    @CodeNote(id = R.id.main_sl)
    ScrollView main_sl;
    @CodeNote(id = R.id.main_pb)
    ProgressBar main_pb;//进度条
    @CodeNote(id = R.id.error_tv)
    TextView error_tv;
    @CodeNote(id = R.id.food_no_tv)
    TextView food_no_tv;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_order_detail);
        mListener = new OrderDetailListener(this);
        mInstance = this;
    }

    @Override
    public void initEvents() {
        String orderid = getIntent().getStringExtra("orderid");
        String foodNo = getIntent().getStringExtra("foodno");
        Log.e("日志","订单号：" + orderid);
        food_no_tv.setText(foodNo);
        if (("").equals(orderid)) {
            OrderDetailActivity.this.finish();
        } else {
            UiState(0);
            mListener.loadOrder(orderid);
        }
        toolbar.setNavigationOnClickListener(v -> {
            mInstance.finish();
        });
    }

    /**
     * @param state
     */
    private void UiState(int state) {
        switch (state) {
            case 0://刷新
                main_sl.setVisibility(View.GONE);//主页隐藏
                main_pb.setVisibility(View.VISIBLE);//圆圈显示
                error_tv.setVisibility(View.GONE);//错误隐藏
                break;
            case 1://显示成功
                main_sl.setVisibility(View.VISIBLE);//主页显示
                main_pb.setVisibility(View.GONE);//圆圈隐藏
                error_tv.setVisibility(View.GONE);//错误隐藏
                break;
            case 2://显示失败
                main_sl.setVisibility(View.GONE);//主页隐藏
                main_pb.setVisibility(View.GONE);//圆圈显示
                error_tv.setVisibility(View.VISIBLE);//错误隐藏
                break;
        }
    }

    @Override
    public void showOrder(OrderModel model) {
        if (model != null) {
            UiState(1);
            order_id_tv.setText(model.getOrderid());
            order_time_tv.setText(model.getDispatchTime());
            List<GoodModel> list = model.getList();
            mAdapter = new CommonAdapter<GoodModel>(mInstance, list, R.layout.item_good) {
                @Override
                public void convert(CommonViewHolder holder, GoodModel goodModel, int position) {
                    holder.setText(R.id.good_name_tv, goodModel.getName());
                    holder.setText(R.id.good_price_tv, "￥" + goodModel.getPrice());
                    holder.setText(R.id.good_num_tv, "x" + goodModel.getCount());
                }
            };

            good_list_lv.setAdapter(mAdapter);
            int height = good_list_lv.getMeasuredHeight();
            int m_height = MesaureHeight();
            good_list_lv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, MesaureHeight() * 5 / 3 * list.size()));

            if (("").equals(model.getNote())) {
                remark_tv.setVisibility(View.GONE);
            } else {
                remark_tv.setVisibility(View.VISIBLE);
                remark_tv.setText("备注：" + model.getNote());//备注
            }
            order_time_tv.setText(model.getAddtime());
            shangjia_name_tel_tv.setText(model.getShopname() + " " + model.getShoptel());
            guke_address_tv.setText(model.getAddress());
            guke_name_tel_tv.setText(model.getRealname() + " " + model.getPhone());
            shangjia_address_tv.setText(model.getShopaddress());
            String btn_state = "";
            String order_state = "";
            switch (model.getSendstate()) {
                case "0":
                    btn_state = "抢单";
                    break;
                case "1":
                    order_state = "已接单";
                    btn_state = "到商家";
                    break;
                case "5":
                    order_state = "到达商家";
                    btn_state = "配送中";
                    break;
                case "2":
                    order_state = "配送中";
                    btn_state = "配送完成";
                    break;
            }
            change_state_tv.setText(btn_state);
            change_order_state_cv.setOnClickListener(v -> {

            });
            shangjia_tel_ll.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getShoptel()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            guke_tel_ll.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getPhone()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            chakan_ditu_ll.setOnClickListener(v -> {
                Utils.IntentPost(OrderMapActivity.class, listener -> {

                });
            });
        } else {
            UiState(2);
        }
    }

    /**
     * 计算当前Item高度
     *
     * @return
     */
    private int MesaureHeight() {
        View total_ll = View.inflate(OrderDetailActivity.mInstance, R.layout.item_good, null);
        total_ll.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return total_ll.getMeasuredHeight();
    }

    @Override
    public void changeResult(String result) {

    }
}
