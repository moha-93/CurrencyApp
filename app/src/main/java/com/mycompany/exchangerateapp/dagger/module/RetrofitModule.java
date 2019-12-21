package com.mycompany.exchangerateapp.dagger.module;

import android.annotation.SuppressLint;
import android.app.Application;

import com.mycompany.exchangerateapp.rest.WebService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {
    private static final int TIMEOUT = 30000;
    private static final String BASE_URL = "https://api.exchangeratesapi.io/";

    @Provides
    @Singleton
    GsonConverterFactory gsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    RxJava2CallAdapterFactory callAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    Cache provideCache(Application application) {
        File cacheDir = application.getCacheDir();
        int cacheSize = 5 * 1024 * 1024; // 5MB
        return new Cache(cacheDir, cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClient(Interceptor interceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    Interceptor provideInterceptor() {
        return new Interceptor() {
            @SuppressLint("DefaultLocale")
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request();
                System.out.println(String.format("Sending request %s on %n%s , %s", request.url(), request.headers(), request.body()));
                Response response = chain.proceed(request);
                long t1 = System.nanoTime();
                long t2 = System.nanoTime();
                System.out.println(
                        String.format("Received response for %s in %.1fms%n%s", response.request().url(),
                                (t2 - t1) / 1e6d, response.headers()));
                return response;
            }
        };
    }

    @Provides
    @Singleton
     static Retrofit provideRetrofit(GsonConverterFactory factory, RxJava2CallAdapterFactory callAdapterFactory, OkHttpClient httpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(factory)
                .addCallAdapterFactory(callAdapterFactory)
                .client(httpClient)
                .build();
    }

    @Provides
    @Singleton
     static WebService provideService(Retrofit retrofit) {
        return retrofit.create(WebService.class);
    }
}
