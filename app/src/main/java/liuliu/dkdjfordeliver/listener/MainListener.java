package liuliu.dkdjfordeliver.listener;


import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.UpdateManager;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.ui.MainActivity;
import liuliu.dkdjfordeliver.ui.MainActivitys;
import liuliu.dkdjfordeliver.view.IMainView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/13.
 */
interface MainMView {
    void checkVersion();//检查版本

    void qiangDan(String orderType, String orderId);

    void downNewVersion(String url);//下载更新包

    void changeQsState(boolean isOpen);//改变订单状态

    void getUserCenter();//获得用户中心信息
}

public class MainListener implements MainMView {
    private IMainView mView;

    public MainListener(IMainView mView) {
        this.mView = mView;
    }


    //版本检测
    @Override
    public void checkVersion() {
        HttpUtil.load()
                .checkUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    String FWcode=model.getVersion();
                    String BDcode=Utils.getVersion();
                    String urlstr=model.getDownload();
                    if (Integer.parseInt(model.getVersion().replace(".","")) > Integer.parseInt(Utils.getVersion().replace(".",""))) {

                        mView.checkVersion(model.getDownload(), model.getContent());
                    } else {
                        mView.checkVersion("", "");
                    }
                }, error -> {
                    mView.checkVersion("", "");
                });
    }

    @Override
    public void qiangDan(String orderType, String orderId) {
        Map<String, String> map = new HashMap<>();
        map.put("ordertype", orderType);
        map.put("orderid", orderId);
        map.put("did", Utils.getCache(Config.user_id));
        HttpUtil.load()
                .qiangDan(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    if (("1").equals(model.getState())) {
                        mView.changeStateResult(true);
                    } else {
                        mView.changeStateResult(false);
                        Toast.makeText(MainActivitys.mInstance, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    mView.changeStateResult(false);
                });
    }

    @Override
    public void downNewVersion(String url) {
        new UpdateManager().checkUpdateInfo(url);
    }

    @Override
    public void changeQsState(boolean isOpen) {
        Map<String, String> map = new HashMap<>();
        map.put("DeliverID", Utils.getCache(Config.user_id));
        map.put("DeliverStatus", isOpen ? "0" : "1");
        HttpUtil.load().changeUserState(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    if (model != null) {
                        switch (model.getSuccess()) {
                            case "1":
                                mView.changeQsState("成功");
                                break;
                            default:
                                mView.changeQsState(model.getErrorMsg());
                                break;
                        }
                    } else {
                        mView.changeQsState("请检查网络连接");
                    }
                }, throwable -> {
                    mView.changeQsState("请检查网络连接");
                });
    }

    @Override
    public void getUserCenter() {
        HttpUtil.load().getUserDetailById(Utils.getCache(Config.user_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    mView.userDetail(model);
                }, error -> {
                    mView.userDetail(null);
                });
    }
}
