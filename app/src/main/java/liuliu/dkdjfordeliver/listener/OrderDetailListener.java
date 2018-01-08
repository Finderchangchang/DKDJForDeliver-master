package liuliu.dkdjfordeliver.listener;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.view.IHistory;
import liuliu.dkdjfordeliver.view.IOrderDetailView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/15.
 */
interface OrderDetailMView {
    void loadOrder(String order_id);//加载订单详情

    void changeOrderState(int state_id);//更改订单状态

    void getHistory(int pageIndex, String startTime, String endTime);
}

public class OrderDetailListener implements OrderDetailMView {
    IOrderDetailView mView;
    IHistory history;

    public OrderDetailListener(IHistory history) {
        this.history = history;
    }

    public OrderDetailListener(IOrderDetailView mView) {
        this.mView = mView;
    }

    @Override
    public void loadOrder(String order_id) {
        HttpUtil.load()
                .getOrderByOrderId(order_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    mView.showOrder(model);
                });
    }

    @Override
    public void changeOrderState(int state_id) {
        mView.changeResult("");
    }

    @Override
    public void getHistory(int pageIndex, String startTime, String endTime) {
        Map<String, String> map = new HashMap<>();
        map.put("DataId", Utils.getCache(Config.user_id));
        map.put("KaiTime", startTime);
        map.put("EndTime", endTime);
        map.put("pageindex", pageIndex + "");
        HttpUtil.load()
                .getHistoryByTime(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    history.historyList(model);
                });
    }
}
