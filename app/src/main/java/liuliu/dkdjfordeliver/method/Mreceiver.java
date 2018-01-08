package liuliu.dkdjfordeliver.method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by XY on 2017/9/5.
 */

public class Mreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//开屏
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d("sunlei", "开屏");
        }//锁屏
        else if (intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d("sunlei", "锁屏");
//如果锁屏关闭当前常规定位方法，调用alarm,每2秒发动一次单次定位
//locationClient.stopLocation();
            wake();
        }//解锁
        else if (intent.ACTION_USER_PRESENT.equals(action)) {
            Log.d("sunlei", "解锁");
//am.cancel(pi);
        }
    }
    private void wake() {
}

}
