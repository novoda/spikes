package novoda.androidkotlinall4mvp

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*

class HomeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    fun display(model: HomeModel) {
        when (model) {
            is HomeModel.Loading -> label.text == "Loading..."
            is HomeModel.Idle -> label.text = model.text
            is HomeModel.Error -> label.text = "Error, sorry!"
        }
    }

    fun setEventReporter(report: (MainActivity.HomeEvent) -> Unit) {
        button.setOnClickListener {
            report(MainActivity.HomeEvent.SelectedLoadButton)
        }
    }

}

