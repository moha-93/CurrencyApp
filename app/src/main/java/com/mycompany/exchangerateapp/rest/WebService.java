package com.mycompany.exchangerateapp.rest;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebService {

    @GET("latest?base=USD")
    Observable<ResponseBody> getCurrencyRate();

    @GET("history?start_at=2019-12-11&end_at=2019-12-17&base=USD")
    Observable<ResponseBody> getCurrencyGraph(@Query("symbols")String key);
}
