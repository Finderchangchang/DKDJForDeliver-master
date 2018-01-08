package liuliu.dkdjfordeliver.method;

import java.util.Map;

import liuliu.dkdjfordeliver.model.GDToBD;
import liuliu.dkdjfordeliver.model.ListModel;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.model.NormalMessageModel;
import liuliu.dkdjfordeliver.model.OrderModel;
import liuliu.dkdjfordeliver.model.UpdateModel;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/20.
 */

public interface GitHubAPI {
    String url = "app/Android/deliver/";

    @GET("app/GetuiLogin.aspx")
    Observable<NormalMessageModel> userInfo(@QueryMap Map<String, String> map);

    @GET("download/version.aspx?c=3")
    Observable<UpdateModel> checkUpdate();

    //获得订单列表
    @GET("App/Cpaotui/GetCityList.aspx")
    Observable<MessageModel> getCityList();

    //获得审核详情
    @GET("App/Android/Deliver/DeliverGeRenZiLiao.aspx")
    Observable<MessageModel> getSHDetail(@Query("DataId") String id);

    //修改骑士状态
    @GET(url + "GaiBianDeliverStatus.aspx")
    Observable<MessageModel> changeUserState(@QueryMap Map<String, String> map);

    //上传图片
    @GET(url + "AFanHui.aspx")
    Observable<MessageModel> checkUser(@QueryMap Map<String, String> map);

    //修改骑士密码
    @GET(url + "UpdatePassword.aspx")
    Observable<OrderModel> changePwd(@QueryMap Map<String, String> map);

    //获得验证码 phone手机号  type类型（0注册手机号 1找回密码）
    @GET(url + "DeliverZhuCeCode.aspx")
    Observable<MessageModel> getCode(@QueryMap Map<String, String> map);

    //骑士举报
    @GET(url + "DeliverJuBao.aspx")
    Observable<MessageModel> qs_jb(@QueryMap Map<String, String> map);

    //骑士注册 username手机号账号  password密码  type类型（0注册手机号  1找回密码）返回参数  dataID骑士id
    @GET(url + "DeliverZhuCe.aspx")
    Observable<MessageModel> regUser(@QueryMap Map<String, String> map);

    //抢单操作
    @GET(url + "deliverreceiveorder.aspx")
    Observable<OrderModel> qiangDan(@QueryMap Map<String, String> map);

    //根据用户ID获得订单信息
    @GET(url + "GetOrderListByUserId.aspx")
    Observable<ListModel> getOrders(@QueryMap Map<String, String> map);

    //根据用户id获得用户信息
    @GET(url + "DeliverZhangHuZhongXin.aspx")
    Observable<MessageModel> getUserDetailById(@Query("DataId") String map);

    //根据订单编号获得订单详细信息
    @GET(url + "GetOrderDetailByOrderId.aspx")
    Observable<OrderModel> getOrderByOrderId(@Query("orderid") String map);

    //根据时间获得历史订单
    @GET(url + "DeliverLiShiCustorderList.aspx")
    Observable<ListModel> getHistoryByTime(@QueryMap Map<String, String> map);

    //更改订单状态
    @GET(url + "saveorderstate.aspx")
    Observable<OrderModel> changeOrderState(@QueryMap Map<String, String> map);

    //提交骑士信息到后台
    @GET(url + "saveorderstate.aspx")
    Observable<OrderModel> putDeliverMsg(@QueryMap Map<String, String> map);

    //?coords=115.4958,38.8869&from=1&to=5&ak=fNa5uQ9a7q8ygX2PVeG8BLRTACsFGffy
    @GET("geoconv/v1/")
    Observable<GDToBD> gdToBd(@QueryMap Map<String, String> map);

    @GET(url + "AddLatLng.aspx")
    Observable<MessageModel> pushLatLng(@QueryMap Map<String, String> map);
    @GET(url + "kuaipaoAddOrderDeliver.aspx")
    Observable<MessageModel> comitOrder(@QueryMap Map<String,String> map);
}
