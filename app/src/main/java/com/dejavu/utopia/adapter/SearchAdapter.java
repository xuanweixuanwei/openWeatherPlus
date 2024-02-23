package com.dejavu.utopia.adapter;

import android.annotation.SuppressLint;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dejavu.utopia.R;
import com.dejavu.utopia.bean.CityBean;
import com.dejavu.utopia.bean.CityBeanList;
import com.dejavu.utopia.dataInterface.DataUtil;
import com.dejavu.utopia.utils.ContentUtil;
import com.dejavu.utopia.utils.SpUtils;
import com.dejavu.utopia.view.activity.SearchActivity;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;



/**
 * 最近搜索
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CityBean> data;
    private final SearchActivity activity;
    private final String searchText;
    private Lang lang;
    private CityBeanList cityBeanList = new CityBeanList();
    private final boolean isSearching;

    public SearchAdapter(SearchActivity activity, List<CityBean> data, String searchText, boolean isSearching) {
        this.activity = activity;
        this.data = data;
        this.searchText = searchText;
        this.isSearching = isSearching;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys") && ContentUtil.SYS_LANG.equals("en")) {
            lang = Lang.EN;
        } else {
            lang = Lang.ZH_HANS;
        }
        View view;
        if (isSearching) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_searching, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_history, viewGroup, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        MyViewHolder viewHolder = (MyViewHolder) myViewHolder;
        View itemView = viewHolder.itemView;
        String name = data.get(i).getCityName();
        int x = name.indexOf("-");
        String parentCity = name.substring(0, x);
        String location = name.substring(x + 1);

        String cityName = location + "，" + parentCity + "，" + data.get(i).getAdminArea() + "，" + data.get(i).getCnty();
        if (TextUtils.isEmpty(data.get(i).getAdminArea())) {
            cityName = location + "，" + parentCity + "，" + data.get(i).getCnty();
        }
        if (!TextUtils.isEmpty(cityName)) {
            viewHolder.tvCity.setText(cityName);
            if (cityName.contains(searchText)) {
                int index = cityName.indexOf(searchText);
                //创建一个 SpannableString对象
                SpannableString sp = new SpannableString(cityName);
                //设置高亮样式一
                sp.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.light_text_color)), index, index + searchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.tvCity.setText(sp);
            }
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cid = data.get(i).getCityId();
                if (lang.equals(Lang.ZH_HANS)) {
                    saveData(Lang.EN, "cityBeanEn", cid);
                    saveBean("cityBean", cid, i);
                } else {
                    saveData(Lang.ZH_HANS, "cityBean", cid);
                    saveBean("cityBeanEn", cid, i);
                }
                activity.onBackPressed();
            }
        });
    }


    private void saveBean(final String key, String cid, int x) {
        List<CityBean> citys = new ArrayList<>();
        cityBeanList = SpUtils.getBean(activity, key, CityBeanList.class);
        if (cityBeanList != null && cityBeanList.getCityBeans() != null) {
            citys = cityBeanList.getCityBeans();
        }
        for (int i = 0; i < citys.size(); i++) {
            if (citys.get(i).getCityId().equals(cid)) {
                citys.remove(i);
            }
        }
        if (citys.size() == 10) {
            citys.remove(9);
        }
        citys.add(0, data.get(x));
        CityBeanList cityBeans = new CityBeanList();
        cityBeans.setCityBeans(citys);
        SpUtils.saveBean(activity, key, cityBeans);

    }

    private void saveData(Lang lang, final String key, final String cid) {
        QWeather.getGeoCityLookup(activity, cid, Range.CN, 1, lang, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i("sky", "onError: ");
                activity.onBackPressed();
            }

            @Override
            public void onSuccess(GeoBean search) {
                List<CityBean> citys = new ArrayList<>();
                if (search.getCode().equals(Code.OK.getCode())) {
                    List<GeoBean.LocationBean> basic = search.getLocationBean();
                    GeoBean.LocationBean basicData = basic.get(0);
                    String parentCity = basicData.getAdm2();
                    String adminArea = basicData.getAdm1();
                    String cnty = basicData.getCountry();
                    if (TextUtils.isEmpty(parentCity)) {
                        parentCity = adminArea;
                    }
                    if (TextUtils.isEmpty(adminArea)) {
                        parentCity = cnty;
                    }
                    CityBean cityBean = new CityBean();
                    cityBean.setCityName(parentCity + " - " + basicData.getName());
                    cityBean.setCityId(basicData.getId());
                    cityBean.setCnty(cnty);
                    cityBean.setAdminArea(adminArea);

                    cityBeanList = SpUtils.getBean(activity, key, CityBeanList.class);
                    if (cityBeanList != null && cityBeanList.getCityBeans() != null) {
                        citys = cityBeanList.getCityBeans();
                    }
                    for (int i = 0; i < citys.size(); i++) {
                        if (citys.get(i).getCityId().equals(cid)) {
                            citys.remove(i);
                        }
                    }
                    if (citys.size() == 10) {
                        citys.remove(9);
                    }
                    citys.add(0, cityBean);
                    CityBeanList cityBeans = new CityBeanList();
                    cityBeans.setCityBeans(citys);
                    SpUtils.saveBean(activity, key, cityBeans);
                    DataUtil.setCid(cid);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCity;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tv_item_history_city);

        }
    }
}
