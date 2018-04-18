package com.novoda.spikes.arcore

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ModelsAdapter(private val layoutInflater: LayoutInflater, private val listener: Listener): RecyclerView.Adapter<FooVH>() {

    private var models = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooVH {
        return FooVH(layoutInflater.inflate(R.layout.model_item_view, parent, false))
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: FooVH, position: Int) {
        val model = models[position]
        holder.button.text = model
        holder.button.setOnClickListener {
            listener.onModelClicked(model)
        }
    }

    fun setModels(models: List<String>) {
        this.models = models
        notifyDataSetChanged()
    }

    interface Listener {
        fun onModelClicked(model: String)
    }

}

class FooVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val button: Button = itemView.findViewById(R.id.button)

}
