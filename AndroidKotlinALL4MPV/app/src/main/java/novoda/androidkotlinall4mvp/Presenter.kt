package novoda.androidkotlinall4mvp

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class Presenter(private val serviceClient: ServiceClient = ServiceClient()) {

    private val liveDataHomeModel: MutableLiveData<HomeModel> = MutableLiveData()

    fun attach(displayer: Displayer, lifeCycleOwner: LifecycleOwner) {
        liveDataHomeModel.observe(lifeCycleOwner, homeModelResponse(displayer))
        serviceClient.attach(liveDataHomeModel)
        displayer.attach(displayerActions())
    }

    private fun displayerActions(): DisplayerActions {
        return object : DisplayerActions {
            override fun requestLoad() {
                serviceClient.loadContent()
            }
        }
    }

    private fun homeModelResponse(displayer: Displayer): Observer<HomeModel> {
        return Observer {
            when (it) {
                is HomeModel.Loading -> displayer.displayLoading()
                is HomeModel.Idle -> displayer.displayContent(it.text)
                is HomeModel.Error -> displayer.displayError()
            }
        }
    }
}
