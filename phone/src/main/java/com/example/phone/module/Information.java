package com.example.phone.module;

import java.util.List;

/**
 * @author suhu
 * @data 2017/9/27.
 * @description
 */

public class Information {


    /**
     * flag : 0
     * data : [{"id":"15","uuid":"caa6f514cbad3149","uptime":"2017-09-20 11:15:24","ip":"219.238.166.98","longitude_latitude":"39788855,116575665"},{"id":"1","uuid":"11111","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"},{"id":"5","uuid":"11112","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"},{"id":"4","uuid":"11113","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"},{"id":"3","uuid":"1113","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"},{"id":"2","uuid":"1112","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"},{"id":"6","uuid":"1114","uptime":"2017-09-19","ip":"192.168.18.202","longitude_latitude":"39788854,116575663"}]
     */

    private int flag;
    private List<DataBean> data;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 15
         * uuid : caa6f514cbad3149
         * uptime : 2017-09-20 11:15:24
         * ip : 219.238.166.98
         * longitude_latitude : 39788855,116575665
         */

        private int id;
        private String uuid;
        private String uptime;
        private String ip;
        private String longitude_latitude;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getLongitude_latitude() {
            return longitude_latitude;
        }

        public void setLongitude_latitude(String longitude_latitude) {
            this.longitude_latitude = longitude_latitude;
        }
    }
}
