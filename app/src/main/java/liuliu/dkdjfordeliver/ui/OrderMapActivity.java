package liuliu.dkdjfordeliver.ui;

import android.os.Bundle;
import android.widget.Button;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;

import net.tsz.afinal.annotation.view.CodeNote;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.method.Utils;

/**
 * Created by Administrator on 2016/10/21.
 */

public class OrderMapActivity extends BaseActivity implements LocationSource, AMap.OnMapLoadedListener {
    /**
     * 基础地图
     */
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMap aMap;
    AMapNavi mAMapNavi;
    @CodeNote(id = R.id.daohang_btn)
    Button daohang_btn;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_order_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
    }

    @Override
    public void initEvents() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        daohang_btn.setOnClickListener(v -> {
            Utils.IntentPost(TestActivity.class);
        });
    }

    /**
     * 设置地图样式
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
        // 设置地图可视缩放大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺
        LatLng latLng = new LatLng(38.894077, 115.507786);
        MarkerOptions otMarkerOptions = new MarkerOptions();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        otMarkerOptions.position(latLng);
        otMarkerOptions.visible(true);//设置可见
        otMarkerOptions.title("芜湖市").snippet("芜湖市：31.383755, 118.438321");//里面的内容自定义
        otMarkerOptions.draggable(true);
        //下面这个是标记上面这个经纬度在地图的位置是
//         otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        aMap.addMarker(otMarkerOptions).showInfoWindow();
        MarkerOptions otMarkerOption = new MarkerOptions();
        otMarkerOption.position(new LatLng(38.890077, 115.547786));
        otMarkerOption.visible(true);//设置可见
        otMarkerOption.title("芜湖市").snippet("芜湖市：31.383755, 118.438321");//里面的内容自定义
        otMarkerOption.draggable(true);//是否可以拖动
        aMap.addMarker(otMarkerOption);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
    }


    @Override
    public void onMapLoaded() {

    }
}
