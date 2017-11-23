package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.model;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.novoda.frankboylan.meetingseating.network.ConnectionStatus;
import com.novoda.frankboylan.meetingseating.network.RetrofitHelper;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.AdvHeatmapDisplayer;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.AdvHeatmapPresenter;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class AdvHeatmapPresenterImpl implements AdvHeatmapPresenter {
    private AdvHeatmapDisplayer displayer;
    private AdvHeatmapModel model;
    private InternalDatabase database;

    public AdvHeatmapPresenterImpl(InternalDatabase database, AssetManager assetManager, AdvHeatmapModelImpl model) {
        this.database = database;
        this.model = model;
    }

    @Override
    public void bind(AdvHeatmapDisplayer displayer) {
        this.displayer = displayer;
    }

    @Override
    public void unbind() {
        displayer = null;
    }

    @Override
    public void startPresenting() {
        if (ConnectionStatus.hasActiveInternetConnection()) {
            //ToDo: check if Endpoint Timestamp > metaDao().getLatestTimestamp() THEN add replace data with new Endpoint data
            Observable<AdvHeatmapMeta> metaObservable = new RetrofitHelper().serviceMetaData();

            metaObservable.subscribe(new Observer<AdvHeatmapMeta>() {      // ToDo: SimpleObserver & .single
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(AdvHeatmapMeta advHeatmapMeta) {
                    long endpointLastUpdated = Long.valueOf(advHeatmapMeta.getLastUpdated());
                    Log.d("EndpointLastUpdated", endpointLastUpdated + "");
                    if (database.metaDao().getLastUpdated() == null) {
                        AdvHeatmapMeta ahm = new AdvHeatmapMeta();
                        ahm.setLastUpdated("0");
                        database.metaDao().insertMetadata(ahm);
                    }
                    long databaseLastUpdated = Long.valueOf(database.metaDao().getLastUpdated());
                    Log.d("DatabaseLastUpdated", databaseLastUpdated + "");
                    if (databaseLastUpdated <= endpointLastUpdated) {
                        database.metaDao().deleteAllMetadata();
                        database.metaDao().insertMetadata(advHeatmapMeta);
                        fetchMainData();
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                    updateListsFromDB();
                }
            });

            // ToDo: Load Dataset automatically (from local files)
            //model.loadLocalDataset();
        }
    }

    private void fetchMainData() {
        Observable<AdvHeatmapRoom> dataObservable = new RetrofitHelper().serviceDataMain();

        dataObservable.subscribe(new Observer<AdvHeatmapRoom>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AdvHeatmapRoom room) {
                Log.d("AdvHeatmapPres", room.getRoomId() + "");
                database.roomDao().insertRoom(room);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void updateListsFromDB() {
        AsyncTask.execute(() -> {
            //displayer.updateRoomList(database.roomDao().getAllRooms());
            //displayer.updateSeatList(database.seatDao().getAllSeats());
            //displayer.updateMetaTimestamp();
        });
    }
}
