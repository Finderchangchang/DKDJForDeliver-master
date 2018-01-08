package liuliu.dkdjfordeliver.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.SetImage.EventBusClass;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.MainListener;
import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.method.gaodeapiActivity;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.view.IMainView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.widget.TextView;


/**
 * Created by XY on 2017/7/28.
 */

public class EditOrderFromPhoto extends BaseActivity implements IMainView, RouteSearch.OnRouteSearchListener {
    @Bind(R.id.address_et)
    EditText SendAddressTv;
    @Bind(R.id.business_phone_et)
    EditText BusinessPhotoTv;
    @Bind(R.id.receive_order)
    Button ReceiveOrder;
    @Bind(R.id.send_photo_button)
    Button SendPhotoButton;
    @Bind(R.id.big_photo)
    ImageView BigPhoto;
    @Bind(R.id.back_tv)
    TextView BackTv;
    String url;//图片订单图片
    String orderid;
    MainListener mainListener;
    private RouteSearch routeSearch;
    double juli;
    int flag = 0;
    String flagfromOrderDetail;
    RouteSearch mRouteSearch;
    private RideRouteResult mRideRouteResult;
    int dis;//距离

    @Override
    public void initViews() {


        setContentView(R.layout.ac_editphoto);
        ButterKnife.bind(this);

        //与距离有关
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        EventBus.getDefault().register(this);
        mainListener = new MainListener(this);
        url = getIntent().getStringExtra("url");
        if (url != null) {

            flagfromOrderDetail = getIntent().getStringExtra("flagfromOrderDetail");
            if (flagfromOrderDetail != null && flagfromOrderDetail != "") {
                flag = 1;
                SendPhotoButton.setBackgroundDrawable(getResources().getDrawable(R.mipmap.send));
                ReceiveOrder.setVisibility(View.GONE);
            }
        }
        orderid = getIntent().getStringExtra("orderid");
        Picasso.with(this).load(url).into(BigPhoto);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    @Override
    public void initEvents() {
        ReceiveOrder.setOnClickListener(v -> {
            AlertDialog.Builder dialog;
            dialog = new AlertDialog.Builder(this);
            //AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("确认接单吗？");
            dialog.setIcon(R.mipmap.message);
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mainListener.qiangDan("1", orderid);

                    //mainListener.changeQsState(true);
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        });
        SendAddressTv.setOnClickListener(v -> {
            Intent intent = new Intent(EditOrderFromPhoto.this, gaodeapiActivity.class);
            startActivity(intent);
        });


        SendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    //启动距离计算
                    ToastShort("请先接单");
                } else if (SendAddressTv.getText().toString().trim().equals("") || SendAddressTv.getText() == null) {
                    ToastShort("请选择用户收货地址");
                } else if (BusinessPhotoTv.getText().toString().trim() == null || BusinessPhotoTv.getText().toString().trim().equals("")) {
                    ToastShort("请填写用户电话");
                } else {
                    String url = Utils.getCache("ulat");
                    if (!((Utils.getCache("ulat") == null || Utils.getCache("ulat").equals("") || Utils.getCache("ulng") == null || Utils.getCache("ulng").equals("")))) {
                        //计算距离
                        CaculationDiatance();
                    }
                }
            }
        });

        //返回
        BackTv.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * 将坐标转换成double
     */

    public double TransDouble(String num) {
        double coordinate = Double.parseDouble(Utils.getCache(num));
        return coordinate;
    }

    /**
     * 计算距离
     */
    private void CaculationDiatance() {

        /*RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(TransDouble("slat"), TransDouble("slng")),
                new LatLonPoint(TransDouble("ulat"), TransDouble("ulng")));
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询*/
        //  LatLng start = new LatLng(TransDouble("slat"),TransDouble("slng"));
        //LatLng end = new LatLng(TransDouble("ulat"), TransDouble("ulng"));
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(TransDouble("slat"), TransDouble("slng")),
                new LatLonPoint(TransDouble("ulat"), TransDouble("ulng")));
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        mRouteSearch.calculateWalkRouteAsyn(query);

    }

    /**
     * 从gaodeapiActivity获得地名并更新UI
     */
    public void onEventMainThread(EventBusClass event) {
        String eventstr = event.getMsg();
        SendAddressTv.setText(eventstr);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void checkVersion(String url, String content) {

    }

    @Override
    public void changeResult(double lat, double lng) {

    }

    @Override
    public void changeStateResult(boolean result) {
        if (result) {
            SendPhotoButton.setBackgroundDrawable(getResources().getDrawable(R.mipmap.send));
            ToastShort("接单成功！");
            flag = 1;
        } else {
            ToastShort("订单被别人接啦！");
        }
    }

    @Override
    public void changeQsState(String result) {

    }

    @Override
    public void userDetail(MessageModel model) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    /**
     * 路径距离查询计算
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        if (i == 1000 && walkRouteResult != null) {
            if (walkRouteResult.getPaths().size() > 0) {
                double length = 0;
                for (WalkPath path1 : walkRouteResult.getPaths()) {
                    length += path1.getDistance();

                }
                juli = Double.valueOf(length) / 1000;
                String dizhi = SendAddressTv.getText().toString().trim();
                String dianhua = BusinessPhotoTv.getText().toString().trim();
                Map<String, String> map = new HashMap<>();
                map.put("orderid", orderid);
                map.put("did", Utils.getCache(Config.user_id));
                map.put("ulat", Utils.getCache("ulat"));
                map.put("ulng", Utils.getCache("ulng"));
                map.put("dizhi", SendAddressTv.getText().toString().trim());
                map.put("jl",  juli+ "");
                map.put("utel", BusinessPhotoTv.getText().toString().trim());
                HttpUtil.load()
                        .comitOrder(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            if (model.getState().equals("1")) {
                                ToastShort(model.getMsg());
                                Message m = new Message();
                                m.arg1 = 1;
                                h.sendMessage(m);
                            } else {
                                ToastShort(model.getMsg());
                            }
                        }, error -> {
                            // ToastShort("服务器出错！");
                        });
            }
        }

    }

    //距离计算结果
    @Override
    public void onRideRouteSearched(RideRouteResult result, int i) {
/*        int errorCode = 1000;
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mRideRouteResult = result;
                    final RidePath ridePath = mRideRouteResult.getPaths()
                            .get(0);
                    dis = (int) ridePath.getDistance();
                    juli = Double.valueOf(dis) / 1000;
                    Map<String, String> map = new HashMap<>();
                    map.put("orderid", orderid);
                    map.put("did", Utils.getCache(Config.user_id));
                    map.put("ulat", Utils.getCache("ulat"));
                    map.put("ulng", Utils.getCache("ulng"));
                    map.put("dizhi", SendAddressTv.getText().toString().trim());
                    map.put("jl",  juli+"");
                    map.put("utel", BusinessPhotoTv.getText().toString().trim());
                    HttpUtil.load()
                            .comitOrder(map)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(model -> {
                                if (model.getState().equals("1")) {
                                    ToastShort(model.getMsg());
                                    Message m = new Message();
                                    m.arg1 = 1;
                                    h.sendMessage(m);
                                } else {
                                    ToastShort(model.getMsg());
                                }
                            }, error -> {
                                // ToastShort("服务器出错！");
                            });
                }
            }
        }*/
    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                EditOrderFromPhoto.this.finish();
            }
        }
    };


}
