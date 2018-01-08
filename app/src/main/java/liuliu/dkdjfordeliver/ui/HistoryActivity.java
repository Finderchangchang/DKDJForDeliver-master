package liuliu.dkdjfordeliver.ui;

import android.app.DatePickerDialog;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.view.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;
import liuliu.dkdjfordeliver.listener.OrderDetailListener;
import liuliu.dkdjfordeliver.method.CommonAdapter;
import liuliu.dkdjfordeliver.method.CommonViewHolder;
import liuliu.dkdjfordeliver.method.Utils;
import liuliu.dkdjfordeliver.model.ListModel;
import liuliu.dkdjfordeliver.model.OrderModel;
import liuliu.dkdjfordeliver.view.IHistory;

/**
 * Created by Administrator on 2016/12/19.
 */

public class HistoryActivity extends BaseActivity implements IHistory {
    OrderDetailListener listener;
    String now;
    CommonAdapter<OrderModel> commonAdapter;
    List<OrderModel> mList;
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.main_lv)
    ListView mainLv;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog mBottomSheetDialog;

    @Override
    public void initViews() {
        setContentView(R.layout.ac_history);
        ButterKnife.bind(this);
        load();
        mList = new ArrayList<>();
        titleBar.setLeftClick(() -> finish());
        titleBar.setRightClick(() -> mBottomSheetDialog.show());
        now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        listener = new OrderDetailListener(this);
        listener.getHistory(1, "2016-11-12", now);
        commonAdapter = new CommonAdapter<OrderModel>(this, mList, R.layout.item_history) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                holder.setText(R.id.id_card_tv, model.getOrderid());
                holder.setText(R.id.fei_tv, model.getSendFee());
                holder.setText(R.id.time_tv, model.getAddtime());
                holder.setText(R.id.state_iv, model.getOrderRcver());
                switch (model.getOrderRcver()) {
                    case "帮我送":
                        holder.setBG(R.id.state_iv, R.mipmap.order_song);
                        break;
                    case "帮我取":
                        holder.setBG(R.id.state_iv, R.mipmap.order_qu);
                        break;
                    case "帮我买":
                        holder.setBG(R.id.state_iv, R.mipmap.order_mai);
                        break;
                    default:
                        holder.setBG(R.id.state_iv, R.mipmap.order_kp);
                        break;
                }
            }
        };
        mainLv.setAdapter(commonAdapter);
        mainLv.setOnItemClickListener((parent, view, position, id) -> {
            Utils.IntentPost(OrderDetailActivitys.class, intent -> {
                intent.putExtra("orderid", mList.get(position).getOrderid());
                intent.putExtra("history", true);
            });
        });
    }

    TextView start_tv;
    TextView end_tv;
    DatePickerDialog datePickerDialog;
    String start_time = "";
    String end_time = "";

    private void load() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(R.layout.dialog_bottom);
        ImageView btn = (ImageView) mBottomSheetDialog.findViewById(R.id.dia_btn);
        Button search_btn = (Button) mBottomSheetDialog.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(v -> {
            if (!("").equals(start_time) || !("").equals(end_time)) {
                mBottomSheetDialog.dismiss();
                if (!("").equals(start_time) && !("").equals(end_time)) {
                    listener.getHistory(1, start_time, end_time);
                } else if (!("").equals(start_time)) {
                    listener.getHistory(1, start_time, now);
                } else {
                    listener.getHistory(1, now, end_time);
                }
            }
        });
        btn.setOnClickListener(v -> mBottomSheetDialog.dismiss());
        start_tv = (TextView) mBottomSheetDialog.findViewById(R.id.start_tv);
        start_tv.setOnClickListener(v -> {
            datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                start_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                start_tv.setText(start_time);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        end_tv = (TextView) mBottomSheetDialog.findViewById(R.id.end_tv);
        end_tv.setOnClickListener(v -> {
            datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                end_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                end_tv.setText(end_time);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        mBottomSheetDialog.setOnDismissListener(dialog -> load());
    }

    @Override
    public void initEvents() {

    }

    @Override
    public void historyList(ListModel model) {
        if (model != null) {
            mList = model.getOrderlist();
            commonAdapter.refresh(mList);
        }
    }
}
