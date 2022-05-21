package com.decodetalkers.personalinterestsmanager.ui.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel

class PlaylistAdapter(var onClick: (plId: Int, plName:String) -> Unit) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    private var Item_List = ArrayList<MediaItemOfListModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_artists_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.itemName.text = Item_List[position].item_name
        if (Item_List[position].item_image == "") {
            if (Item_List.get(position).item_type == "movies")
                holder.itemImage.load(R.drawable.ic_movies2) {
                    crossfade(true)
                    crossfade(500)
                }
            if (Item_List.get(position).item_type == "music")
                holder.itemImage.load(R.drawable.ic_music) {
                    crossfade(true)
                    crossfade(500)
                }
            if (Item_List.get(position).item_type == "books")
                holder.itemImage.load(R.drawable.ic_books) {
                    crossfade(true)
                    crossfade(500)
                }
        } else {
            if (Item_List.get(position).item_type == "movies") {
                holder.itemImage.load(MediaItemRecycler.MOVIE_IMAGE_LINK_M + Item_List.get(position).item_image) {
                    crossfade(true)
                    crossfade(500)
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
                Item_List.get(position).item_id.toInt(),
                Item_List.get(position).item_name
            )
        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
    }

    fun setItem_List(list: List<MediaItemOfListModel>) {
        Item_List = list as ArrayList<MediaItemOfListModel>
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView
        val itemImage: ImageView
        var selected: Boolean = false

        init {
            itemName = itemView.findViewById(R.id.artistName)
            itemImage = itemView.findViewById(R.id.artistImage)
        }
    }
}