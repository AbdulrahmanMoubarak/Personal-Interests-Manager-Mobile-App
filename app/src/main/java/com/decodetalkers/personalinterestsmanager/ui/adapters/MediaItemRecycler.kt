package com.decodetalkers.personalinterestsmanager.ui.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.radioalarm.application.MainApplication

class MediaItemRecycler(var onClick: (itemId: String, image: ImageView, type: String) -> Unit) :
    RecyclerView.Adapter<MediaItemRecycler.MediaItemViewHolder>() {
    private var Item_List = ArrayList<MediaItemOfListModel>()

    companion object {
        const val TYPE_SONG = "song"
        const val TYPE_MOVIE = "movie"
        const val TYPE_BOOK = "book"
        const val TYPE_ALBUM = "album"
        const val TYPE_ARTIST = "artist"
        const val TYPE_CAST_MEMBER = "cast_member"
        const val MOVIE_IMAGE_LINK_M = "https://image.tmdb.org/t/p/w300"
        const val MOVIE_IMAGE_LINK_L = "https://image.tmdb.org/t/p/w500"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemViewHolder {
        return MediaItemViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_item_inlist_recycler, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) {
        holder.itemName.text = Item_List.get(position).item_name

        if (Item_List.get(position).item_type.lowercase() == TYPE_CAST_MEMBER) {
            holder.itemRole.visibility = View.VISIBLE
            holder.itemRole.text = Item_List.get(position).item_id

            holder.itemImage.layoutParams.height = 350
            holder.itemImage.layoutParams.width = 350
            holder.itemNameCard.layoutParams.width = 350

            holder.itemNameCard.layoutParams.height = 200
        }

        if (Item_List.get(position).item_type.lowercase() == TYPE_MOVIE || Item_List.get(position).item_type.lowercase() == TYPE_CAST_MEMBER) {
            holder.itemImage.load(MOVIE_IMAGE_LINK_M + Item_List.get(position).item_image) {
                crossfade(true)
                crossfade(500)
            }
        } else {

            if (Item_List.get(position).item_type.lowercase() == TYPE_ALBUM) {
                holder.itemCard.radius = 0f
            }

            holder.itemImage.layoutParams.height = 350
            holder.itemImage.layoutParams.width = 350
            holder.itemNameCard.layoutParams.width = 350

            if (Item_List.get(position).item_type.lowercase() == TYPE_ARTIST) {
                MainApplication.getAppContext()?.let {
                    Glide.with(it)
                        .load(Item_List.get(position).item_image)
                        .circleCrop()
                        .into(holder.itemImage)
                }
            } else {
                holder.itemImage.load(Item_List.get(position).item_image) {
                    crossfade(true)
                    crossfade(500)
                }
            }
        }

        holder.itemView.setOnClickListener {

            onClick(
                Item_List.get(position).item_id,
                holder.itemImage,
                Item_List.get(position).item_type
            )

        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
        notifyDataSetChanged()
    }

    fun setItem_List(list: List<MediaItemOfListModel>) {
        Item_List = list as ArrayList<MediaItemOfListModel>
    }

    class MediaItemViewHolder : RecyclerView.ViewHolder {
        val itemName: TextView
        val itemImage: ImageView
        val itemNameCard: ConstraintLayout
        val itemCard: CardView
        val itemRole: TextView

        constructor(itemView: View) : super(itemView) {
            itemName = itemView.findViewById(R.id.media_item_name)
            itemImage = itemView.findViewById(R.id.media_item_image)
            itemNameCard = itemView.findViewById(R.id.media_item_name_card)
            itemCard = itemView.findViewById(R.id.media_item_card)
            itemRole = itemView.findViewById(R.id.media_item_role)
        }
    }
}