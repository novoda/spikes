package novoda.androidkotlinall4mvp

import android.arch.lifecycle.MutableLiveData

class ServiceClient {

    private lateinit var liveDataHomeModel: MutableLiveData<HomeModel>

    fun attach(liveDataHomeModel: MutableLiveData<HomeModel>) {
        this.liveDataHomeModel = liveDataHomeModel
    }

    fun loadContent() {
        liveDataHomeModel.value = HomeModel.Loading

        Thread(Runnable {
            Thread.sleep(200)

            val apiHomeModel = ApiHomeModel("Hey", "team")

            liveDataHomeModel.postValue(apiHomeModel.toHomeModel())
        }).start()
    }
}
