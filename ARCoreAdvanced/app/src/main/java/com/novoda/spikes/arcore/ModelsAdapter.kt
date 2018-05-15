package com.novoda.spikes.arcore

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.novoda.spikes.arcore.poly.PolyAsset

class ModelsAdapter(private val layoutInflater: LayoutInflater, private val listener: Listener): RecyclerView.Adapter<ModelVH>() {

    private var models = emptyList<PolyAsset>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelVH {
        return ModelVH(layoutInflater.inflate(R.layout.model_item_view, parent, false))
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: ModelVH, position: Int) {
        val model = models[position]
        holder.modelLabelView.text = "${model.displayName} by ${model.authorName}\nSearched as ${model.representsLabel}"
        holder.itemView.setOnClickListener {
            listener.onModelClicked(model)
        }
        Glide.with(holder.itemView).load(model.thumbnail).into(holder.thumbnailView)
    }

    fun setModels(models: List<PolyAsset>) {
        this.models = models
        notifyDataSetChanged()
    }

    interface Listener {
        fun onModelClicked(model: PolyAsset)
    }

}

class ModelVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val thumbnailView: ImageView = itemView.findViewById(R.id.thumbnailView)
    val modelLabelView: TextView = itemView.findViewById(R.id.modelLabelView)

}
