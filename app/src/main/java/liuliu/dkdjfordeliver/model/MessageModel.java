package liuliu.dkdjfordeliver.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */

public class MessageModel implements Serializable {

    /**
     * state : 商家未设置地理位置，请联系市场人员
     */

    private String state;
    private String msg;
    private List<CityModel> citydata;
    /**
     * success : 0
     * errorMsg : 请输入骑士id
     */

    private String success;
    private String errorMsg;
    /**
     * data : {"code":"2632"}
     */

    private DataBean data;
    /**
     * DataId : 31
     * Name : 丁玉强
     * cardid : bbbbbbbbbb
     * IsApproved : 1
     * cardimgZiPai : http://www.dakedaojia.com/uploadZhengJian/31/201612191151237517123.jpg
     * cardimg1 : http://www.dakedaojia.com/uploadZhengJian/31/201612191151242777456.jpg
     */

    private String DataId;
    private String Name;
    private String cardid;
    private String IsApproved;
    private String cardimgZiPai;
    private String cardimg1;
    private String pic;
    /**
     * Ri : {"ShouRu":"0.00","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     * Zhou : {"ShouRu":"0.00","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     * Yue : {"ShouRu":"60.80","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     * Phone : 15042139998
     * JinRiZongMoney : 0.00
     * JinRiZongDanShu : 0
     * JinRiZongGongLi : 0
     */

    private RiBean Ri;
    private RiBean Zhou;
    private RiBean Yue;
    private String Phone;
    private String JinRiZongMoney;
    private String JinRiZongDanShu;
    private String JinRiZongGongLi;

    public RiBean getZhou() {
        return Zhou;
    }

    public void setZhou(RiBean zhou) {
        Zhou = zhou;
    }

    public RiBean getYue() {
        return Yue;
    }

    public void setYue(RiBean yue) {
        Yue = yue;
    }

    /**
     * Ri : {"ShouRu":"0.00","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     * Zhou : {"ShouRu":"0.00","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     * Yue : {"ShouRu":"60.80","TiCheng":"0.00","JiangLi":"0.00","FaKuan":"0.00","BaoXian":"0.00","QiTa":"0.00"}
     */


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataBean getData() {
        return data;
    }

    public List<CityModel> getCitydata() {
        return citydata;
    }

    public void setCitydata(List<CityModel> citydata) {
        this.citydata = citydata;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getDataId() {
        return DataId;
    }

    public void setDataId(String DataId) {
        this.DataId = DataId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(String IsApproved) {
        this.IsApproved = IsApproved;
    }

    public String getCardimgZiPai() {
        return cardimgZiPai;
    }

    public void setCardimgZiPai(String cardimgZiPai) {
        this.cardimgZiPai = cardimgZiPai;
    }

    public String getCardimg1() {
        return cardimg1;
    }

    public void setCardimg1(String cardimg1) {
        this.cardimg1 = cardimg1;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public RiBean getRi() {
        return Ri;
    }

    public void setRi(RiBean Ri) {
        this.Ri = Ri;
    }

//    public ZhouBean getZhou() {
//        return Zhou;
//    }
//
//    public void setZhou(ZhouBean Zhou) {
//        this.Zhou = Zhou;
//    }
//
//    public YueBean getYue() {
//        return Yue;
//    }
//
//    public void setYue(YueBean Yue) {
//        this.Yue = Yue;
//    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getJinRiZongMoney() {
        return JinRiZongMoney;
    }

    public void setJinRiZongMoney(String JinRiZongMoney) {
        this.JinRiZongMoney = JinRiZongMoney;
    }

    public String getJinRiZongDanShu() {
        return JinRiZongDanShu;
    }

    public void setJinRiZongDanShu(String JinRiZongDanShu) {
        this.JinRiZongDanShu = JinRiZongDanShu;
    }

    public String getJinRiZongGongLi() {
        return JinRiZongGongLi;
    }

    public void setJinRiZongGongLi(String JinRiZongGongLi) {
        this.JinRiZongGongLi = JinRiZongGongLi;
    }


    public static class DataBean implements Serializable {
        /**
         * code : 2632
         */

        private String code;
        private String dataID;

        public String getDataID() {
            return dataID;
        }

        public void setDataID(String dataID) {
            this.dataID = dataID;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }


    public static class RiBean implements Serializable {
        /**
         * ShouRu : 0.00
         * TiCheng : 0.00
         * JiangLi : 0.00
         * FaKuan : 0.00
         * BaoXian : 0.00
         * QiTa : 0.00
         */

        private String ShouRu;
        private String TiCheng;
        private String JiangLi;
        private String FaKuan;
        private String BaoXian;
        private String QiTa;

        public String getShouRu() {
            return ShouRu;
        }

        public void setShouRu(String ShouRu) {
            this.ShouRu = ShouRu;
        }

        public String getTiCheng() {
            return TiCheng;
        }

        public void setTiCheng(String TiCheng) {
            this.TiCheng = TiCheng;
        }

        public String getJiangLi() {
            return JiangLi;
        }

        public void setJiangLi(String JiangLi) {
            this.JiangLi = JiangLi;
        }

        public String getFaKuan() {
            return FaKuan;
        }

        public void setFaKuan(String FaKuan) {
            this.FaKuan = FaKuan;
        }

        public String getBaoXian() {
            return BaoXian;
        }

        public void setBaoXian(String BaoXian) {
            this.BaoXian = BaoXian;
        }

        public String getQiTa() {
            return QiTa;
        }

        public void setQiTa(String QiTa) {
            this.QiTa = QiTa;
        }
    }

    public static class ZhouBean implements Serializable {
        /**
         * ShouRu : 0.00
         * TiCheng : 0.00
         * JiangLi : 0.00
         * FaKuan : 0.00
         * BaoXian : 0.00
         * QiTa : 0.00
         */

        private String ShouRu;
        private String TiCheng;
        private String JiangLi;
        private String FaKuan;
        private String BaoXian;
        private String QiTa;

        public String getShouRu() {
            return ShouRu;
        }

        public void setShouRu(String ShouRu) {
            this.ShouRu = ShouRu;
        }

        public String getTiCheng() {
            return TiCheng;
        }

        public void setTiCheng(String TiCheng) {
            this.TiCheng = TiCheng;
        }

        public String getJiangLi() {
            return JiangLi;
        }

        public void setJiangLi(String JiangLi) {
            this.JiangLi = JiangLi;
        }

        public String getFaKuan() {
            return FaKuan;
        }

        public void setFaKuan(String FaKuan) {
            this.FaKuan = FaKuan;
        }

        public String getBaoXian() {
            return BaoXian;
        }

        public void setBaoXian(String BaoXian) {
            this.BaoXian = BaoXian;
        }

        public String getQiTa() {
            return QiTa;
        }

        public void setQiTa(String QiTa) {
            this.QiTa = QiTa;
        }
    }

    public static class YueBean implements Serializable {
        /**
         * ShouRu : 60.80
         * TiCheng : 0.00
         * JiangLi : 0.00
         * FaKuan : 0.00
         * BaoXian : 0.00
         * QiTa : 0.00
         */

        private String ShouRu;
        private String TiCheng;
        private String JiangLi;
        private String FaKuan;
        private String BaoXian;
        private String QiTa;

        public String getShouRu() {
            return ShouRu;
        }

        public void setShouRu(String ShouRu) {
            this.ShouRu = ShouRu;
        }

        public String getTiCheng() {
            return TiCheng;
        }

        public void setTiCheng(String TiCheng) {
            this.TiCheng = TiCheng;
        }

        public String getJiangLi() {
            return JiangLi;
        }

        public void setJiangLi(String JiangLi) {
            this.JiangLi = JiangLi;
        }

        public String getFaKuan() {
            return FaKuan;
        }

        public void setFaKuan(String FaKuan) {
            this.FaKuan = FaKuan;
        }

        public String getBaoXian() {
            return BaoXian;
        }

        public void setBaoXian(String BaoXian) {
            this.BaoXian = BaoXian;
        }

        public String getQiTa() {
            return QiTa;
        }

        public void setQiTa(String QiTa) {
            this.QiTa = QiTa;
        }
    }
}
