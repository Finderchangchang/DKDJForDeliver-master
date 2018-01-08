package liuliu.dkdjfordeliver.listener;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.ui.MainActivity;
import liuliu.dkdjfordeliver.util.ToastUtil;
import liuliu.dkdjfordeliver.view.IMainFragView;
import liuliu.dkdjfordeliver.view.IOrderDetail;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/14.
 */
interface MainFragView {
    void loadOrder(int pageindex, String sendstate);

    void qiangDan(String orderType, String orderId);

    void changeOrderState(String sendState, String orderType);

    void finishOrder(String sendState, String orderType, String orderId, String address, String lat, String lng, String cityName);

    void getOrderDetail(String orderid);//finishOrder
}

public class MainFragListener implements MainFragView {
    IMainFragView mView;
    IOrderDetail mOrder;

    public MainFragListener(IOrderDetail mOrder) {
        this.mOrder = mOrder;
    }

    public MainFragListener(IMainFragView mView) {
        this.mView = mView;
    }

    /**
     * http://www.dakedaojia.com/App/Android/deliver/GetOrderListByUserId.aspx?1=1&gid=9&pageindex=1&sendstate=0&cityid=8&did=31&state=-1&lat=38.893242&pagesize=10&lng=115.508574
     * pageindex,sendstate,lat,pagesize
     * cityid,lng,did
     *
     * @param pageindex
     */


    /**加载我的订单列表数据*/
    @Override
    public void loadOrder(int pageindex, String sendstate) {
        Map<String, String> map = new HashMap<>();
        map.put("pageindex", pageindex + "");
        if (("0").equals(sendstate)) {
            map.put("pagesize", "50");
        } else {
            map.put("pagesize", "15");
        }
        map.put("cityid", Utils.getCache(Config.city_id));
        map.put("did", Utils.getCache(Config.user_id));
        map.put("sendstate", sendstate);
        if (!("").equals(Utils.getCache("lat"))) {
            map.put("dlat", Utils.getCache("lat") + "");
            map.put("dlng", Utils.getCache("lon") + "");
            map.put("cityname", Utils.getCache("cityName"));
        }
        try {
            HttpUtil.load()
                    .getOrders(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        if (model != null) {
                            mView.refreshOrder(model.getOrderlist());
                            //Log.e("日志","ok");
                        } else {
                            mView.refreshOrder(null);
                        }
                    }, error -> {

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void qiangDan(String orderType, String orderId) {
        Map<String, String> map = new HashMap<>();
        map.put("ordertype", orderType);//订单状态
        map.put("orderid", orderId);//订单编号
        map.put("did", Utils.getCache(Config.user_id));//骑手id

        HttpUtil.load()
                .qiangDan(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    if (("1").equals(model.getState())) {
                        mView.changeStateResult(true);
                    } else {
                        mView.changeStateResult(false);
                        Toast.makeText(MainActivity.mInstance, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Log.e("日志：", "又出错了");
                });

    }

    /**
     * @param sendState 配送状态
     * @param orderId   订单ID
     */
    @Override
    public void changeOrderState(String sendState, String orderId) {
        Map<String, String> map = new HashMap<>();
        map.put("state", sendState);
        map.put("ordertype", "1");
        map.put("orderid", orderId);
        map.put("did", Utils.getCache(Config.user_id));
        try {
            HttpUtil.load()
                    .changeOrderState(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(models -> {
                        if (("1").equals(models.getState())) {
                            if (mView != null) {
                                mView.changeStateResult(true);
                            }
                            if (mOrder != null) {
                                mOrder.changeResult(true);
                            }
                        } else {
                            if (mView != null) {
                                mView.changeStateResult(false);
                            }
                            if (mOrder != null) {
                                mOrder.changeResult(false);
                            }
                        }
                    }, error -> {
                        Log.e("日志：", "又出错了");
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sendState 配送状态
     * @param orderType 订单类型
     * @param orderId   订单ID
     */

    //state状态：接完单传1，到商家2，配送完成3.
    @Override
    public void finishOrder(String sendState, String orderType, String orderId, String address, String lat, String lng, String cityName) {
        Map<String, String> map = new HashMap<>();
        map.put("state", sendState);//配送状态
        map.put("ordertype", orderType);//1外卖订单 2跑腿
        map.put("orderid", orderId);
        map.put("did", Utils.getCache(Config.user_id));
        map.put("address", address);
        map.put("cityName", cityName);
        try {
            HttpUtil.load()
                    .changeOrderState(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(models -> {
                        if (("1").equals(models.getState())) {
                            if (mView != null) {
                                mView.changeStateResult(true);
                            }
                            if (mOrder != null) {
                                mOrder.changeResult(true);
                            }
                        } else {
                            if (mView != null) {
                                mView.changeStateResult(false);
                            }
                            if (mOrder != null) {
                                mOrder.changeResult(false);
                            }
                        }
                    }, error -> {
                        Log.e("日志：", "又出错了");
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getOrderDetail(String orderid) {//任务详情页OrderDetailActivity中获得数据接口
        HttpUtil.load()
                .getOrderByOrderId(orderid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    mOrder.showOrder(model);
                },error->{
                    String s="";
                });
    }
}

