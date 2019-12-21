package com.mycompany.exchangerateapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mycompany.exchangerateapp.R;
import com.mycompany.exchangerateapp.adapter.CurrencyAdapter;
import com.mycompany.exchangerateapp.dagger.base.BaseApplication;
import com.mycompany.exchangerateapp.modal.Currency;
import com.mycompany.exchangerateapp.rest.WebService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ProgressBar progressBar;
    public static final String EXTRA_CURRENCY_KEY = "com.mycompany.exchangerateapp.ui.currency_key";
    CompositeDisposable disposable = new CompositeDisposable();
    List<Currency> currencyList = new ArrayList<>();
    private ListView listView;
    private CurrencyAdapter adapter;
    private Parcelable state = null;

    @Inject
    WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        listView = findViewById(R.id.list_view);
        fetchData();

        if (state != null) {
            listView.onRestoreInstanceState(state);
        }

    }

    private void fetchData() {
        disposable.add(webService.getCurrencyRate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        progressBar.setVisibility(View.VISIBLE);
                        if (responseBody != null) {
                            JSONObject json = new JSONObject(responseBody.string());
                            JSONObject rates = json.getJSONObject("rates");
                            Iterator<String> iterator = rates.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                String value = rates.getString(key);
                                Currency currency = new Currency(key, value);
                                currencyList.add(currency);
                                populateList(currencyList);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void populateList(List<Currency> list) {
        adapter = new CurrencyAdapter(MainActivity.this, list);
        listView.setAdapter(adapter);
        listView.smoothScrollToPosition(0);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(MainActivity.this, ChartActivity.class);
        intent.putExtra(EXTRA_CURRENCY_KEY, currencyList.get(i).getName());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    protected void onPause() {
        state = listView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
