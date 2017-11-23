package com.novoda.frankboylan.meetingseating.network;

import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapMeta;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model.AdvHeatmapRoom;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitHelper {
    public Observable<AdvHeatmapMeta> serviceMetaData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AwsSeatMonitorService.META_BASE)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();
        AwsSeatMonitorService aws = retrofit.create(AwsSeatMonitorService.class);

        return aws.advMetadata()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }

    public Observable<AdvHeatmapRoom> serviceDataMain() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AwsSeatMonitorService.BASE)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();
        AwsSeatMonitorService aws = retrofit.create(AwsSeatMonitorService.class);

        return aws.advBase()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }
}
