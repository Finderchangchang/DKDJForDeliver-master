package liuliu.dkdjfordeliver.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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

public class OrderFragment extends Fragment implements IMainFragView {
    ListView listView;
    CommonAdapter<OrderModel> mAdapter;
    View view;
    MainFragListener listener;
    int tab_index = 0;
    private List<OrderModel> mOrders;
    LinearLayout no_data_ll;
    SwipeRefreshLayout item_refresh_sw;
    int pageNum = 1;
    int totalPage = 1;
    boolean isLoad = false;
//数字的接单顺序 1>5>2>3
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new MainFragListener(this);
        mOrders = new ArrayList<>();
        mAdapter = new CommonAdapter<OrderModel>(MyOrderActivity.mInstance,
                mOrders, R.layout.item_order) {

            // 切换状态栏按钮
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                String btn_state = "";
                holder.setText(R.id.remark_tv, model.getOrderAttach());//备注
                //配送状态：0：未配送(0页面：抢单，1页面：到商家)，1.已接单。5.到商家（配送中），2：配送中（配送完成），3：配送完成， 4：配送失败
                switch (model.getSendstate()) {
                    case "0":
                        btn_state = "抢单";
                        break;
                    case "1":
                        btn_state = "上报到店";//以前是到商家
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.no_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                    case "5":
                        btn_state = "已取货";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                    case "2":
                        btn_state = "已完成";//以前是完成配送
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                   /* case "5":
                        btn_state = "完成配送";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;*/
                    case "3":
                        btn_state = "已完成";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.sy_ren);
                        break;
                  /*  case "4":  2017年9月8日10:07:58 注释
                        btn_state = "已取消";
                        break;*/
                }
                //1--1  25--2 3--3
                holder.setOnClickListener(R.id.tou_ll, v -> {//第二个按钮
                    if (!("").equals(model.getUlng())) {//导航到用户
                        String lat_lng = model.getUlat() + "-" + model.getUlng();
                        Utils.IntentPost(TestActivity.class, intent -> {
                            intent.putExtra("key", lat_lng);
                            intent.putExtra("address", model.getAddress());
                        });
                    } else {
                        MyOrderActivity.mInstance.ToastShort("坐标有问题无法导航 -_-!");
                    }
                });
                holder.setText(R.id.change_state_tv, btn_state); //改变第三个按钮上显示的话
                holder.setOnClickListener(R.id.tos_ll, v -> {//第一个按钮
                    if (!("").equals(model.getSlat())) {//开启导航
                        String lat_lng = Utils.check(model.getSlat(), model.getSlng());
                        Utils.IntentPost(TestActivity.class, intent -> {
                            intent.putExtra("key", lat_lng);
                            intent.putExtra("address", model.getShopaddress());
                        });
                    } else {
                        MyOrderActivity.mInstance.ToastShort("坐标有问题无法导航 -_-!");
                    }
                });

                //state是选择那四栏后发送给服务器索要数据然后返回的到达下一个状态。如果状态是0 就是待抢单的订单 发过去回过来的就是1
                //想要服务器改变什么状态就给服务器传几，比如说传5就是要求服务器把状态改编成5已取货
                holder.setOnClickListener(R.id.order_state_ll, v -> {//第三个按钮
                   // if (!("5").equals(model.getSendstate())) {
                        String now_state = "0";
                        switch (model.getSendstate()) {
                            case "0":
                                now_state = "1";//抢单
                                break;
                            case "1":
                                now_state = "5";//上报到店
                                break;
                            case "5":
                                now_state = "2";//已取货
                                break;
                            case "2":
                                now_state = "3";//已完成
                                break;
                            case "3":  //已完成不需要获得数据了
                                now_state = "4";
                                break;

                        }
                        try {
                            if (("1").equals(now_state)) {//抢单
                                listener.qiangDan("1", model.getOrderid());

                            } else if (("4").equals(now_state)) {//3是已完成了，所以应该不需要 修改成备注时间2017年9月8日15:24:04

                               /* listener.finishOrder(now_state, "1", model.getOrderid(), Utils.getCache("address")
                                        , Utils.getCache("lat"), Utils.getCache("lon"), Utils.getCache("cityName"));*/
                            } else  {
                                listener.finishOrder(now_state, "1", model.getOrderid(), "", "", "", "");
                            }
                            pageNum = 1;
                            refreshList(tab_index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } /*else {
                        Intent intent = new Intent(MyOrderActivity.mInstance, CameraActivity.class);
                        intent.putExtra("orderid", model.getOrderid());
                        startActivityForResult(intent, 99);
                        listener.finishOrder("2", "1", model.getOrderid(), "", "", "", "");
                    }*/
                //});
                );

                try {
                    holder.setText(R.id.jl1_tv, model.getJuLi());
                    holder.setText(R.id.jl2_tv, model.getSJDaoYH());
                    holder.setText(R.id.qh_tv, model.getShopname());
                    holder.setText(R.id.xx_address_tv, model.getTogoAddress());
                    holder.setText(R.id.sh_tv, model.getAddress());
                    holder.setText(R.id.fb_time_tv, model.getAddtime());
                    holder.setText(R.id.sr_tv, model.getSendFee());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.setOnClickListener(R.id.tel1_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getPotioncomm()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
                holder.setOnClickListener(R.id.tel2_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getOrderComm()));
                    Log.e("tel:", model.getOrderComm());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
        //每个item点击获得订单详情
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(MyOrderActivity.mInstance, OrderDetailActivitys.class);
            if (mOrders.size() > 0) {
                intent.putExtra("orderid", mOrders.get(position).getOrderid());
                intent.putExtra("foodno", mOrders.get(position).getFoodNo());
                intent.putExtra("btn_state", mOrders.get(position).getSendstate());
                if (mOrders.get(position).getImgOrder() != null & !(mOrders.get(position).getImgOrder().length() < 5)) {
                    intent.putExtra("imgurl", mOrders.get(position).getImgOrder());
                    intent.putExtra("ImgNumber",mOrders.get(position).getImgNumber());
                    Utils.putCache("slat",mOrders.get(position).getSlat());//商家坐标
                    Utils.putCache("slng",mOrders.get(position).getSlng());
                    intent.putExtra("ImgNumber",mOrders.get(position).getImgNumber());

                }

            }
            startActivityForResult(intent, 44);
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (!isLoading && totalPage > pageNum) {
                            isLoading = true;
                            Toast.makeText(MyOrderActivity.mInstance, pageNum + "/" + totalPage, Toast.LENGTH_SHORT).show();
                            pageNum = pageNum + 1;
                            if (tab_index == 2) {
                                listener.loadOrder(pageNum, "2,5");
                            } else {
                                listener.loadOrder(pageNum, tab_index + "");
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        item_refresh_sw = (SwipeRefreshLayout) view.findViewById(R.id.item_refresh_sw);
        no_data_ll = (LinearLayout) view.findViewById(R.id.no_data_ll);
        item_refresh_sw.setOnRefreshListener(() -> {
            pageNum = 1;
            refreshList(tab_index);
        });
        return view;
    }

    boolean isLoading = false;

    /**
     * 刷新当前列表
     *
     * @param position 当前选中的item位置
     */
    public void refreshList(int position) {
        isLoad = true;
        if (item_refresh_sw != null) {
            item_refresh_sw.setRefreshing(true);
        }
        tab_index = position;
        if (listener == null) {
            listener = new MainFragListener(this);
        }
        mOrders = new ArrayList<>();
     /*   if (position == 2) {
            listener.loadOrder(1, "2");
            //listener.loadOrder(1, "2");
        } else {*/
        listener.loadOrder(1, position + "");
        //}
    }


    @Override
    public void refreshOrder(List<OrderModel> list) {
        item_refresh_sw.setRefreshing(false);
        mOrders = list;
        if (list != null) {
            mAdapter.refresh(list);
            if (list.size() > 0) {
                no_data_ll.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.refresh(new ArrayList<>());
            listView.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refresh(ListModel list) {
        isLoading = false;
        item_refresh_sw.setRefreshing(false);
        pageNum = Integer.parseInt(list.getPage());
        totalPage = Integer.parseInt(list.getTotal());
        if (pageNum > 1) {
            List<OrderModel> order = list.getOrderlist();
            for (OrderModel model : order) {
                mOrders.add(model);
            }
        } else {
            mOrders = list.getOrderlist();
        }
        if (list != null) {
            mAdapter.refresh(mOrders);
            if (mOrders.size() > 0) {
                no_data_ll.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.refresh(new ArrayList<>());
            listView.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void loadMoreOrder(List<OrderModel> list) {
    }

    @Override
    public void changeStateResult(boolean result) {
        if (result) {
//            refreshList(tab_index);
            MyOrderActivity.mInstance.ToastShort("更新成功~~");
        } else {
            MyOrderActivity.mInstance.ToastShort("更新失败~~");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 99:
                refreshList(tab_index);
                break;
            case 44:
                refreshList(tab_index);
                break;
        }
    }
}
