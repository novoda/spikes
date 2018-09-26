package novoda.androidkotlinall4mvp

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel

class Presenter(private val serviceClient: ServiceClient = ServiceClient()) : ViewModel() {

    private val homeModel = object : MutableLiveData<HomeModel>() {

        init {
            serviceClient.attach { postValue(it) }
        }

    }

    fun foo(lifecycleOwner: LifecycleOwner, display: (HomeModel) -> Unit) {
        homeModel.observe(lifecycleOwner, Observer { display(it!!) })
    }

    fun reportEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectedLoadButton -> {
                homeModel.postValue(HomeModel.Loading)
                serviceClient.loadContent()
            }
        }
    }

}
