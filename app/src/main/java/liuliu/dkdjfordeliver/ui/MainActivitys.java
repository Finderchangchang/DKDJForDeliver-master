package liuliu.dkdjfordeliver.ui;

import android.Manifest;
import android.app.AlarmManager;
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
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import net.tsz.afinal.view.TitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.PreferencesUtils;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.base.BaseApplication;
import liuliu.dkdjfordeliver.listener.MainFragListener;
import liuliu.dkdjfordeliver.listener.MainListener;
import liuliu.dkdjfordeliver.listener.UserListener;
import liuliu.dkdjfordeliver.locationservicedemo.LocationService;
import liuliu.dkdjfordeliver.locationservicedemo.LocationStatusManager;
import liuliu.dkdjfordeliver.method.CommonAdapter;
import liuliu.dkdjfordeliver.method.CommonViewHolder;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.Config;
import liuliu.dkdjfordeliver.model.ListModel;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.model.OrderModel;
import liuliu.dkdjfordeliver.view.IMainFragView;
import liuliu.dkdjfordeliver.view.IMainView;
import liuliu.dkdjfordeliver.view.ISHView;

import static liuliu.dkdjfordeliver.R.id.order_lv;

/**
 * Created by Administrator on 2016/12/12.
 */

public class MainActivitys extends BaseActivity implements IMainView, IMainFragView, ISHView {
    public static MainActivitys mInstance;
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(order_lv)
    ListView orderLv;
    @Bind(R.id.my_order_btn)
    Button myOrderBtn;
    @Bind(R.id.refresh_ll)
    LinearLayout refresh_ll;
    @Bind(R.id.main_srl)
    SwipeRefreshLayout main_srl;
    CommonAdapter<OrderModel> mAdapter;
    List<OrderModel> mList;
    MainFragListener listener;
    @Bind(R.id.tj_sh_ll)
    LinearLayout tjShLl;
    @Bind(R.id.no_data_ll)
    LinearLayout no_data_ll;
    boolean isTG;
    MainListener mainListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @Bind(R.id.nav_view)
    NavigationView nav_view;
    View top_view;
    BroadcastReceiver mItemViewListClickReceiver;
    IntentFilter intentFilter;
    private NotificationManager manger;
    public static final String DOWNLOAD_ID = "download_id";
    private long lastDownloadId = 0;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private MainActivitys.DownloadChangeObserver downloadObserver;
    UserListener userListener;
    private PowerManager.WakeLock wl;
    private PendingIntent pi;

    @Override
    public void initViews() {
        mInstance = this;
        startLocationService();
        setContentView(R.layout.ac_total_main);
        ButterKnife.bind(this);
        //写一个定时的Pendingintent
        Intent intent1 = new Intent();
        intent1.setAction("repeating");

        pi = PendingIntent.getBroadcast(this, 0, intent1, 0);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);//自定义的code
        }
        userListener = new UserListener(this);
        top_view = nav_view.getHeaderView(0);
        LinearLayout btn = (LinearLayout) top_view.findViewById(R.id.auc_ll7);

        //退出登录
        btn.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("确定要退出当前账号？");
            builder.setNegativeButton("确定", (dialog, which) -> {
                mainListener.changeQsState(true);
                Utils.putCache(Config.user_id, "");
                if (broadcastManager != null) {
                    broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
                }
                finish();
            });
            builder.setPositiveButton("取消", null);
            builder.show();
        });
        LinearLayout auc_ll1 = (LinearLayout) top_view.findViewById(R.id.auc_ll1);
        auc_ll1.setOnClickListener(v -> Utils.IntentPost(SHDetailActivity.class));
        LinearLayout auc_ll3 = (LinearLayout) top_view.findViewById(R.id.auc_ll3);
        auc_ll3.setOnClickListener(v -> Utils.IntentPost(JvBaoActivity.class));
        LinearLayout auc_ll2 = (LinearLayout) top_view.findViewById(R.id.auc_ll2);//规则
        auc_ll2.setOnClickListener(v -> Utils.IntentPost(WebActivity.class, intent -> intent.putExtra("web", "规则")));
        LinearLayout auc_ll6 = (LinearLayout) top_view.findViewById(R.id.auc_ll6);//关于
        auc_ll6.setOnClickListener(v -> Utils.IntentPost(WebActivity.class, intent -> intent.putExtra("web", "关于")));
        LinearLayout auc_ll4 = (LinearLayout) top_view.findViewById(R.id.auc_ll4);
        auc_ll4.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "04212910555"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        isTG = Utils.getBooleanCache("istg");
        //启动定位服务
        //startLocationService();

        listener = new MainFragListener(this);
        mainListener = new MainListener(this);
        mainListener.getUserCenter();
        mainListener.checkVersion();//检查版本
        builder = new AlertDialog.Builder(this);
        myOrderBtn.setOnClickListener(v -> {
            if (isTG) {
                Utils.IntentPost(MyOrderActivity.class);
            }
        });
        titleBar.setLeftClick(() -> leftManager());
        mainListener.changeQsState(right_click);
        //收工
        titleBar.setRightSWClick(() -> {
            builder.setTitle("提示");
            builder.setMessage("确定要" + (!right_click ? "接单" : "关闭") + "吗？");
            builder.setNegativeButton("确定", (dialog, which) -> {
                mainListener.changeQsState(right_click);
            });
            builder.setPositiveButton("取消", null);
            builder.show();
        });
        mAdapter = new CommonAdapter<OrderModel>(this, mList, R.layout.item_orders) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                Utils.putCache("slat", model.getSlat());//商家坐标
                Utils.putCache("slng", model.getSlng());
                holder.setText(R.id.qh_tv, model.getShopname());
                holder.setText(R.id.xx_address_tv, model.getTogoAddress());
                holder.setText(R.id.fb_time_tv, model.getAddtime());
                holder.setText(R.id.remark_tv, model.getOrderAttach());
                holder.setText(R.id.tj_tv, model.getTiji());
                holder.setText(R.id.zl_tv, model.getZhongliang());
                holder.setText(R.id.car_type_tv, model.getChename());
                //图片空，不是图片订单，距离，用户，价钱，不为空。接单按钮才显示。电话需要不自己填写
                if (model.getImgOrder() == null || model.getImgOrder().equals("") || model.getImgOrder().length() < 10 || model.getImgOrder() == "") {
                    holder.setText(R.id.jl1_tv, model.getJuLi());
                    holder.setText(R.id.jl2_tv, model.getSJDaoYH());
                    holder.setText(R.id.sh_tv, model.getAddress());
                    holder.setText(R.id.sr_tv, model.getSendFee());
                    holder.SetState(R.id.left_btn, true);
                } else {//有图片
                    holder.SetState(R.id.left_btn, false);
                    holder.SetState(R.id.picture_ll, true);//设置图片所在的布局可见
                    holder.instanImageview(R.id.picture_iv, MainActivitys.this, model.getImgOrder());//设置显示图片
                    holder.setOnClickListener(R.id.picture_ll, v -> {
                        Intent intent = new Intent(MainActivitys.this, EditOrderFromPhoto.class);
                        intent.putExtra("url", model.getImgOrder());
                        intent.putExtra("orderid", model.getOrderid());
                        startActivity(intent);
                    });
                }
                holder.setOnClickListener(R.id.left_btn, v -> {
                    builder.setTitle("提示");
                    builder.setMessage("确定要接此单？");
                    builder.setNegativeButton("确定", (dialog, which) -> {
                        mainListener.qiangDan("1", model.getOrderid());
                    });
                    builder.setPositiveButton("取消", null);
                    builder.show();
                });
                holder.setOnClickListener(R.id.tel1_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getPotioncomm()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
                holder.setOnClickListener(R.id.tel2_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getOrderComm()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        };
        orderLv.setAdapter(mAdapter);
        orderLv.setOnItemClickListener((parent, view, position, id) -> {
            Utils.IntentPost(OrderDetailActivitys.class, listener -> {
                listener.putExtra("orderid", mList.get(position).getOrderid());
                listener.putExtra("foodno", mList.get(position).getFoodNo());
            });
        });
        refresh_ll.setOnClickListener(v -> {
            if (right_click) {
                refreshUI();
            }
        });
        main_srl.setOnRefreshListener(() -> {
                    if (right_click && isTG) {
                        refreshUI();
                    }
                }
        );
        broadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("data");
                simpleNotify(msg);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
        //if (!isTG) {
        userListener.getSHDetail();//获得审核信息
        //}
        //每隔30刷新一次列表列表的
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }, 0, 30000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 开始定位服务
     */
    private void startLocationService() {
        Intent intent = new Intent(this, dingwei.class);
        startService(intent);
    }

    /**
     * 关闭服务
     * 先关闭守护进程，再关闭定位服务
     */
    private void stopLocationService() {
        sendBroadcast(liuliu.dkdjfordeliver.locationservicedemo.Utils.getCloseBrodecastIntent());
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (Utils.isNetworkAvailable(MainActivitys.this)) {
                refreshUI();
            }
        }
    };


    //订单推送提示
    private void simpleNotify(String msg) {
        if (MainActivitys.mInstance != null) {
            manger = (NotificationManager) MainActivitys.mInstance.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivitys.mInstance);
            builder.setTicker("提示");
            builder.setContentTitle("订单提示");

            if (msg.trim().equals("您有新的个人订单")) {
                builder.setContentText(msg.trim());
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.rengongpaidan));
            } else if (msg.trim().equals("后台取消订单")) {
                builder.setContentText("您的订单已取消");
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.orderquxiao));
            } else if (msg.trim().equals("系统派单")) {
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.xitongpaidan));
            } else {
                builder.setContentText("您有新的群组订单");
                builder.setSound(Uri.parse("android.resource://" + BaseApplication.getContext().getPackageName() + "/" + R.raw.group_order));
            }

            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ykplogoh);
            Intent intent = new Intent(MainActivitys.mInstance, MainActivitys.class);

            //点击跳转的intent
            builder.setContentIntent(pi);
            // builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

            Notification notification = builder.build();
            manger.notify(1, notification);
        }
    }

    LocalBroadcastManager broadcastManager;

    private void leftManager() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            drawer_layout.openDrawer(GravityCompat.START);
        }
    }

    AlertDialog.Builder builder;

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                break;
        }
    }

    /**
     * 刷新当前ui
     */
    private void refreshUI() {
        if (isTG) {
            main_srl.setRefreshing(true);
            listener.loadOrder(1, "0");
        } else {
            broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
            mAdapter.refresh(new ArrayList<>());
            orderLv.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
        titleBar.setRightBtn(isTG);
//        tjShLl.setVisibility(isTG ? View.GONE : View.VISIBLE);
        tjShLl.setVisibility(View.GONE);
        tjShLl.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ToastShort("请在系统管理中开始相机权限");
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                }
                startActivity(localIntent);
            } else {
                startActivityForResult(new Intent(MainActivitys.mInstance, CheckUserActivity.class), 11);
            }
        });//提交审核资质
        orderLv.setVisibility(isTG ? View.VISIBLE : View.GONE);
        if (isTG) {
            broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
        } else {
            broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
        }
    }

    @Override
    public void initEvents() {

    }

    @Override
    public void refreshOrder(List<OrderModel> list) {
        main_srl.setRefreshing(false);
        if (list != null) {
            mAdapter.refresh(list);
            if (list.size() > 0) {
                no_data_ll.setVisibility(View.GONE);
                orderLv.setVisibility(View.VISIBLE);
                mList = list;
            } else {
                orderLv.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.refresh(new ArrayList<>());
            orderLv.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refresh(ListModel list) {

    }

    boolean right_click = false;

    @Override
    public void loadMoreOrder(List<OrderModel> list) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (11 == resultCode) {
            mainListener.getUserCenter();
        }
    }

    @Override
    public void checkVersion(String url, String content) {
        if (!("").equals(content)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivitys.this);
            builder.setTitle("提示");
            builder.setMessage(content);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DownloadManager dowanloadmanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    //2.创建下载请求对象，并且把下载的地址放进去
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    //3.给下载的文件指定路径
                    request.setDestinationInExternalFilesDir(MainActivitys.this, Environment.DIRECTORY_DOWNLOADS, "weixin.apk");
                    //4.设置显示在文件下载Notification（通知栏）中显示的文字。6.0的手机Description不显示
                    request.setTitle(getResources().getString(R.string.app_name));
                    request.setDescription(content);
                    //5更改服务器返回的minetype为android包类型
                    request.setMimeType("application/vnd.android.package-archive");
                    //6.设置在什么连接状态下执行下载操作
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    //7. 设置为可被媒体扫描器找到
                    request.allowScanningByMediaScanner();
                    //8. 设置为可见和可管理


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
                    request.setVisibleInDownloadsUi(true);
                    //9.保存id到缓存
                    PreferencesUtils.putLong(MainActivitys.this, DOWNLOAD_ID, lastDownloadId);
                    //10.采用内容观察者模式实现进度
                    downloadObserver = new MainActivitys.DownloadChangeObserver(null);
                    getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
                }
            });
            builder.setPositiveButton("取消", null);
            builder.show();
        }
    }

    @Override
    public void changeResult(double lat, double lng) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isTG) {
            mainListener.getUserCenter();
        } else {
            userListener.getSHDetail();//获得审核信息
        }
    }

    @Override
    public void changeStateResult(boolean result) {
        if (result) {
            refreshUI();
        }
    }

    @Override
    public void changeQsState(String result) {
        if (("成功").equals(result)) {
            right_click = !right_click;
            no_data_ll.setVisibility(right_click ? View.GONE : View.VISIBLE);
            orderLv.setVisibility(right_click ? View.VISIBLE : View.GONE);
            titleBar.setRightButtonClick(right_click);
            if (right_click) {
                broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
            } else {
                broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
            }
        } else {
            ToastShort(result);
        }
    }

    @Override
    public void getUser(MessageModel model) {
        if (model != null) {
            if (("0").equals(model.getIsApproved())) {
                Utils.putBooleanCache("istg", true);
                isTG = true;
            } else {
                Utils.putBooleanCache("istg", false);
                isTG = false;
            }
            refreshUI();
        }
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
            }
        }
    }

    @Override
    public void userDetail(MessageModel model) {
        if (model != null) {
            TextView user_name_tv = (TextView) top_view.findViewById(R.id.user_name_tv);
            TextView user_tel_tv = (TextView) top_view.findViewById(R.id.user_tel_tv);
            user_tel_tv.setText("手机号" + model.getPhone());
            user_name_tv.setText(model.getName());
            TextView sr_tv = (TextView) top_view.findViewById(R.id.sr_tv);
            sr_tv.setText(model.getJinRiZongMoney());
            TextView ds_tv = (TextView) top_view.findViewById(R.id.ds_tv);
            ds_tv.setText(model.getJinRiZongDanShu());
            TextView gl_tv = (TextView) top_view.findViewById(R.id.gl_tv);
            gl_tv.setText(model.getJinRiZongGongLi());
            LinearLayout user_center_ll = (LinearLayout) top_view.findViewById(R.id.user_center_ll);
            user_center_ll.setOnClickListener(v -> Utils.IntentPost(AcountCenterActivity.class, intent -> intent.putExtra("model", model)));
        }
    }
}