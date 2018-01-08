package liuliu.dkdjfordeliver.ui.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.model.MessageModel;

/**
 * Created by Administrator on 2016/12/17.
 */

public class PieChartFragment extends Fragment {
    TextView shouru_tv;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    int position = 0;
    MessageModel.RiBean riBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_piechart, container, false);
        shouru_tv = (TextView) view.findViewById(R.id.shouru_tv);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        tv3 = (TextView) view.findViewById(R.id.tv3);
        tv4 = (TextView) view.findViewById(R.id.tv4);
        tv5 = (TextView) view.findViewById(R.id.tv5);
        tv6 = (TextView) view.findViewById(R.id.tv6);
        load(position, riBean);
        return view;
    }

    public void load(int position, MessageModel.RiBean model) {
        if (shouru_tv != null && model != null) {
            switch (position) {
                case 0:
                    shouru_tv.setText("本日收入(元)\n" + model.getShouRu());
                    break;
                case 1:
                    shouru_tv.setText("本周收入(元)\n" + model.getShouRu());
                    break;
                case 2:
                    shouru_tv.setText("本月收入(元)\n" + model.getShouRu());
                    break;
            }
            tv1.setText("收入： " + model.getShouRu());
            tv2.setText("提成： " + model.getTiCheng());
            tv3.setText("奖励： " + model.getJiangLi());
            tv4.setText("罚款： " + model.getFaKuan());
            tv5.setText("保险： " + model.getBaoXian());
            tv6.setText("其他： " + model.getQiTa());
        } else {
            riBean = model;
        }
    }
}
