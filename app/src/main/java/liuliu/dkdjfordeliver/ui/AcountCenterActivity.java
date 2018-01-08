package liuliu.dkdjfordeliver.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.tsz.afinal.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.MessageModel;
import liuliu.dkdjfordeliver.ui.fragement.PieChartFragment;

/**
 * Created by Administrator on 2016/12/16.
 */

public class AcountCenterActivity extends BaseActivity {
    ViewPager viewPager;
    private RadioGroup radioGroup_main;
    private RadioButton[] arrRadioButton = null;
    private List<Fragment> totalList = new ArrayList<Fragment>();
    PieChartFragment pieChartFragment1, pieChartFragment2, pieChartFragment3;
    MessageModel model;
    TextView tv3;
    TitleBar title_bar;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_account_center);
        viewPager = (ViewPager) findViewById(R.id.ac_viewpager);
        radioGroup_main = (RadioGroup) findViewById(R.id.ac_rg);
        model = (MessageModel) getIntent().getSerializableExtra("model");
        tv3 = (TextView) findViewById(R.id.tv3);
        title_bar = (TitleBar) findViewById(R.id.title_bar);
        title_bar.setLeftClick(() -> finish());
        tv3.setOnClickListener(v -> Utils.IntentPost(HistoryActivity.class));
    }

    @Override
    public void initEvents() {
        arrRadioButton = new RadioButton[3];
        for (int i = 0; i < 3; i++) {
            if (i == 0)
                ((RadioButton) radioGroup_main.getChildAt(i)).setChecked(true);
            arrRadioButton[i] = (RadioButton) radioGroup_main.getChildAt(i);
        }
        pieChartFragment1 = new PieChartFragment();
        totalList.add(pieChartFragment1);
        pieChartFragment2 = new PieChartFragment();
        totalList.add(pieChartFragment2);
        pieChartFragment3 = new PieChartFragment();
        totalList.add(pieChartFragment3);

        radioGroup_main
                .setOnCheckedChangeListener((group, checkedId) -> {
                    for (int i = 0; i < radioGroup_main.getChildCount(); i++) {
                        if (arrRadioButton[i].getId() == checkedId) {
                            viewPager.setCurrentItem(i);
                            switch (i) {
                                case 0:
                                    pieChartFragment1.load(i, model.getRi());
                                    break;
                                case 1:
                                    pieChartFragment2.load(i, model.getZhou());
                                    break;
                                case 2:
                                    pieChartFragment3.load(i, model.getYue());
                                    break;
                            }
                            return;
                        }
                    }
                });
        initViewPager();
        if (model != null) {
            pieChartFragment1.load(0, model.getRi());
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.ac_viewpager);
        viewPager.setAdapter(new MyPagerAdapter(
                getSupportFragmentManager(), totalList));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                arrRadioButton[position].setChecked(true);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> list = null;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return (list != null) ? list.size() : 0;
        }
    }
}
