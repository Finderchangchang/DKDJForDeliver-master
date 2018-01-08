package liuliu.dkdjfordeliver.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.listener.MainFragListener;
import liuliu.dkdjfordeliver.method.CommonAdapter;
import liuliu.dkdjfordeliver.method.CommonViewHolder;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.ListModel;
import liuliu.dkdjfordeliver.model.OrderModel;
import liuliu.dkdjfordeliver.view.IMainFragView;

/**
 * Created by Administrator on 2016/10/14.
 */

public class MainFragment extends Fragment implements IMainFragView {
    ListView listView;
    CommonAdapter<OrderModel> mAdapter;
    View view;
    MainFragListener listener;
    int tab_index = 0;
    private List<OrderModel> mOrders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new MainFragListener(this);
        mOrders = new ArrayList<>();
        mAdapter = new CommonAdapter<OrderModel>(MainActivity.mInstance,
                mOrders, R.layout.item_order) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
//                holder.setText(R.id.shop_name_tv, model.getShopname() + "   " + model.getFoodNo() + "    距离：" + model.getJuLi());
//                holder.setText(R.id.shop_address_tv, model.getTogoAddress());
//                String money = model.getTotalmoney();
//                try {
//                    double db = Math.round(Float.parseFloat(money) * 0.8 * 100);
//                    holder.setText(R.id.user_address_tv, model.getAddress() + "  收入：" + db / 100 + "元");
//                } catch (Exception e) {
//                    holder.setText(R.id.user_address_tv, model.getAddress());
//                }
                String btn_state = "";
                String order_state = "";
                holder.setText(R.id.remark_tv, model.getOrderAttach());
                //配送状态：0：未配送(0页面：抢单，1页面：到商家)，1.已接单。5到商家（配送中），2：配送中（配送完成），3：配送完成， 4：配送失败
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
//                holder.setOnClickListener(R.id.daohang_ll, v -> {
//                    if (tab_index == 0 || tab_index == 1) {//导航到商家
//                        if (!("").equals(model.getSlat())) {
//                            String lat_lng = Utils.check(model.getSlat(), model.getSlng());
//                            Utils.IntentPost(TestActivity.class, intent -> {
//                                intent.putExtra("key", lat_lng);
//                            });
//                        } else {
//                            MainActivity.mInstance.ToastShort("坐标有问题无法导航 -_-!");
//                        }
//                    } else {//导航到用户
//                        if (!("").equals(model.getUlng())) {
//                            String lat_lng = model.getUlat() + "-" + model.getUlng();
//                            Utils.IntentPost(TestActivity.class, intent -> {
//                                intent.putExtra("key", lat_lng);
//                            });
//                        } else {
//                            MainActivity.mInstance.ToastShort("快跑订单不能导航 -_-!");
//                        }
//                    }
//                });
//                holder.setText(R.id.order_state_tv, order_state);
                holder.setText(R.id.change_state_tv, btn_state);
//                holder.setOnClickListener(R.id.tel_ll, v -> {
//                    String tel = "";
//                    if (tab_index == 0 || tab_index == 1) {//商家电话
//                        tel = model.getPotioncomm();
//                    } else {//用户电话
//                        tel = model.getOrderComm();
//                    }
//                    if (!("").equals(tel)) {
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        MainActivity.mInstance.ToastShort("快跑订单没有电话 -_- !");
//                    }
//                });

                //改变当前的订单状态
                holder.setOnClickListener(R.id.order_state_ll, v -> {
                    String now_state = "0";
                    switch (model.getSendstate()) {
                        case "0":
                            now_state = "1";//取货中
                            break;
                        case "1":
                            now_state = "2";
                            break;

                        case "2":
                            now_state = "3";//配送完成
                            break;
                        case "3"://已完成
                            break;
                        case "4"://已取消
                            break;
                    }
                    listener = new MainFragListener(MainFragment.this);
                    if (("1").equals(now_state)) {
                        listener.qiangDan("1", model.getOrderid());
                    } else if (("3").equals(now_state)) {
                        listener.finishOrder(now_state, "1", model.getOrderid(), Utils.getCache("address"),
                                Utils.getCache("lat"), Utils.getCache("lon"), Utils.getCache("cityName"));
                    } else {
                        listener.finishOrder(now_state, "1", model.getOrderid(), "", "", "", "");
                    }
                });
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) view.findViewById(R.id.list_frag);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Utils.IntentPost(OrderDetailActivity.class, intent -> {
                if (mOrders.size() > 0) {
                    intent.putExtra("orderid", mOrders.get(position).getOrderid());
                    intent.putExtra("foodno", mOrders.get(position).getFoodNo());
                }
            });
        });
        refreshList(tab_index);
        return view;
    }

    public void refreshList(int position) {
        tab_index = position;
        if (listener == null) {
            listener = new MainFragListener(this);
        }
        mOrders = new ArrayList<>();
        switch (position) {
            case 0://新订单
                listener.loadOrder(1, "0");
                break;
            case 1://取货中
                listener.loadOrder(1, "1");
                break;
            case 2://待送达
                listener.loadOrder(1, "2");
                break;
        }
    }


    @Override
    public void refreshOrder(List<OrderModel> list) {
        mOrders = list;
        if (mAdapter != null) {
            mAdapter.refresh(list);
        }
    }

    @Override
    public void refresh(ListModel list) {

    }


    @Override
    public void loadMoreOrder(List<OrderModel> list) {

    }

    @Override
    public void changeStateResult(boolean result) {
        if (result) {
            refreshList(tab_index);
            MainActivity.mInstance.ToastShort("更新成功~~");
        } else {
            MainActivity.mInstance.ToastShort("更新失败~~");
        }
    }
}
