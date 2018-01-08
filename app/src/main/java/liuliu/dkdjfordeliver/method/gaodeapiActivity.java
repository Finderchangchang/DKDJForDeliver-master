package liuliu.dkdjfordeliver.method;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.NaviPara;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.AoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.SetImage.EventBusClass;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.base.BaseApplication;
import liuliu.dkdjfordeliver.listener.AddressManageListener;
import liuliu.dkdjfordeliver.model.PoiModel;
import liuliu.dkdjfordeliver.ui.BaseFragmentFromBussiness;
import liuliu.dkdjfordeliver.util.AMapUtil;
import liuliu.dkdjfordeliver.util.ToastUtil;

/**
 * Created by 女神 on 2017/8/7.
 */

public class gaodeapiActivity extends BaseActivity implements AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, TextWatcher,
        PoiSearch.OnPoiSearchListener, View.OnClickListener, Inputtips.InputtipsListener,LocationSource{

    private AutoCompleteTextView searchText;// 输入搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private int currentPage = 0;// 当前页面，从0开始计数
    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private TextView SendAddressTv,NoAddressTv;
    //定位当前位置


    private MapView mMapView;
    AMap aMap;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mLocationClient = null;
    AMapLocation mapLocation;
    Dialog dialog;
    PoiModel model;
    String point_title,point_address;
    LatLonPoint point_latlng;
    BaseFragmentFromBussiness fujin_bf;
    public static gaodeapiActivity mIntails;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    ListView lv;
    double nowlat,nowlng;//移动地图直接定位的横纵坐标
    AddressManageListener listener;
    double latitude,longitude;
    Button searButton,chooseonButton;
    @Override
    public void initViews() {
        setContentView(R.layout.ac_gaodeapi);
        mMapView = (MapView) findViewById(R.id.map);
        SendAddressTv= (TextView) findViewById(R.id.send_address_tv);
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        NoAddressTv= (TextView) findViewById(R.id.no_address_tv);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
        lv= (ListView) findViewById(R.id.address_list_lv);
        searButton = (Button) findViewById(R.id.searchButton);
        chooseonButton= (Button) findViewById(R.id.chooseon_button);


    }
    List<PoiModel> pois;//要传递的
    @Override
    public void initEvents() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = getDefaultOption();
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(locationListener);
        mLocationClient.startLocation();
        mMapView.onCreate(savedInstanceState);// 必须要写
        searButton.setOnClickListener(this);//搜索按钮
        chooseonButton.setOnClickListener(this);
        dialog = Utils.ProgressDialog(this, "定位中，请稍后...", true);
        dialog.show();
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        //aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式


    }
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //启动定位

        return mLocationOption;
    }
    /**
     * 设置页面监听
     */
    private void setUpMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMyLocationEnabled(true);// 可触发定位并显示定位层
            mUiSettings = aMap.getUiSettings();//实例化UiSettings类
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),17));//将地图移动到定位点

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener(){
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
//                    SendAddressTv.setVisibility(View.GONE);
//                    NoAddressTv.setVisibility(View.VISIBLE);
//                    NoAddressTv.setText("正在获取当前位置");
                }
                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    pois = new ArrayList<>();
                    LatLng latLng = cameraPosition.target;
                    GeocodeSearch geocoderSearch = new GeocodeSearch(BaseApplication.getContext());//传入context
                    nowlat=latLng.latitude;//移动地图对随时获得的横纵坐标
                    nowlng=latLng.longitude;
                    LatLonPoint latLonPoint = new LatLonPoint(nowlat, nowlng);
                    // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                    RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                    geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                        @Override
                        public void onRegeocodeSearched(RegeocodeResult result, int resultcode) {
                            if (resultcode == 1000) {
                                List<AoiItem> poiItems = result.getRegeocodeAddress().getAois();
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                if (poiItems.size() > 0) {
                                    Log.e("size",poiItems.size()+"");
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    //检索附近poi
                                    String s = result.getRegeocodeAddress().getDistrict() + result.getRegeocodeAddress().getStreetNumber().getStreet() +
                                            result.getRegeocodeAddress().getStreetNumber().getNumber();
                                    pois.add(new PoiModel(poiItems.get(0).getAoiName(), s, "0", poiItems.get(0).getAoiCenterPoint().getLatitude(), poiItems.get(0).getAoiCenterPoint().getLongitude()));
                                }
                                List<PoiItem> aois = result.getRegeocodeAddress().getPois();
                                for (PoiItem model : aois) {
                                    pois.add(new PoiModel(model.getTitle(), model.getSnippet(), model.getDistance() + "", model.getLatLonPoint().getLatitude(), model.getLatLonPoint().getLongitude()));
                                }
                                SetPoiList(pois);

                                if (!click) {
                                    if (pois.size() > 0) {
                                        SendAddressTv.setText(pois.get(0).getPoiName());
                                        poiModel = new PoiModel(pois.get(0).getPoiName(), pois.get(0).getPoiAddress(), "0", pois.get(0).getLat(), pois.get(0).getLng());
                                        click = false;
                                    } else {
                                        click = false;
                                    }
                                } else {
                                    click = false;
                                }
                            }else {
                                NoAddressTv.setText("没有位置信息，请尝试其他结果");
                                SendAddressTv.setVisibility(View.GONE);
                                NoAddressTv.setVisibility(View.VISIBLE);
                                }

                        }

                        @Override
                        public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

                        }
                    });
                    geocoderSearch.getFromLocationAsyn(query);
                }
            });

    }
    boolean click;
    PoiModel poiModel;
    CommonAdapter mAdapter;
    /**
     * 通知顶部文字改变
     */
    public void load(boolean itemClick, PoiModel model) {
        click = itemClick;
        poiModel = model;
        if (model != null) {
            SendAddressTv.setText(model.getPoiName());//地址名
            //address_desc_tv.setText(model.getPoiAddress()); //详细地址
            if (itemClick) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLat(), model.getLng()), 16));

            }
        }
    }
    boolean result;
    //boolean sc;


    public void loadPoint(List<PoiModel> list) {
        mAdapter.refresh(list);


    }

    //加载poi地址列表并赋值给lv
    public void SetPoiList(List<PoiModel> poiss){

        if (poiss == null) {
            poiss = new ArrayList<>();
        }
        List<PoiModel> finalPoiss1 = poiss;
        mAdapter = new CommonAdapter<PoiModel>(BaseApplication.getContext(), finalPoiss1, R.layout.item_poi) {
            @Override
            public void convert(CommonViewHolder holder, PoiModel model, int position) {
               // mAdapter.refresh(finalPoiss1);
                holder.setText(R.id.address_title_tv, model.getPoiName());
                if (model.getPoiAddress().length() > 20) {//如果地名长度大于20就变成...
                    holder.setText(R.id.address_desc_tv, model.getPoiAddress().substring(0, 20) + "...");
                } else {
                    holder.setText(R.id.address_desc_tv, model.getPoiAddress());//不大于20显示全地名
                }
                if (model.getJvli() != null) {//如果有距离就显示
                    holder.setText(R.id.jvli_tv, model.getJvli() + "米");
                } else {
                    holder.setText(R.id.jvli_tv, "");//没有距离就不显示
                }
            }
        };
        lv.setAdapter(mAdapter);
        List<PoiModel> finalPoiss = poiss;
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            if (finalPoiss.size() > 0) {
              //  load(true, finalPoiss.get(position));//中心的SendAddressTv显示lv选择的地名
                EventBus.getDefault().post(new EventBusClass(finalPoiss.get(position).getPoiName()));//向EditOrderFromPhoto发送地名
                double lastLat=finalPoiss.get(position).getLat();
                double lastlng=finalPoiss.get(position).getLng();
                Utils.putCache("ulat",lastLat+"");
                Utils.putCache("ulng",lastlng+"");

                gaodeapiActivity.this.finish();
            } else {
                load(false, null);
            }
        });
        if (result) {
            mAdapter.refresh(poiss);
        }
    /*    if (sc) {
            if (listener == null) {
                listener = new AddressManageListener(this);
            }
            //listener.loadAddressList();
        }
*/
    }

    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = AMapUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(gaodeapiActivity.this, "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }



    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "朝阳市");
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起高德地图app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAMapNavi(marker);
            }
        });
        return view;
    }

    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     */
    public void startAMapNavi(Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, getApplicationContext());
        } catch (com.amap.api.maps2d.AMapException e) {

            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getApplicationContext());

        }

    }

    /**
     * 判断高德地图app是否已经安装
     */
    public boolean getAppIn() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(
                    "com.autonavi.minimap", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        // 本手机没有安装高德地图app
        if (packageInfo != null) {
            return true;
        }
        // 本手机成功安装有高德地图app
        else {
            return false;
        }
    }

    /**
     * 获取当前app的应用名字
     */
    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager
                .getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(gaodeapiActivity.this, infomation);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, "朝阳");
            Inputtips inputTips = new Inputtips(gaodeapiActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(gaodeapiActivity.this,
                                "对不起，没有搜索到相关数据！");
                    }
                }
            } else {
                ToastUtil.show(gaodeapiActivity.this,
                        "对不起，没有搜索到相关数据！");
            }
        } else {
            ToastUtil.showerror(gaodeapiActivity.this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }

    /**
     * Button点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击搜索按钮
             */
            case R.id.searchButton:
                searchButton();
                break;
            case R.id.chooseon_button:
                ChooseOn();
                break;

            default:
                break;
        }
    }
     /**选定当前地址、
      * 设置显示当前的地名
      * 将横纵坐标传入Utils*/
    private void ChooseOn() {
        try {
            EventBus.getDefault().post(new EventBusClass(SendAddressTv.getText().toString().trim()));//向EditOrderFromPhoto发送地名
            Utils.putCache("ulat",nowlat+"");
            Utils.putCache("ulng",nowlng+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        gaodeapiActivity.this.finish();
    }


    /**搜地点列表*/
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(gaodeapiActivity.this, rCode);
        }


    }
    //声明定位回调监听器
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                     latitude = aMapLocation.getLatitude();//获取纬度
                     longitude = aMapLocation.getLongitude();//获取经度

                        setUpMap();


                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

/*    @Override
    public void loadAddressResult(List<PoiModel> list) {
        pois = list;
        if (list != null) {
            mAdapter.refresh(list);
        }
    }*/
}
