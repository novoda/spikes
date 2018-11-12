package com.novoda.playground.multiplatform.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Presenter(private val api: Api = Api()) : CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = ApplicationDispatcher + job

    fun startPresenting(view: View) {
        job = Job()

        launch {
            val recipe = api.recipe()
            view.render(recipe)
        }
    }

    fun stopPresenting() {
        job.cancel()
    }

    interface View {
        fun render(recipe: Recipe)
    }

}
