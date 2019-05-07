package com.jimi.map;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2015/8/10.
 */
public class MyLatLng implements Serializable {
    public LatLng mLatLng;
    public float latitude;
    public float longitude;
	public int trackIndex;
	public String address;

	public int getTrackIndex() {
		return trackIndex;
	}
//
	public MyLatLng setTrackIndex(int pTrackIndex) {
		trackIndex = pTrackIndex;
		return this;
	}

	public MyLatLng(double latitude, double longitude) {
        mLatLng = new LatLng(latitude,longitude);
		this.latitude = (float) mLatLng.latitude;
		this.longitude = (float) mLatLng.longitude;
    }

    	/**
	 * 百度坐标纠偏转换函数
	 *
	 * @param
	 * @param
	 * @return
	 */
	public MyLatLng gpsConversion(MyLatLng pLatLng) {

		// 将GPS设备采集的原始GPS坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordinateConverter.CoordType.GPS);
		// sourceLatLng待转换坐标
		converter.coord(pLatLng.mLatLng);
		LatLng desLatLng = converter.convert();
		MyLatLng vLatLng = new MyLatLng(desLatLng.latitude, desLatLng.longitude);
		return vLatLng;// 转成百度坐标

	}

	/**
	 * 将百度坐标转换成gps坐标
	 *
	 * @param sourceLatLng
	 * @return
	 */
	public MyLatLng convertBaiduToGPS(MyLatLng sourceLatLng) {
		// 将GPS设备采集的原始GPS坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordinateConverter.CoordType.GPS);
		// sourceLatLng待转换坐标
		converter.coord(sourceLatLng.mLatLng);
		LatLng desLatLng = converter.convert();
		double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
		double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
		BigDecimal bdLatitude = new BigDecimal(latitude);
		bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
		BigDecimal bdLongitude = new BigDecimal(longitude);
		bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
		return new MyLatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
	}

	/**
	 * 将百度坐标转换成火星坐标
	 *
	 * @param pLatLng
	 * @return
	 */
	public MyLatLng convertBaiduToHX(MyLatLng pLatLng)
	{
		double x = pLatLng.mLatLng.longitude - 0.0065, y = pLatLng.mLatLng.latitude - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
		double lon = z * Math.cos(theta);
		double lat = z * Math.sin(theta);
		MyLatLng vLatlng = new MyLatLng(lat,lon);
		return vLatlng;
	}
}
