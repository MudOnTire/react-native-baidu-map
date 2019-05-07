package com.jimi.map;

/**
 * Created by huning on 2016/2/29 0029.
 */
public class LocationResult {
    public MyLatLng latLng;
    public String keywords;
    public String address;
    public String distance;

    public MyLatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(MyLatLng pLatLng) {
        latLng = pLatLng;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String pKeywords) {
        keywords = pKeywords;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String pAddress) {
        address = pAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String pDistance) {
        distance = pDistance;
    }
}
