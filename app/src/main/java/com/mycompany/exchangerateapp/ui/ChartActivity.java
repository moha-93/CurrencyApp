package com.mycompany.exchangerateapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mycompany.exchangerateapp.R;
import com.mycompany.exchangerateapp.dagger.base.BaseApplication;
import com.mycompany.exchangerateapp.modal.DateValueFormatter;
import com.mycompany.exchangerateapp.rest.WebService;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.mycompany.exchangerateapp.ui.MainActivity.EXTRA_CURRENCY_KEY;

public class ChartActivity extends AppCompatActivity {
    CompositeDisposable disposable = new CompositeDisposable();
    List<BarEntry> entryList;
    private BarChart barChart;
    @Inject
    WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_chart);
        entryList = new ArrayList<>();
        AndroidThreeTen.init(this);
        barChart = findViewById(R.id.bar_chart);
        String key = getIntent().getStringExtra(EXTRA_CURRENCY_KEY);
        fetchChartData(key);
    }

    private void fetchChartData(final String key) {
        disposable.add(webService.getCurrencyGraph(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        if (responseBody != null) {
                            JSONObject json = new JSONObject(responseBody.string());
                            JSONObject rates = json.getJSONObject("rates");
                            Iterator<String> iterator = rates.keys();
                            while (iterator.hasNext()) {
                                String keyDate = iterator.next();
                                String keyValue = rates.getJSONObject(keyDate).getString(key);
                                String value = String.format("%.4s", keyValue);
                                LocalDate localDate = LocalDate.parse(keyDate);
                                long epochDayLong = localDate.toEpochDay();
                                entryList.add(new BarEntry(epochDayLong, Float.parseFloat(value)));
                                populateChart(entryList);
                            }

                        } else {
                            Toast.makeText(ChartActivity.this, getResources().getString(R.string.popup_msg), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(ChartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void populateChart(List<BarEntry> barEntryList) {
        BarDataSet dataSet = new BarDataSet(barEntryList, "Rate");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.animateY(5000);
        barChart.setFitBars(true);
        barChart.getDescription().setText("Growth rate per week");
        barChart.invalidate();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7, false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new DateValueFormatter());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        barChart = null;

    }
}
