package com.example.weather5days.screens.weather;

import com.example.weather5days.api.ApiFactory;
import com.example.weather5days.api.ApiService;
import com.example.weather5days.pojo.Weather5days;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WeatherPresenter {

    private String units = "metric";
    private String lang = "ru";
    private String appid = "292fc3d250148f4c77a7a51ac68a6302";

    private CompositeDisposable compositeDisposable;
    private WeatherView weatherView;

    public WeatherPresenter(WeatherView weatherView) {
        this.weatherView = weatherView;
    }

    public void getWeather() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();

        Disposable disposable = apiService.getWeather5days(WeatherActivity.getLat(), WeatherActivity.getLon(), units, lang, appid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Weather5days>() {
                    @Override
                    public void accept(Weather5days weather5days) throws Exception {
                        weatherView.showData(weather5days);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        weatherView.showError();
                    }
                });
        compositeDisposable.add(disposable);
    }



    public void disposeDisposable(){
        if(compositeDisposable != null){
            compositeDisposable.dispose();
        }
    }
}
