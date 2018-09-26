package novoda.androidkotlinall4mvp

class ServiceClient {

    private lateinit var liveDataHomeModel: (HomeModel) -> Unit

    fun attach(liveDataHomeModel: (HomeModel) -> Unit) {
        this.liveDataHomeModel = liveDataHomeModel
    }

    fun loadContent() {
        liveDataHomeModel(HomeModel.Loading)

        Thread(Runnable {
            Thread.sleep(200)

            val apiHomeModel = ApiHomeModel("Hey", "team")

            liveDataHomeModel(apiHomeModel.toHomeModel())
        }).start()
    }
}

