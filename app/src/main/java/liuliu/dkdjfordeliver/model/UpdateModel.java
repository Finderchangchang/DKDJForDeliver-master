package liuliu.dkdjfordeliver.model;

/**
 * 更新
 * Created by Administrator on 2016/10/13.
 */

public class UpdateModel {

    /**
     * version : 0.1.7
     * time : 2016-10-01
     * content : 版本升级
     * download : http://www.dakedaojia.com/download/deliver.apk
     */

    private String version;
    private String time;
    private String content;
    private String download;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}
