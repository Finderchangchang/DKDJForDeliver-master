package liuliu.dkdjfordeliver.model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class ListModel {

    /**
     * page : 1
     * total : 1
     * record : 2
     * orderlist : [{"orderid":"16101014124058000","shopname":"丁玉强海鲜店(测试)","addtime":"2016-10-10 14:12:10","DispatchTime":"","totalmoney":"2.0","state":"7","address":"河北省,保定市,北市区,城苑路,427号|向阳驾校(城苑路425)|22","sendstate":"1","ordertype":"1","TogoAddress":"中国河北省保定市莲池区城苑路与李庄街交叉路口袋鞋对面鹏翔小区1号楼1单元202室","OrderAttach":"","OrderRcver":"就为了 先生","deliverid":"31"},{"orderid":"16072122014658000","shopname":"丁玉强海鲜店(测试)","addtime":"2016-07-21 22:01:21","DispatchTime":"","totalmoney":"8.0","state":"7","address":"|河北班|123","sendstate":"2","ordertype":"1","TogoAddress":"中国河北省保定市莲池区城苑路与李庄街交叉路口袋鞋对面鹏翔小区1号楼1单元202室","OrderAttach":"","OrderRcver":"柳伟杰 先生","deliverid":"31"}]
     */

    private String page;
    private String total;
    private String record;
    private List<OrderModel> orderlist;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public List<OrderModel> getOrderlist() {
        return orderlist;
    }

    public void setOrderlist(List<OrderModel> orderlist) {
        this.orderlist = orderlist;
    }
}
