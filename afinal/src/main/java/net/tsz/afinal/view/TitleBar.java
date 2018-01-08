package net.tsz.afinal.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.R;

/**
 * Created by Administrator on 2016/11/28.
 */

public class TitleBar extends LinearLayout {
    int str_left_iv;
    int str_center_iv;
    int str_right_iv;
    String str_right_tv;
    String str_left_tv;
    boolean no_bottom_line;//默认显示
    boolean right_have_sw;//默认隐藏
    String center_str;//中间字
    String str_center_tv;
    ImageView left_iv;
    ImageView center_iv;
    ImageView right_iv;
    TextView center_tv;
    TextView right_tv;
    TextView left_tv;
    Toolbar tool_bar;
    LeftClick leftClick;
    RightClick rightClick;
    RightSWClick rightSWClick;
    LinearLayout bottom_line_ll;
    RelativeLayout left_rl;
    RelativeLayout center_rl;
    RelativeLayout right_rl;
    Button right_btn;

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        str_left_iv = a.getResourceId(R.styleable.TitleBar_left_iv, 0);
        str_center_iv = a.getResourceId(R.styleable.TitleBar_center_iv, 0);
        str_right_iv = a.getResourceId(R.styleable.TitleBar_right_iv, 0);
        str_center_tv = a.getString(R.styleable.TitleBar_center_tv);
        no_bottom_line = a.getBoolean(R.styleable.TitleBar_no_bottom_line, false);
        str_right_tv = a.getString(R.styleable.TitleBar_right_tv);
        str_left_tv = a.getString(R.styleable.TitleBar_left_tv);
        right_have_sw = a.getBoolean(R.styleable.TitleBar_right_have_sw, false);
        a.recycle();
        init(context);
    }

    boolean result = false;//是否开工。true开工

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.v_title_bar, this);
        left_iv = (ImageView) findViewById(R.id.left_iv);
        left_tv = (TextView) findViewById(R.id.left_tv);
        center_iv = (ImageView) findViewById(R.id.center_iv);
        right_iv = (ImageView) findViewById(R.id.right_iv);
        center_tv = (TextView) findViewById(R.id.center_tv);
        tool_bar = (Toolbar) findViewById(R.id.tool_bar);
        right_tv = (TextView) findViewById(R.id.right_tv);
        bottom_line_ll = (LinearLayout) findViewById(R.id.bottom_line_ll);
        left_rl = (RelativeLayout) findViewById(R.id.left_rl);
        center_rl = (RelativeLayout) findViewById(R.id.center_rl);
        right_rl = (RelativeLayout) findViewById(R.id.right_rl);
        right_btn = (Button) findViewById(R.id.right_btn);
        right_btn.setVisibility(right_have_sw ? VISIBLE : GONE);

        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightSWClick != null) {
                    rightSWClick.onClick();
                }
            }
        });
        if (!("").equals(str_center_tv)) {//中间文字显示隐藏
            center_tv.setVisibility(VISIBLE);
            center_tv.setText(str_center_tv);
            center_iv.setVisibility(GONE);
        } else {
            center_tv.setVisibility(GONE);
            center_iv.setVisibility(VISIBLE);
        }
        if (!("").equals(str_left_tv)) {
            left_tv.setVisibility(VISIBLE);
            left_tv.setText(str_left_tv);
            left_iv.setVisibility(GONE);
        } else {
            left_tv.setVisibility(GONE);
            left_iv.setVisibility(VISIBLE);
        }
        if (str_left_iv != 0) {
            left_iv.setImageResource(str_left_iv);
            left_tv.setVisibility(GONE);
            left_iv.setVisibility(VISIBLE);
        }
        left_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftClick != null) {
                    leftClick.onClick();
                }
            }
        });
        right_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightClick != null) {
                    rightClick.onClick();
                }
            }
        });
        if (!("").equals(str_right_tv)) {
            right_tv.setVisibility(VISIBLE);
            right_iv.setVisibility(GONE);
            right_tv.setText(str_right_tv);
        } else {
            right_tv.setVisibility(GONE);
            right_iv.setVisibility(VISIBLE);
        }
        if (str_right_iv != 0) {
            right_tv.setVisibility(GONE);
            right_iv.setVisibility(VISIBLE);
            right_iv.setImageResource(str_right_iv);
        } else {
            right_tv.setVisibility(VISIBLE);
            right_iv.setVisibility(GONE);
        }
        if (no_bottom_line) {
            bottom_line_ll.setVisibility(GONE);
        } else {
            bottom_line_ll.setVisibility(VISIBLE);
        }
        tool_bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftClick != null) {
                    leftClick.onClick();
                }
            }
        });

    }

    public boolean getRightClick() {
        return result;
    }

    public TitleBar(Context context) {
        this(context, null);
        init(context);
    }

    public interface LeftClick {
        void onClick();
    }

    public interface RightClick {
        void onClick();
    }

    public interface RightSWClick {
        void onClick();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    //设置右侧是否可见
    public void setRightBtn(boolean result) {
        right_btn.setVisibility(result ? VISIBLE : GONE);
        right_iv.setVisibility(result?VISIBLE:GONE);
        right_tv.setVisibility(result?VISIBLE:GONE);
        right_rl.setVisibility(result?VISIBLE:GONE);
    }

    public void setLeftClick(LeftClick leftClick) {
        this.leftClick = leftClick;
    }


    public void setRightClick(RightClick rightClick) {
        this.rightClick = rightClick;
    }

    public void setRightSWClick(RightSWClick rightSWClick) {
        this.rightSWClick = rightSWClick;
    }
    public void setRightButtonClick(boolean result){
        right_btn.setBackgroundResource(result ? R.drawable.kg_sw : R.drawable.sg_sw);
    }
    public String getCenter_str() {
        return center_str;
    }

    public void setCenter_str(String center_str) {
        center_tv.setText(center_str);
    }
}
