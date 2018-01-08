package liuliu.dkdjfordeliver.locationservicedemo;

import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.method.*;
import liuliu.dkdjfordeliver.model.Config;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static liuliu.dkdjfordeliver.base.BaseApplication.mLocationClient;
import static liuliu.dkdjfordeliver.method.HttpUtil.load;

/**
 * 包名： com.amap.locationservicedemo
 * <p>
 * 创建时间：2016/10/27
 * 项目名称：LocationServiceDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：后台服务定位
 *
 * <p>
 *     modeified by liangchao , on 2017/01/17
 *     update:
 *     1. 只有在由息屏造成的网络断开造成的定位失败时才点亮屏幕
 *     2. 利用notification机制增加进程优先级
 * </p>
 */
public class LocationService extends NotiService {

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private int locationCount;

    /**
     * 处理息屏关掉wifi的delegate类
     */
    private IWifiAutoCloseDelegate mWifiAutoCloseDelegate = new WifiAutoCloseDelegate();

    /**
     * 记录是否需要对息屏关掉wifi的情况进行处理
     */
    private boolean mIsWifiCloseable = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        applyNotiKeepMech(); //开启利用notification提高进程优先级的机制

        if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
            mIsWifiCloseable = true;
            mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
        }

        startLocation();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        unApplyNotiKeepMech();
        stopLocation();

        super.onDestroy();
    }

    /**
     * 启动定位
     */
    void startLocation() {
        stopLocation();

        if (null == mLocationClient) {
            mLocationClient = new AMapLocationClient(this.getApplicationContext());
        }

        mLocationOption = new AMapLocationClientOption();
        // 使用连续
        mLocationOption.setOnceLocation(false);
        mLocationOption.setLocationCacheEnable(false);
        // 每10秒定位一次
        mLocationOption.setInterval(1000);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
        }
    }

    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.e("lo","定位监听已启动");
                Log.e("lo","moedl"+aMapLocation);
                Map<String, String> map_cache = new HashMap<>();
                map_cache.put("address", aMapLocation.getAddress());
                map_cache.put("lat", aMapLocation.getLatitude() + "");
                map_cache.put("lon", aMapLocation.getLongitude() + "");
                Map<String, String> mpp = new HashMap<String, String>();
                mpp.put("did", liuliu.dkdjfordeliver.method.Utils.getCache(Config.user_id));
                mpp.put("lat", aMapLocation.getLatitude() + "");
                mpp.put("lng", aMapLocation.getLongitude() + "");
                mpp.put("glat", aMapLocation.getLatitude() + "");
                mpp.put("glng", aMapLocation.getLongitude() + "");
                load().pushLatLng(mpp)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mm -> {
                            if (("-1").equals(mm.getState())) {
                                //ToastShort(mm.getMsg());
                            }
                        });
                map_cache.put("cityName", aMapLocation.getCity());
                liuliu.dkdjfordeliver.method.Utils.putCache(map_cache);

            if (!mIsWifiCloseable) {
                return;
            }

            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                mWifiAutoCloseDelegate.onLocateSuccess(getApplicationContext(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isMobileAva(getApplicationContext()));
            } else {
                mWifiAutoCloseDelegate.onLocateFail(getApplicationContext() , aMapLocation.getErrorCode() , PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isWifiCon(getApplicationContext()));
            }

        }



    };

}
