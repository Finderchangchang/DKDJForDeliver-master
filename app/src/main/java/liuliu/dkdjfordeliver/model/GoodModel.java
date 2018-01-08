package liuliu.dkdjfordeliver.model;

/**
 * 用户订的菜品
 * Created by Administrator on 2016/10/15.
 */

public class GoodModel {

    /**
     * count : 1
     * id : 22495
     * price : 2.0
     * name : tupian的撒旦蚬子(0)
     */

    private int count;
    private String id;
    private double price;
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
