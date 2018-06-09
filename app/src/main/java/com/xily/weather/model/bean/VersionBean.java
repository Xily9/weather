package com.xily.weather.model.bean;

public class VersionBean {

    /**
     * status : 0
     * data : {"version":5,"version_name":"Ver.2.0 beta2","download_url":"https://xilym.tk/apk/fzujwc.apk","version_force_update_under":0,"text":"测试更新","time":"2018-04-20"}
     */

    private int status;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * version : 5
         * version_name : Ver.2.0 beta2
         * download_url : https://xilym.tk/apk/fzujwc.apk
         * version_force_update_under : 0
         * text : 测试更新
         * time : 2018-04-20
         */

        private int version;
        private String version_name;
        private String download_url;
        private int version_force_update_under;
        private String text;
        private String time;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public int getVersion_force_update_under() {
            return version_force_update_under;
        }

        public void setVersion_force_update_under(int version_force_update_under) {
            this.version_force_update_under = version_force_update_under;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
