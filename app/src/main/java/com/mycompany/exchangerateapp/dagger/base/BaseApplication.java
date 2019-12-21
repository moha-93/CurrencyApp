package com.mycompany.exchangerateapp.dagger.base;

import android.app.Application;

import com.mycompany.exchangerateapp.dagger.component.AppComponent;
import com.mycompany.exchangerateapp.dagger.component.DaggerAppComponent;
import com.mycompany.exchangerateapp.dagger.module.RetrofitModule;
import com.mycompany.exchangerateapp.modal.ApplicationModule;

public class BaseApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .retrofitModule(new RetrofitModule())
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }
}
