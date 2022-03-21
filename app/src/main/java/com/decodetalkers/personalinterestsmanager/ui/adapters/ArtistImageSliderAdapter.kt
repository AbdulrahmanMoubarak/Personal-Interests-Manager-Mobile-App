package com.decodetalkers.personalinterestsmanager.ui.adapters

import com.smarteist.autoimageslider.SliderViewAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


import android.view.LayoutInflater
import coil.load
import com.decodetalkers.personalinterestsmanager.R


class ArtistImageSliderAdapter: SliderViewAdapter<ArtistImageSliderAdapter.Holder>() {

    private var imageList = arrayListOf<String>()

    fun setAdapterImages(images: ArrayList<String>){
        this.imageList = images
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): Holder {
        val view: View =
            LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.artist_image_slider_adapter, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.imageView?.load(imageList.get(position)){
            crossfade(true)
            crossfade(500)
        }
    }

    class Holder(itemView: View) : ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.atrist_adapter_image)
        }
    }


}