package com.mycompany.exchangerateapp.dagger.component;

import com.mycompany.exchangerateapp.dagger.module.RetrofitModule;
import com.mycompany.exchangerateapp.modal.ApplicationModule;
import com.mycompany.exchangerateapp.ui.ChartActivity;
import com.mycompany.exchangerateapp.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, RetrofitModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(ChartActivity chartActivity);
}
