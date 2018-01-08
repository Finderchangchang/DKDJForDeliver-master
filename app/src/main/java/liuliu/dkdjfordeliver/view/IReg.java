package liuliu.dkdjfordeliver.view;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IReg {
    //请求验证码返回结果
    void getCodeResult(boolean result, String message);

    void getRegUserResult(boolean result, String message);
}
