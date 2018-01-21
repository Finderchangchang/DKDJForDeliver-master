package liuliu.dkdjfordeliver.ui;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.model.Config;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static liuliu.dkdjfordeliver.method.HttpUtil.load;

/**
 * Created by XY on 2017/11/1.
 */

public class dingwei extends Service implements AMapLocationListener {
    private PendingIntent pi;
    private Mreceiver mreceiver;
    private PowerManager.WakeLock wl = null;
    LocationReceiver locationReceiver;
    private AMapLocationClient locationClient = null;
    AlarmManager am;
    Context context;
    private AMapLocationClientOption mLocationOption;
    PowerManager pm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle(getResources().getString(R.string.app_name) + "运行中...");
        Notification notification = builder.build();
        startForeground(2, notification);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = MainActivitys.mInstance;
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mywakrlock");

        startLocation(false);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wl.setReferenceCounted(false);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_SCREEN_ON);
        intentfilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentfilter.addAction(Intent.ACTION_USER_PRESENT);
        mreceiver = new Mreceiver();
        context.registerReceiver(mreceiver, intentfilter);
        //注册设置定时唤醒定位
        IntentFilter intentFile = new IntentFilter();
        intentFile.addAction("repeating");
        locationReceiver = new LocationReceiver();
        context.registerReceiver(locationReceiver, intentFile);
        //写一个定时的Pendingintent
        Intent intent1 = new Intent();
        intent1.setAction("repeating");
        pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return super.onStartCommand(intent, flags, startId);
    }


    private void wake() {
// TODO Auto-generated method stub

        //每2秒激活广播，发起一次定位
        startLocation(false);
        wl.acquire();
        am.setRepeating(0, System.currentTimeMillis(), 2000, pi);
    }

    // 开始定位。。参数是限制是否为单次定位
    private void startLocation(boolean flag) {
        // TODO Auto-generated method stub
        if (locationClient == null) {
            locationClient = new AMapLocationClient(
                    context.getApplicationContext());
            locationClient.setLocationListener(mLocationListener);
        }
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(flag);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2 * 1000);
        // 给定位客户端对象设置定位参数
        locationClient.setLocationOption(mLocationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }


    public class Mreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent2) {
            String action = intent2.getAction();
            //开屏
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.d("sunlei", "开屏");
                locationClient.stopLocation();
                wake();
            }//锁屏
            else if (intent2.ACTION_SCREEN_OFF.equals(action)) {
                Log.d("sunlei", "锁屏");
                //如果锁屏关闭当前常规定位方法，调用alarm,每2秒发动一次单次定位
                locationClient.stopLocation();
                wake();


            }//解锁
            else if (intent2.ACTION_USER_PRESENT.equals(action)) {
                locationClient.stopLocation();
                wake();
                Log.d("sunlei", "解锁");
                am.cancel(pi);
            }
        }
    }

    class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("sunlei", "定位重新获取");
            // 在这里重新申请定位
            locationClient.startLocation();
        }
    }

    //定位回调
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.e("lo", "定位监听已启动");
            Log.e("lo", "moedl" + aMapLocation);
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
                    }, error -> {

                    });
            map_cache.put("cityName", aMapLocation.getCity());
            liuliu.dkdjfordeliver.method.Utils.putCache(map_cache);
        }

        //停止定位
        public void stopdingwei() {
            MainActivity.mInstance.unregisterReceiver(mreceiver);
            MainActivity.mInstance.unregisterReceiver(locationReceiver);
            if (null != locationClient) {
                /**
                 * 如果AMapLocationClient是在当前Activity实例化的，
                 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
                 */
                locationClient.onDestroy();
                locationClient = null;
                mLocationOption = null;
            }
        }
    };
}
