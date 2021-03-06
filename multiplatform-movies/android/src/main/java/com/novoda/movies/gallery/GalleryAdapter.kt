package com.novoda.movies.gallery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.novoda.movies.R


class GalleryAdapter(private val moviePosterWidthInPixels: Int) : RecyclerView.Adapter<GalleryItemViewHolder>() {

    private var moviePosters = listOf<MoviePoster>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GalleryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return GalleryItemViewHolder(view, moviePosterWidthInPixels)
    }

    override fun getItemCount(): Int = moviePosters.size

    override fun onBindViewHolder(viewHolder: GalleryItemViewHolder, position: Int) {
        val movie = moviePosters[position]
        viewHolder.bind(movie)
    }

    fun updateWith(gallery: Gallery) {
        moviePosters = gallery.moviePosters
        notifyDataSetChanged()
    }
}

class GalleryItemViewHolder(itemView: View, moviePosterWidthInPixels: Int) : RecyclerView.ViewHolder(itemView) {

    private val imageView = itemView.findViewById<ImageView>(R.id.poster_image)

    init {
        imageView.layoutParams = ViewGroup.LayoutParams(moviePosterWidthInPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun bind(movie: MoviePoster) {
        Glide.with(imageView.context).load(movie.thumbnailUrl).into(imageView)
    }
}
