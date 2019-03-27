package com.example.balaji.fusedlocationexample;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.balaji.fusedlocationexample.datamodel.Day;
import com.example.balaji.fusedlocationexample.datamodel.Forecastday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class CustomWeatherAdapter extends ArrayAdapter<Forecastday> {

    public CustomWeatherAdapter(Context context, ArrayList<Forecastday> resource) {
        super(context, 0, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_item, parent, false);
        }

        TextView tv_day = convertView.findViewById(R.id.forecast_day);
        TextView tv_temp = convertView.findViewById(R.id.forecast_temp);

        Forecastday fcastDay = getItem(position);
        String fDate = fcastDay.getDate();
        Day fDay = fcastDay.getDay();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());

        String formattedDay = "";

        try {
            formattedDay = new SimpleDateFormat("EEEE").format(sdf.parse(fDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int tempReading = (int) fDay.getAvgtempC();

        tv_day.setText(formattedDay);
        tv_temp.setText(Html.fromHtml(tempReading + "&#8451  "));

//        Log.e("forecast adapter", formattedDay +":"+ tempReading) ;

        return convertView;
    }

}
