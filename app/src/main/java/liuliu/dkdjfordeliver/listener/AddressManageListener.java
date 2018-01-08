package liuliu.dkdjfordeliver.listener;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.PoiModel;
import liuliu.dkdjfordeliver.view.IAddressList;
import liuliu.dkdjfordeliver.view.IAddressManage;

/**
 * 地址管理
 * Created by Administrator on 2016/12/2.
 */
interface IAddressMView {
    //加载地址列表
    void loadAddressList();

    //增删改地址
    void addAddress(PoiModel model);
}

public class AddressManageListener {
    IAddressManage mView;//增删改管理
    IAddressList mResult;//地址列表

    public AddressManageListener(IAddressManage mView, IAddressList mResult) {
        this.mView = mView;
        this.mResult = mResult;
    }

    public AddressManageListener(IAddressManage mView) {
        this.mView = mView;
    }

    public AddressManageListener(IAddressList mResult) {
        this.mResult = mResult;
    }



}

