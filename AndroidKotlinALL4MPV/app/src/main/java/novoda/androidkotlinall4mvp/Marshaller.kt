package novoda.androidkotlinall4mvp

fun ApiHomeModel.toHomeModel(): HomeModel {
    return HomeModel.Idle("$title, $subtitle")
}
