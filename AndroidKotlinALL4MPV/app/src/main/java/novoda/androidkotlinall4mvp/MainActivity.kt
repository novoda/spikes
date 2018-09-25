package novoda.androidkotlinall4mvp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val presenter: Presenter = Presenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(displayer = home_view, lifeCycleOwner = this)

        button.setOnClickListener { presenter.loadContent() }
    }
}
