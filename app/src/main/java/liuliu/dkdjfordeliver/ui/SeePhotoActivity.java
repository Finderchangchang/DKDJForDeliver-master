package liuliu.dkdjfordeliver.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import liuliu.dkdjfordeliver.R;
import liuliu.dkdjfordeliver.base.BaseActivity;

/**
 * Created by XY on 2017/8/14.
 */

public class SeePhotoActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.big_photo)
    ImageView BigPhoto;
    @Bind(R.id.back_tv1)
    TextView BackTv;
    String imgurl;
    @Override
    public void initViews() {
        setContentView(R.layout.ac_seephoto);
        ButterKnife.bind(this);
        imgurl=getIntent().getStringExtra("imgurl");

    }

    @Override
    public void initEvents() {
        BackTv.setOnClickListener(this);
        Picasso.with(this).load(imgurl).into(BigPhoto);

    }

    @Override
    public void onClick(View view) {
       if(view.getId()==R.id.back_tv1) {finish();}
    }
}
