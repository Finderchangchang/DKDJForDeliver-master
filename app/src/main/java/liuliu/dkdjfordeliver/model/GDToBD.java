package liuliu.dkdjfordeliver.model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */

public class GDToBD {

    /**
     * status : 0
     * result : [{"x":115.50862787432,"y":38.893382521035}]
     */

    private int status;
    /**
     * x : 115.50862787432
     * y : 38.893382521035
     */

    private List<ResultBean> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
