package com.dejavu.utopia.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dejavu.utopia.dataInterface.DataUtil;
import com.dejavu.utopia.R;
import com.dejavu.utopia.bean.CityBean;
import com.dejavu.utopia.bean.CityBeanList;
import com.dejavu.utopia.utils.SpUtils;
import com.dejavu.utopia.view.activity.MainActivity;
import com.dejavu.utopia.view.window.LocListWindow;

import java.util.ArrayList;
import java.util.List;

public class LocLIstAdapter extends Adapter<RecyclerView.ViewHolder> {

    private List<CityBean> data;
    private LocListWindow locListWindow;
    private MainActivity activity;

    public LocLIstAdapter(LocListWindow locListWindow, List<CityBean> data, MainActivity context) {
        this.data = data;
        this.locListWindow = locListWindow;
        this.activity = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_favorite_light, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,  int i) {
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        i=viewHolder.getAdapterPosition();
        myViewHolder.tvCity.setText(data.get(i).getCityName());
        final int finalI = viewHolder.getAdapterPosition();
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = data.get(finalI).getCityId();
                String cityName = data.get(finalI).getCityName();

                CityBean cityBean = new CityBean();
                cityBean.setCityName(cityName);
                cityBean.setCityId(city);
                CityBeanList cityBeans;
                List<CityBean> citys = new ArrayList<>();

                cityBeans = SpUtils.getBean(activity, "cityBean", CityBeanList.class);
                if (cityBeans != null && cityBeans.getCityBeans() != null) {
                    citys = cityBeans.getCityBeans();
                }else {
                    cityBeans = new CityBeanList();
                }
                citys.add(0, cityBean);
                cityBeans.setCityBeans(citys);
                SpUtils.saveBean(activity, "cityBean", cityBeans);
                SpUtils.saveBean(activity, "cityBeanEn", cityBeans);

                SpUtils.putString(activity, "lastLocation", city);
                DataUtil.setCid(city);
                locListWindow.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCity;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tv_item_favorite_city);

        }
    }
}
