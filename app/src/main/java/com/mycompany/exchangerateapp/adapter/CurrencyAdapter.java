package com.mycompany.exchangerateapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycompany.exchangerateapp.R;
import com.mycompany.exchangerateapp.modal.Currency;

import java.util.List;

public class CurrencyAdapter extends BaseAdapter {
    private Context context;
    private List<Currency> currencyList;
    private int lastPosition;

    public CurrencyAdapter(Context context, List<Currency> currencyList) {
        this.context = context;
        this.currencyList = currencyList;
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_view_layout, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        convertView.startAnimation(animation);
        lastPosition = position;
        Currency currencyRate = currencyList.get(position);
        String name = currencyRate.getName();
        double value = Double.parseDouble(currencyRate.getValue());
        viewHolder.txtCurrencyName.setText(name);
        viewHolder.txtCurrencyValue.setText(String.format("%.2f", value));
        viewHolder.txtTimeStamp.setText(getTimeStamp());

        return convertView;
    }
    private static String getTimeStamp() {
        Time now = new Time();
        now.setToNow();
        return now.format("%Y-%m-%d  %H:%M:%S");
    }

    private class ViewHolder {
        private TextView txtCurrencyName, txtCurrencyValue,txtTimeStamp;

        ViewHolder(View view) {
            txtCurrencyName = view.findViewById(R.id.txt_currency_name);
            txtCurrencyValue = view.findViewById(R.id.txt_currency_value);
            txtTimeStamp=view.findViewById(R.id.txt_time_stamp);
        }
    }
}

