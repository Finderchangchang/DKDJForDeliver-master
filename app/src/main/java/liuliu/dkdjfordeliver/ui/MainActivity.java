package liuliu.dkdjfordeliver.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import net.tsz.afinal.annotation.view.CodeNote;

import java.util.HashMap;
import java.util.Map;

import cn.trinea.android.common.util.PreferencesUtils;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.base.BaseApplication;
import liuliu.dkdjfordeliver.listener.MainListener;
import liuliu.dkdjfordeliver.method.HttpUtil;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.view.IMainView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static liuliu.dkdjfordeliver.base.BaseApplication.mLocationClient;

/**
 * 给骑士定位
 */
public class MainActivity extends BaseActivity implements IMainView {
    public static MainActivity mInstance;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;
    @CodeNote(id = R.id.bottom_tv)
    TextView bottom_tv;
    MainListener mListener;
    @CodeNote(id = R.id.tabs)
    TabLayout mTabLayout;
    MainFragment new_order;
    MainFragment qu_huo;
    MainFragment dai_song;
    int position = 0;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    public static final String DOWNLOAD_ID = "download_id";
    private DownloadChangeObserver downloadObserver;
    private long lastDownloadId = 0;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    //    private MaterialDialog materialDialog;
    //    private String NetUrl = "http://www.dakedaojia.com/download/deliver003.apk";
    LocalBroadcastManager broadcastManager;
    boolean resu = true;
    @Override
    public void initViews() {
        setContentView(R.layout.activity_main);
        mListener = new MainListener(this);
        mInstance = this;
        new_order = new MainFragment();
        qu_huo = new MainFragment();
        dai_song = new MainFragment();
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);//自定义的code
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(30000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationListener = model -> {
            Map<String, String> map_cache = new HashMap<>();
            map_cache.put("address", model.getAddress());
            map_cache.put("lat", model.getLatitude() + "");
            map_cache.put("lon", model.getLongitude() + "");
            Map<String, String> mpp = new HashMap<String, String>();
            mpp.put("did", Utils.getCache(Config.user_id));
            mpp.put("lat", model.getLatitude() + "");
            mpp.put("lng", model.getLongitude() + "");
            mpp.put("glat", model.getLatitude() + "");
            mpp.put("glng", model.getLongitude() + "");
            HttpUtil.load().pushLatLng(mpp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mm -> {
                        if (("-1").equals(mm.getState())) {
                            ToastShort(mm.getMsg());
                        }
                    }, error -> {
                        ToastShort("请检查网络连接");
                    });
            map_cache.put("cityName", model.getCity());
            Utils.putCache(map_cache);
            if (model.getAddress().contains("朝阳市")) {
                bottom_tv.setText("当前地址：" + model.getAddress().split("朝阳市")[1]);
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("data");
                simpleNotify(msg);
                new_order.refreshList(0);
            }


        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private NotificationManager manger;

    private void simpleNotify(String msg) {
        if (MainActivity.mInstance != null) {
            manger = (NotificationManager) MainActivity.mInstance.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.mInstance);
            builder.setTicker("提示");
            builder.setContentTitle("订单提示");

            if (msg.trim().equals("您有新的个人订单")) {
                builder.setContentText(msg.trim());
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.order));
            } else {
                builder.setContentText("您有新的群组订单");
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.group_order));
            }

            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ykplogoh);
            Intent intent = new Intent(MainActivity.mInstance, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(MainActivity.mInstance, 1, intent, 0);
            //点击跳转的intent
            builder.setContentIntent(pIntent);
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

            Notification notification = builder.build();
            manger.notify(1, notification);
        }
    }

    @Override
    public void initEvents() {
        mListener.checkVersion();//检查版本
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mSectionsPagerAdapter);//给Tabs设置适配器
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                switch (position) {
                    case 0:
                        new_order.refreshList(0);
                        break;
                    case 1:
                        qu_huo.refreshList(1);
                        break;
                    case 2:
                        dai_song.refreshList(2);
                        break;
                }
                mViewPager.setCurrentItem(tab.getPosition()); //解决单击Tab标签无法翻页的问题
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(v -> {//刷新当前list
//            switch (position) {
//                case 0:
//                    new_order.refreshList(0);
//                    break;
//                case 1:
//                    qu_huo.refreshList(1);
//                    break;
//                case 2:
//                    dai_song.refreshList(2);
//                    break;
//            }
//        });
//        fab.setOnLongClickListener(v -> {//跳转到个人中心页面
//            Utils.IntentPost(UserCenterActivity.class);
//            return false;
//        });
    }

    /**
     * 版本检测
     */
    @Override
    public void checkVersion(String url, String content) {
        if (!("").equals(content)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage(content);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DownloadManager dowanloadmanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    //2.创建下载请求对象，并且把下载的地址放进去
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    //3.给下载的文件指定路径
                    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "weixin.apk");
                    //4.设置显示在文件下载Notification（通知栏）中显示的文字。6.0的手机Description不显示
                    request.setTitle("大可到家骑士版");
                    request.setDescription(content);
                    //5更改服务器返回的minetype为android包类型
                    request.setMimeType("application/vnd.android.package-archive");
                    //6.设置在什么连接状态下执行下载操作
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    //7. 设置为可被媒体扫描器找到
                    request.allowScanningByMediaScanner();
                    //8. 设置为可见和可管理
                    request.setVisibleInDownloadsUi(true);
                    if (lastDownloadId == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || lastDownloadId == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                            || lastDownloadId == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                        String packageName = "com.android.providers.downloads";
                        try {
                            // Open the specific App Info page:

                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                            intent.setData(Uri.parse("package:" + packageName));

                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        lastDownloadId = dowanloadmanager.enqueue(request);

                    }
                    //9.保存id到缓存
                    PreferencesUtils.putLong(MainActivity.this, DOWNLOAD_ID, lastDownloadId);
                    //10.采用内容观察者模式实现进度
                    downloadObserver = new DownloadChangeObserver(null);
                    getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
                }
            });
            builder.setPositiveButton("取消", null);
            builder.show();
        }
    }

    @Override
    public void changeResult(double lat, double lng) {
//        if (lat != 0 && lng != 0) {
//            String la = lat + "";
//            String ln = lng + "";
//            if (la.contains(".")) {
//                mAcache.put("lat", la.substring(0, 9));
//            }
//            if (ln.contains(".")) {
//                mAcache.put("lon", ln.substring(0, 10));
//            }
//        }
    }

    @Override
    public void changeStateResult(boolean result) {

    }

    @Override
    public void changeQsState(String result) {

    }


    @Override
    public void userDetail(MessageModel model) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new_order;
                case 1:
                    return qu_huo;
                case 2:
                    return dai_song;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "新任务";
                case 1:
                    return "取货中";
                case 2:
                    return "待送达";
            }
            return null;
        }
    }

    /**
     * 刷新首页
     */
    public static void refreshMain() {
        mViewPager.setCurrentItem(0);
    }

    //用于显示下载进度
    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(lastDownloadId);
            DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                int progress = Math.round(percent * 100);
//                materialDialog.setProgress(progress);
//                if(progress == 100) {
//                    materialDialog.dismiss();
//                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadObserver != null) {
            getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }
}
