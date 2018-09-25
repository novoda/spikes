package novoda.androidkotlinall4mvp

interface Displayer {

    fun attach(displayerActions: DisplayerActions)

    fun displayContent(text: String)

    fun displayLoading()

    fun displayError()
}
