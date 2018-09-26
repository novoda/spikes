package novoda.androidkotlinall4mvp

sealed class HomeModel {
    object Loading: HomeModel()
    data class Error(val error: String): HomeModel()
    data class Idle(val text: String): HomeModel()
    data class Reload(val text: String): HomeModel()
    data class Retry(val error: String): HomeModel()
}

sealed class HomeEvent {
    object SelectedLoadButton : HomeEvent()
}



