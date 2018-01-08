package liuliu.dkdjfordeliver.model;

/**
 * 正常消息
 * Created by Administrator on 2016/10/13.
 */

public class NormalMessageModel {

    /**
     * msg : 登录成功
     * userid : 31
     * state : 1
     * name : 丁玉强
     * newordercount : 0
     * ExpressOrdercount : 0
     * gid : 9
     * cityid : 8
     */

    private String msg;
    private String userid;
    private String state;
    private String name;
    private String newordercount;
    private String ExpressOrdercount;
    private String gid;
    private String cityid;
    private String IsApproved;

    public String getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(String isApproved) {
        IsApproved = isApproved;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewordercount() {
        return newordercount;
    }

    public void setNewordercount(String newordercount) {
        this.newordercount = newordercount;
    }

    public String getExpressOrdercount() {
        return ExpressOrdercount;
    }

    public void setExpressOrdercount(String ExpressOrdercount) {
        this.ExpressOrdercount = ExpressOrdercount;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }
}
