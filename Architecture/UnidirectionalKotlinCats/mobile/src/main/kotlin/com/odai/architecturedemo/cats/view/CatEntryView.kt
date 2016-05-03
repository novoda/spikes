package com.odai.architecturedemo.cats.view

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.LinearLayout
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.favourite.model.ActionState
import com.odai.architecturedemo.favourite.model.FavouriteState
import com.odai.architecturedemo.favourite.model.FavouriteStatus
import com.odai.architecturedemo.imageloader.Crop
import com.odai.architecturedemo.imageloader.load
import kotlinx.android.synthetic.main.cat_entry_view.view.*

class CatEntryView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun display(cat: Cat, favouriteState: FavouriteState, listener: CatsPresenter.CatClickedListener) {
        catLabel.text = cat.name

        displayFavouriteIndicator(cat, favouriteState, listener)

        load(cat.image) {
            cropAs { Crop.CIRCLE_CROP }
            into { avatar }
        }

        setOnClickListener {
            listener.onCatClicked(cat)
        }
    }

    private fun displayFavouriteIndicator(cat: Cat, favouriteState: FavouriteState, listener: CatsPresenter.CatClickedListener) {
        favouriteIndicator.clearColorFilter()
        favouriteIndicator.setOnClickListener { listener.onFavouriteClicked(cat, favouriteState) }
        favouriteIndicator.setImageDrawable(favouriteDrawable(favouriteState.status))
        favouriteIndicator.isEnabled = favouriteState.state == ActionState.CONFIRMED
        if (favouriteState.state == ActionState.PENDING) {
            favouriteIndicator.setColorFilter(R.color.grey, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun favouriteDrawable(favouriteStatus: FavouriteStatus) = resources.getDrawable(when (favouriteStatus) {
        FavouriteStatus.FAVOURITE -> android.R.drawable.star_big_on
        FavouriteStatus.UN_FAVOURITE -> android.R.drawable.star_big_off
    }, null)

}
