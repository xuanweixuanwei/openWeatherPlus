package com.dejavu.utopia.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.MapsInitializer;
import com.dejavu.utopia.R;
import com.dejavu.utopia.adapter.ViewPagerAdapter;
import com.dejavu.utopia.bean.CityBean;
import com.dejavu.utopia.bean.CityBeanList;
import com.dejavu.utopia.dataInterface.DataInterface;
import com.dejavu.utopia.dataInterface.DataUtil;
import com.dejavu.utopia.service.LocationService;
import com.dejavu.utopia.utils.ContentUtil;
import com.dejavu.utopia.utils.DisplayUtil;
import com.dejavu.utopia.utils.IconUtils;
import com.dejavu.utopia.utils.SpUtils;
import com.dejavu.utopia.utils.ToTwoDecimalPlaces;
import com.dejavu.utopia.view.fragment.WeatherFragment;
import com.dejavu.utopia.view.window.LocListWindow;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends BaseActivity implements View.OnClickListener, DataInterface {
    private List<Fragment> fragments;
    private List<String> locaitons;
    private List<String> locaitonsEn;
    private List<String> cityIds;
    private ViewPager viewPager;
    private LinearLayout llRound;
    private int mNum = 0;
    private TextView tvLocation;
    private ImageView ivLoc;
    CityBeanList cityBeanList = new CityBeanList();
    private ImageView ivBack;
    private String condCode;
    public AMapLocationClientOption mLocationOption = null;
    private RelativeLayout rvTitle;
    private ImageView ivSet;
    private ImageView ivAdd;

    /*首先调用父类onCreate()方法，然后通过setContentView(R.layout.activity_main);设置当前Activity的布局文件为activity_main。
获取窗口对象Window window = getWindow();，并使用window.addFlags(WindowManager.LayoutParams
.FLAG_TRANSLUCENT_STATUS);设置状态栏为透明。
找到布局文件中的各个View组件（如ViewPager、TextView、ImageView等），并将其实例化，存储为成员变量以便后续操作。
调用initFragments(true);方法，可能是用于初始化Fragment页面或者进行页面加载的相关操作。
为两个ImageView(ivSet和ivAdd)设置点击事件监听器，当用户点击这两个按钮时，会触发onClick()方法。
使用setMargins()方法设*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //透明状态栏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        privacyCompliance();
        initLayout();

        addListener();

    }

    private void initLayout() {
        viewPager = findViewById(R.id.view_pager);
        llRound = findViewById(R.id.ll_round);
        tvLocation = findViewById(R.id.tv_location);
        ivLoc = findViewById(R.id.iv_loc);
        ivBack = findViewById(R.id.iv_main_back);
        rvTitle = findViewById(R.id.rv_title);
        ivSet = findViewById(R.id.iv_set);
        ivAdd = findViewById(R.id.iv_add_city);
        //置了ViewPager和RelativeLayout顶部的外边距，
        // 其中getStatusBarHeight()获取状态栏的高度，
        //DisplayUtil.dip2px()则是将 dips (device independent pixels) 转换为实际像素值，
        // 这样做的目的是为了在界面布局上适配状态栏高度，使得布局内容能自适应屏幕且不被状态栏遮挡。
        setMargins(viewPager, 0, getStatusBarHeight(this) + DisplayUtil.dip2px(this, 52), 0, 0);
        setMargins(rvTitle, 0, getStatusBarHeight(this), 0, 0);
    }

    private void addListener() {
        ivSet.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    private void privacyCompliance() {
        MapsInitializer.updatePrivacyShow(MainActivity.this, true, true);
        SpannableStringBuilder spannable = new SpannableStringBuilder("\"亲，感谢您对XXX" +
                "一直以来的信任！我们依据最新的监管要求更新了XXX《隐私权政策》，特向您说明如下\n1.为向您提供交易相关基本功能，我们会收集、使用必要的信息；\n2" +
                ".基于您的明示授权，我们可能会获取您的位置（为您提供附近的商品、店铺及优惠资讯等）等信息，您有权拒绝或取消授权；\n3" +
                ".我们会采取业界先进的安全措施保护您的信息安全；\n4.未经您同意，我们不会从第三方处获取、共享或向提供您的信息；\n");
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 35, 42,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        new AlertDialog.Builder(this)
                .setTitle("温馨提示(隐私合规示例)")
                .setMessage(spannable)
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsInitializer.updatePrivacyAgree(MainActivity.this, true);
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                initFragments(true);
                                startService(new Intent(getApplicationContext(),
                                        LocationService.class));
                            }
                        };
                        timer.schedule(task, 3000);


                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsInitializer.updatePrivacyAgree(MainActivity.this, false);
                    }
                })
                .show();
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    ContentUtil.NOW_LON = aMapLocation.getLongitude();//获取经度
                    ContentUtil.NOW_LAT = aMapLocation.getLatitude();//获取纬度
                    aMapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);//定位时间
                    getNowCity(true);
                    mLocationClient.onDestroy();
                } else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 没有权限
                        View view =
                                LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_loc_list, null);
                        LocListWindow locListWindow = new LocListWindow(view, MATCH_PARENT,
                                MATCH_PARENT, MainActivity.this);
                        locListWindow.show();
                        locListWindow.showAtLocation(tvLocation, Gravity.CENTER, 0, 0);
                        if (ContentUtil.FIRST_OPEN) {
                            ContentUtil.FIRST_OPEN = false;
                            SpUtils.putBoolean(MainActivity.this, "first_open", false);
                        }
                    }
                    getNowCity(true);
                    mLocationClient.onDestroy();
                }
            }
        }

    };

    /**
     * 兼容全面屏的状态栏高度
     */
    public void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    /**
     * 获取状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void initFragments(boolean first) {
        /*从本地存储（SharedPreferences）中读取名为"cityBean"的Bean对象列表，这里读取了两次，分别赋值给 cityBeanList 和
        cityBeanEn，推测可能对应两种语言环境下的城市列表数据。
初始化两个ArrayList locaitonsEn 和 locaitons，用来存放不同语言环境下城市的名称。
遍历从SharedPreferences中取出的城市Bean列表，提取出城市名称并添加到对应的ArrayList中。
初始化另外两个ArrayList cityIds 和 fragments，它们可能分别用于存放城市ID和与城市相关的Fragment。
根据传入的布尔值 first 判断是否首次初始化：
如果是首次初始化（first为true），则调用 initLocation() 方法，这可能是去请求或初始化地理位置信息。
如果不是首次初始化（first为false），则调用 getNowCity(false) 方法，这可能是获取或设置当前城市信息，同时参数 false 可能表示不需要更新界面或其他附加操作。*/
        cityBeanList = SpUtils.getBean(MainActivity.this, "cityBean", CityBeanList.class);
//        对应两种语言环境下的城市列表数据
        CityBeanList cityBeanListEn = SpUtils.getBean(MainActivity.this, "cityBeanEn",
                CityBeanList.class);
        CityBeanList cityBeanListChn = SpUtils.getBean(MainActivity.this, "cityBean",
                CityBeanList.class);

//     初始化两个ArrayList locaitonsEn 和 locaitons，用来存放不同语言环境下城市的名称。
        locaitonsEn = new ArrayList<>();
        locaitons = new ArrayList<>();

        if (cityBeanListEn != null) {
            for (CityBean city : cityBeanListEn.getCityBeans()) {
                String cityName = city.getCityName();
                locaitonsEn.add(cityName);
            }
        }
        if (cityBeanListChn != null) {
            for (CityBean city : cityBeanListChn.getCityBeans()) {
                String cityName = city.getCityName();
                locaitons.add(cityName);
            }
        }
//      初始化另外两个ArrayList cityIds 和 fragments，它们可能分别用于存放城市ID和与城市相关的Fragment
        cityIds = new ArrayList<>();
        fragments = new ArrayList<>();
//根据传入的布尔值 first 判断是否首次初始化：
//如果是首次初始化（first为true），则调用 initLocation() 方法，去请求或初始化地理位置信息。
//如果不是首次初始化（first为false），则调用 getNowCity(false) 方法，获取或设置当前城市信息，同时参数 false 可能表示不需要更新界面或其他附加操作。
        if (first) {
            initLocation();
        } else {
            getNowCity(false);
        }

    }

    private void initLocation() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置定位回调监听

        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(10000);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        mLocationClient.setLocationListener(mLocationListener);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /*该方法 `getNowCity()` 主要用于获取当前所在城市的详细信息，并进行相应的处理。过程如下：
1. 。
2. 使用 `HeWeather.getGeoCityLookup()` 方法，通过经纬度 (`ContentUtil.NOW_LON` 和 `ContentUtil.NOW_LAT`)
进行模糊地理编码查询，获取当前所在位置的城市信息。
3. 若查询成功，`onSuccess()` 回调方法会被调用，从中获取到城市的基本信息，包括城市ID（cid）和城市名称（location）。如果这是首次查询（`first`为true
），还会将这些信息保存到全局变量中（`ContentUtil.NOW_CITY_ID` 和 `ContentUtil.NOW_CITY_NAME`）。
4. 创建一个 `CityBean` 对象，并填充查询到的城市ID和名称，然后将这个对象添加到城市列表（`cityBeans`）的开头。
5. 同时将新获取的城市名称添加到之前创建的 `locaitons` 和 `locaitonsEn` 列表的开头。
6. 更新界面上显示的当前位置文本（`tvLocation.setText(location)`）。
7. 最后调用 `getData(cityBeans, first)` 方法，传递处理后的城市列表以及是否首次查询的标识，继续进行后续的数据处理和界面展示。
8. 若查询失败，则调用 `onError()` 回调方法，在此情况下，假设默认城市为“北京”，创建一个包含北京信息的 `CityBean` 对象，并同样执行上述的数据处理和界面更新操作。*/
    private void getNowCity(final boolean first) {
//        首先根据应用的当前语言设置判断要使用的查询语言类型（中文或英文）
        com.qweather.sdk.bean.base.Lang lang;
        if (ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys"
        ) && ContentUtil.SYS_LANG.equals("en")) {
            lang = com.qweather.sdk.bean.base.Lang.EN;
        } else {
            lang = com.qweather.sdk.bean.base.Lang.ZH_HANS;
        }

        QWeather.getGeoCityLookup(this,
                ToTwoDecimalPlaces.StringType(ContentUtil.NOW_LON) + "," + ToTwoDecimalPlaces.StringType(ContentUtil.NOW_LAT), com.qweather.sdk.bean.base.Range.CN, 10, lang, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                List<CityBean> cityBeans = new ArrayList<>();
                CityBean cityBean = new CityBean();
                cityBean.setCityName("北京");
                cityBean.setCityId("CN101010100");
                cityBeans.add(cityBean);
                getData(cityBeans, first);
            }

            @Override
            public void onSuccess(com.qweather.sdk.bean.geo.GeoBean search) {
                if (search.getLocationBean().size() != 0) {
                    com.qweather.sdk.bean.geo.GeoBean.LocationBean basic =
                            search.getLocationBean().get(0);
                    String cid = basic.getId();
                    final String location = basic.getName();
                    if (first) {
                        ContentUtil.NOW_CITY_ID = cid;
                        ContentUtil.NOW_CITY_NAME = location;
                    }

                    List<CityBean> cityBeans = new ArrayList<>();
                    CityBean cityBean = new CityBean();
                    cityBean.setCityName(location);
                    cityBean.setCityId(cid);

                    locaitons.add(0, location);
                    locaitonsEn.add(0, location);
                    if (cityBeanList != null && cityBeanList.getCityBeans() != null && cityBeanList.getCityBeans().size() > 0) {
                        cityBeans = cityBeanList.getCityBeans();
                        cityBeans.add(0, cityBean);
                    } else {
                        cityBeans.add(cityBean);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvLocation.setText(location);
                        }
                    });

                    getData(cityBeans, first);
                } else {
                    this.onError(new NullPointerException());
                }
            }
        });
    }

    private void getNow(String location, final boolean nowCity) {
        QWeather.getGeoCityLookup(this, location, Range.CN, 3, Lang.ZH_HANS,
                new QWeather.OnResultGeoListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(GeoBean search) {
                        if (search.getLocationBean().size() != 0) {
                            GeoBean.LocationBean basic = search.getLocationBean().get(0);
                            String cid = basic.getId();
                            String location = basic.getName();
                            if (nowCity) {
                                ContentUtil.NOW_CITY_ID = cid;
                                ContentUtil.NOW_CITY_NAME = location;
                                if (cityIds != null && cityIds.size() > 0) {
                                    cityIds.add(0, cid);
                                    cityIds.remove(1);
                                }
                            }
                            QWeather.getWeatherNow(MainActivity.this, cid,
                                    new QWeather.OnResultWeatherNowListener() {
                                        @Override
                                        public void onError(Throwable throwable) {

                                        }

                                        @Override
                                        public void onSuccess(WeatherNowBean weatherNowBean) {
                                            if (Code.OK==(weatherNowBean.getCode())) {
                                                WeatherNowBean.NowBaseBean now =
                                                        weatherNowBean.getNow();
                                                condCode = now.getIcon();
                                                DateTime nowTime = DateTime.now();
                                                int hourOfDay = nowTime.getHourOfDay();
                                                if (hourOfDay > 6 && hourOfDay < 19) {
                                                    ivBack.setImageResource(IconUtils.getDayBack(condCode));
                                                } else {
                                                    ivBack.setImageResource(IconUtils.getNightBack(condCode));
                                                }
                                            }
                                        }

                                    });
                        }

                    }
                });
    }

    /**
     * 获取数据
     */
    private void getData(final List<CityBean> cityBeans, final boolean first) {
        fragments = new ArrayList<>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llRound.removeAllViews();
                for (CityBean city : cityBeans) {
                    String cityId = city.getCityId();
                    cityIds.add(cityId);
                    WeatherFragment weatherFragment = WeatherFragment.newInstance(cityId);//todo
                    fragments.add(weatherFragment);
                }
                if (cityIds.get(0).equalsIgnoreCase(ContentUtil.NOW_CITY_ID)) {
                    ivLoc.setVisibility(View.VISIBLE);
                } else {
                    ivLoc.setVisibility(View.INVISIBLE);
                }
                View view;
                for (int i = 0; i < fragments.size(); i++) {
                    //创建底部指示器(小圆点)
                    view = new View(MainActivity.this);
                    view.setBackgroundResource(R.drawable.background);
                    view.setEnabled(false);
                    //设置宽高
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(DisplayUtil.dip2px(getApplicationContext(), 4),
                                    DisplayUtil.dip2px(getApplicationContext(), 4));
                    //设置间隔
                    if (fragments.get(i) != fragments.get(0)) {
                        layoutParams.leftMargin = 10;
                    }
                    //添加到LinearLayout
                    llRound.addView(view, layoutParams);
                }
                viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
                //第一次显示小白点
                llRound.getChildAt(0).setEnabled(true);
                mNum = 0;
                if (fragments.size() == 1) {
                    llRound.setVisibility(View.GONE);
                } else {
                    llRound.setVisibility(View.VISIBLE);
                }
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        if (cityIds.get(i).equalsIgnoreCase(ContentUtil.NOW_CITY_ID)) {
                            ivLoc.setVisibility(View.VISIBLE);
                        } else {
                            ivLoc.setVisibility(View.INVISIBLE);
                        }
                        llRound.getChildAt(mNum).setEnabled(false);
                        llRound.getChildAt(i).setEnabled(true);
                        mNum = i;
                        tvLocation.setText(locaitons.get(i));
                        if (ContentUtil.SYS_LANG.equalsIgnoreCase("en")) {
                            tvLocation.setText(locaitonsEn.get(i));
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                if (!first && fragments.size() > 1) {
                    viewPager.setCurrentItem(1);
                    getNow(cityIds.get(1), false);
                } else {
                    viewPager.setCurrentItem(0);
                    getNow(ContentUtil.NOW_LON + "," + ContentUtil.NOW_LAT, true);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_set:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.iv_add_city:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataUtil.setDataInterface(this);
        if (!ContentUtil.APP_PRI_TESI.equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    WeatherFragment weatherFragment = (WeatherFragment) fragment;
                    weatherFragment.changeTextSize();
                }
            }
            if ("small".equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
                tvLocation.setTextSize(15);
            } else if ("large".equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
                tvLocation.setTextSize(17);
            } else {
                tvLocation.setTextSize(16);
            }
            ContentUtil.APP_PRI_TESI = ContentUtil.APP_SETTING_TESI;
        }
        if (ContentUtil.CHANGE_LANG) {
            if (ContentUtil.SYS_LANG.equalsIgnoreCase("en")) {
                changeLang(com.qweather.sdk.bean.base.Lang.EN);
            } else {
                changeLang(com.qweather.sdk.bean.base.Lang.ZH_HANS);
            }
            ContentUtil.CHANGE_LANG = false;
        }
        if (ContentUtil.CITY_CHANGE) {
            initFragments(true);
            ContentUtil.CITY_CHANGE = false;
        }
        if (ContentUtil.UNIT_CHANGE) {
            for (Fragment fragment : fragments) {
                WeatherFragment weatherFragment = (WeatherFragment) fragment;
                weatherFragment.changeUnit();
            }
            ContentUtil.UNIT_CHANGE = false;
        }
    }

    @Override
    public void setCid(String cid) {
        initFragments(false);
    }

    @Override
    public void deleteID(int index) {
        initFragments(true);
    }

    @Override
    public void changeBack(String condCode) {
        DateTime nowTime = DateTime.now();
        int hourOfDay = nowTime.getHourOfDay();
        if (hourOfDay > 6 && hourOfDay < 19) {
            ivBack.setImageResource(IconUtils.getDayBack(condCode));
        } else {
            ivBack.setImageResource(IconUtils.getNightBack(condCode));
        }
    }

    private void changeLang(final com.qweather.sdk.bean.base.Lang lang) {
        QWeather.getGeoCityLookup(this,
                ToTwoDecimalPlaces.StringType(ContentUtil.NOW_LON) + "," + ToTwoDecimalPlaces.StringType(ContentUtil.NOW_LAT),
                com.qweather.sdk.bean.base.Range.CN, 10, lang, new QWeather.OnResultGeoListener() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(com.qweather.sdk.bean.geo.GeoBean search) {
                        if (search.getLocationBean().size() != 0) {
                            com.qweather.sdk.bean.geo.GeoBean.LocationBean basic =
                                    search.getLocationBean().get(0);
                            String location = basic.getName();

                            if (lang == com.qweather.sdk.bean.base.Lang.EN) {
                                locaitonsEn.remove(0);
                                locaitonsEn.add(0, location);
                                tvLocation.setText(locaitonsEn.get(mNum));
                            } else if (lang == com.qweather.sdk.bean.base.Lang.ZH_HANS) {
                                locaitons.remove(0);
                                locaitons.add(0, location);
                                tvLocation.setText(locaitons.get(mNum));
                            }
                        }

                    }
                });
    }
}
