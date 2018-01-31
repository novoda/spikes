package com.novoda.androidstoreexample.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.novoda.androidstoreexample.R
import com.novoda.androidstoreexample.models.Product

class ProductListAdapter(private val context: Context,
                         private val products: List<Product>,
                         private val unitClicked: (Product) -> Unit) : RecyclerView.Adapter<ProductListAdapter.Holder>(){


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false)
        return Holder(view, unitClicked)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bindProduct(products[position], context)
    }


    inner class Holder(itemView: View?, private val itemClick: (Product) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val productImage = itemView?.findViewById<ImageView>(R.id.productImage)
        private val productName = itemView?.findViewById<TextView>(R.id.productTitle)
        private val productPrice = itemView?.findViewById<TextView>(R.id.productPrice)

        fun bindProduct(product: Product, context: Context) {
            val resourceId: Int = context.resources.getIdentifier(product.image, "drawable", context.packageName)
            productImage?.setImageResource(resourceId)
            productName?.text = product.title
            productPrice?.text = product.price
            itemView.setOnClickListener { itemClick(product) }
        }
    }
}