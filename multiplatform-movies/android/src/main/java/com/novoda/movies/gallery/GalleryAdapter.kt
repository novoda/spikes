package com.novoda.movies.gallery

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class GalleryAdapter : RecyclerView.Adapter<GalleryItemViewHolder>() {

    private var moviePosters = listOf<MoviePoster>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GalleryItemViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int = moviePosters.size

    override fun onBindViewHolder(viewHolder: GalleryItemViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateWith(gallery: Gallery) {
        moviePosters = gallery.moviePosters
        notifyDataSetChanged()
    }

}

class GalleryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


}