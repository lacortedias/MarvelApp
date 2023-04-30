package com.example.marvelapp.framework.imageloader

import android.widget.ImageView
import com.bumptech.glide.Glide
import javax.inject.Inject

class GlideImageLoader @Inject constructor() : ImageLoader {
    override fun load(
        imageView: ImageView,
        imageUrl: String,
        placeHolder: Int,
        fallback: Int
    ) {
        Glide.with(imageView.rootView)
            .load(imageUrl)
            .placeholder(placeHolder)
            .fallback(fallback)
            .into(imageView)
    }
}