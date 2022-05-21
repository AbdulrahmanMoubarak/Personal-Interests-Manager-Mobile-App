package com.decodetalkers.personalinterestsmanager.ui.adapters

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.radioalarm.application.MainApplication

class MediaItemArrayAdapter(
    var onClick: (itemId: String, image: ImageView, type: String) -> Unit,
    @NonNull context: Context,
    var mediaItemList: List<MediaItemOfListModel>
) : ArrayAdapter<MediaItemOfListModel>(context, 0, mediaItemList) {


    fun setItemList(mediaItemList: List<MediaItemOfListModel>){
        this.mediaItemList = mediaItemList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.media_item_inlist_recycler, parent, false);
        }
        val mediaItem = mediaItemList.get(position)

        val itemName = listItemView?.findViewById(R.id.media_item_name) as TextView
        val itemImage = listItemView.findViewById(R.id.media_item_image) as ImageView
        val itemNameCard = listItemView.findViewById(R.id.media_item_name_card) as ConstraintLayout
        val itemCard = listItemView.findViewById(R.id.media_item_card) as CardView
        val itemRole = listItemView.findViewById(R.id.media_item_role) as TextView


        itemName.text = mediaItem.item_name

        if (mediaItem.item_type.lowercase() == MediaItemRecycler.TYPE_CAST_MEMBER) {
            itemRole.visibility = View.VISIBLE
            itemRole.text = mediaItem.item_id

            itemImage.layoutParams.height = 350
            itemImage.layoutParams.width = 350
            itemNameCard.layoutParams.width = 350

            itemNameCard.layoutParams.height = 200
        }

        if (mediaItem.item_type.lowercase() == MediaItemRecycler.TYPE_MOVIE || mediaItem.item_type.lowercase() == MediaItemRecycler.TYPE_CAST_MEMBER) {
            itemImage.load(MediaItemRecycler.MOVIE_IMAGE_LINK_M + mediaItem.item_image) {
                crossfade(true)
                crossfade(500)
            }
        } else {

            if (mediaItem.item_type.lowercase() == MediaItemRecycler.TYPE_ALBUM) {
                itemCard.radius = 0f
            }

            itemImage.layoutParams.height = 350
            itemImage.layoutParams.width = 350
            itemNameCard.layoutParams.width = 350

            if (mediaItem.item_type.lowercase() == MediaItemRecycler.TYPE_ARTIST) {
                MainApplication.getAppContext()?.let {
                    Glide.with(it)
                        .load(mediaItem.item_image)
                        .circleCrop()
                        .into(itemImage)
                }
            } else {
                itemImage.load(mediaItem.item_image) {
                    crossfade(true)
                    crossfade(500)
                }
            }
        }

        listItemView.setOnClickListener {
            onClick(
                mediaItem.item_id,
                itemImage,
                mediaItem.item_type
            )
        }

        return listItemView
    }
}