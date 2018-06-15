package liuliu.dkdjfordeliver.model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class OrderModel {
    private String orderid;
    private String shopname;
    private String addtime;
    private String DispatchTime;
    private String totalmoney;
    private String state;
    private String address;
    private String sendstate;
    private String ordertype;
    private String TogoAddress;
    private String OrderAttach;
    private String OrderRcver;
    private String deliverid;
    private List<GoodModel> list;
    private String msg;
    private String phone;
    private String SentTime;
    private String ReachTime;
    private String userid;
    private String foodinorderString;
    private int people;
    private String username;
    private String shopid;
    private String Packagefree;
    private String sentmoney;
    private String realname;
    private String note;
    private String shopaddress;
    private String shoptel;
    private String PayMode;
    private String paymoney;
    private String OrderTotal;
    private String oldprice;
    private String promotionmoney;
    private String cardpay;
    private String IsShopSet;
    private String paystate;
    private String ulat;
    private String ulng;
    private String slat;
    private String slng;
    private String FoodNo;
    private String JuLi;
    private String SJDaoYH;
    private String SendFee;
    private String Promotions;
    private String dname;
    private String dphone;
    private String dglog;
    private String dglat;

    private String tiji;
    private String zhongliang;
    private String cheid;
    private String chename;
    private String chemoney;


    private String ImgOrder;//图片订单的图片字段
    private String ImgNumber;

    public String getImgNumber() {
        return ImgNumber;
    }

    public void setImgNumber(String imgNumber) {
        ImgNumber = imgNumber;
    }


    /**
     * OrderComm : 17093215800
     * potioncomm : 15042139998
     */

    private String OrderComm;
    private String potioncomm;
    private String JieDanTime;

    public String getImgOrder() {
        return ImgOrder;
    }//得到图片

    public void setImgOrder(String imgOrder) {
        ImgOrder = imgOrder;
    }

    public String getJieDanTime() {
        return JieDanTime;
    }

    public void setJieDanTime(String jieDanTime) {
        JieDanTime = jieDanTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getDispatchTime() {
        return DispatchTime;
    }

    public void setDispatchTime(String DispatchTime) {
        this.DispatchTime = DispatchTime;
    }

    public String getTotalmoney() {
        return totalmoney;
    }

    public void setTotalmoney(String totalmoney) {
        this.totalmoney = totalmoney;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSendstate() {
        return sendstate;
    }

    public void setSendstate(String sendstate) {
        this.sendstate = sendstate;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getTogoAddress() {
        return TogoAddress;
    }

    public void setTogoAddress(String TogoAddress) {
        this.TogoAddress = TogoAddress;
    }

    public String getOrderAttach() {
        return OrderAttach;
    }

    public void setOrderAttach(String OrderAttach) {
        this.OrderAttach = OrderAttach;
    }

    public String getOrderRcver() {
        return OrderRcver;
    }

    public void setOrderRcver(String OrderRcver) {
        this.OrderRcver = OrderRcver;
    }

    public String getDeliverid() {
        return deliverid;
    }

    public void setDeliverid(String deliverid) {
        this.deliverid = deliverid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSentTime() {
        return SentTime;
    }

    public void setSentTime(String SentTime) {
        this.SentTime = SentTime;
    }

    public String getReachTime() {
        return ReachTime;
    }

    public void setReachTime(String ReachTime) {
        this.ReachTime = ReachTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFoodinorderString() {
        return foodinorderString;
    }

    public void setFoodinorderString(String foodinorderString) {
        this.foodinorderString = foodinorderString;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getPackagefree() {
        return Packagefree;
    }

    public void setPackagefree(String Packagefree) {
        this.Packagefree = Packagefree;
    }

    public String getSentmoney() {
        return sentmoney;
    }

    public void setSentmoney(String sentmoney) {
        this.sentmoney = sentmoney;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public String getShoptel() {
        return shoptel;
    }

    public void setShoptel(String shoptel) {
        this.shoptel = shoptel;
    }

    public String getPayMode() {
        return PayMode;
    }

    public void setPayMode(String PayMode) {
        this.PayMode = PayMode;
    }

    public String getPaymoney() {
        return paymoney;
    }

    public void setPaymoney(String paymoney) {
        this.paymoney = paymoney;
    }

    public String getOrderTotal() {
        return OrderTotal;
    }

    public void setOrderTotal(String OrderTotal) {
        this.OrderTotal = OrderTotal;
    }

    public String getOldprice() {
        return oldprice;
    }

    public void setOldprice(String oldprice) {
        this.oldprice = oldprice;
    }

    public String getPromotionmoney() {
        return promotionmoney;
    }

    public void setPromotionmoney(String promotionmoney) {
        this.promotionmoney = promotionmoney;
    }

    public String getCardpay() {
        return cardpay;
    }

    public void setCardpay(String cardpay) {
        this.cardpay = cardpay;
    }

    public String getIsShopSet() {
        return IsShopSet;
    }

    public void setIsShopSet(String IsShopSet) {
        this.IsShopSet = IsShopSet;
    }

    public String getPaystate() {
        return paystate;
    }

    public void setPaystate(String paystate) {
        this.paystate = paystate;
    }

    public String getUlat() {
        return ulat;
    }

    public void setUlat(String ulat) {
        this.ulat = ulat;
    }

    public String getUlng() {
        return ulng;
    }

    public void setUlng(String ulng) {
        this.ulng = ulng;
    }

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getSlng() {
        return slng;
    }

    public void setSlng(String slng) {
        this.slng = slng;
    }

    public String getPromotions() {
        return Promotions;
    }

    public void setPromotions(String Promotions) {
        this.Promotions = Promotions;
    }

    public List<GoodModel> getList() {
        return list;
    }

    public void setList(List<GoodModel> list) {
        this.list = list;
    }

    public String getOrderComm() {
        return OrderComm;
    }

    public void setOrderComm(String OrderComm) {
        this.OrderComm = OrderComm;
    }

    public String getPotioncomm() {
        return potioncomm;
    }

    public void setPotioncomm(String potioncomm) {
        this.potioncomm = potioncomm;
    }

    public String getFoodNo() {
        return FoodNo;
    }

    public void setFoodNo(String foodNo) {
        FoodNo = foodNo;
    }

    public String getJuLi() {
        return JuLi;
    }

    public void setJuLi(String juLi) {
        JuLi = juLi;
    }

    public String getSJDaoYH() {
        return SJDaoYH;
    }

    public void setSJDaoYH(String SJDaoYH) {
        this.SJDaoYH = SJDaoYH;
    }

    public String getSendFee() {
        return SendFee;
    }

    public void setSendFee(String sendFee) {
        SendFee = sendFee;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDphone() {
        return dphone;
    }

    public void setDphone(String dphone) {
        this.dphone = dphone;
    }

    public String getDglog() {
        return dglog;
    }

    public void setDglog(String dglog) {
        this.dglog = dglog;
    }

    public String getDglat() {
        return dglat;
    }

    public void setDglat(String dglat) {
        this.dglat = dglat;
    }

    public String getTiji() {
        return tiji;
    }

    public void setTiji(String tiji) {
        this.tiji = tiji;
    }

    public String getZhongliang() {
        return zhongliang;
    }

    public void setZhongliang(String zhongliang) {
        this.zhongliang = zhongliang;
    }

    public String getCheid() {
        return cheid;
    }

    public void setCheid(String cheid) {
        this.cheid = cheid;
    }

    public String getChename() {
        return chename;
    }

    public void setChename(String chename) {
        this.chename = chename;
    }

    public String getChemoney() {
        return chemoney;
    }

    public void setChemoney(String chemoney) {
        this.chemoney = chemoney;
    }
}
