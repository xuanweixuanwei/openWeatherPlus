package com.dejavu.utopia.presenters;


import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.warning.WarningBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;

/**
 * Created by niuchong on 2018/5/17.
 */

public interface WeatherInterface {
    /**
     * 实况天气
     */
    void getWeatherNow(WeatherNowBean bean);

    /**
     * 3-7天天气预报
     */
    void getWeatherForecast(WeatherDailyBean bean);

    /**
     * 灾害天气预警
     */
    void getWarning(WarningBean.WarningBeanBase bean);

    /**
     * 空气实况
     */
    void getAirNow(AirNowBean bean);

    /**
     * 空气预报
     */
//    void getAirForecast(AirForecast bean);


    /**
     * 逐小时预报
     */
    void getWeatherHourly(WeatherHourlyBean bean);

}
