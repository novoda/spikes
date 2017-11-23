package com.novoda.frankboylan.meetingseating.rooms.heatmap;

import com.novoda.frankboylan.meetingseating.network.AwsSeatMonitorService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class HeatmapSeatListModelImpl implements HeatmapSeatListModel {
    private AwsSeatMonitorService service;
    private HeatMapSeatListPresenter heatMapSeatListPresenter;

    HeatmapSeatListModelImpl(HeatMapSeatListPresenter heatMapSeatListPresenter) {
        this.heatMapSeatListPresenter = heatMapSeatListPresenter;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AwsSeatMonitorService.HEATMAP_BASE)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        service = retrofit.create(AwsSeatMonitorService.class);
    }

    @Override
    public void retrieveData(String roomId) {
        service.seatHeatmapData(roomId, 1234, 4321).enqueue(new Callback<HeatmapSeatData>() { // Constant start & end query for now
            @Override
            public void onResponse(Call<HeatmapSeatData> call, Response<HeatmapSeatData> response) {
                HeatmapSeatData heatmapSeatData = response.body();
                if (response.isSuccessful() && heatmapSeatData != null) {
                    heatMapSeatListPresenter.updateList(heatmapSeatData.getSeats());
                }
            }
            @Override
            public void onFailure(Call<HeatmapSeatData> call, Throwable t) {
            }
        });
    }
}
