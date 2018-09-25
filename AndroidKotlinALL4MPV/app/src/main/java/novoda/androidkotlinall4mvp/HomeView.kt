package novoda.androidkotlinall4mvp

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*

class HomeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), Displayer {

    private lateinit var displayerActions: DisplayerActions

    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = VERTICAL
        gravity = Gravity.CENTER

        button.setOnClickListener { displayerActions.requestLoad() }
    }

    fun attach(displayerActions: DisplayerActions) {
        this.displayerActions = displayerActions
    }

    override fun displayContent(text: String) {
        label.text = text
    }

    override fun displayLoading() {
        label.text = "Loading..."
    }

    override fun displayError() {
        label.text = "Error, sorry!"
    }
}

