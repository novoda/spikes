package novoda.androidkotlinall4mvp

sealed class HomeModel {
    object Loading: HomeModel()
    data class Error(val text: String): HomeModel()
    data class Idle(val text: String): HomeModel()
}



