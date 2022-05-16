package com.decodetalkers.personalinterestsmanager.ui.adapters;

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.radioalarm.application.MainApplication
import java.lang.reflect.Method


class FavArtistAdapter(var onClick: (itemId: String) -> Unit) :
    RecyclerView.Adapter<FavArtistAdapter.FavArtistViewHolder>() {
    private var Item_List = ArrayList<MediaItemOfListModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavArtistViewHolder {
        return FavArtistViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_atrists_adapter, parent, false)
        )
    }


    override fun onBindViewHolder(holder: FavArtistViewHolder, position: Int) {
        holder.itemName.text = Item_List[position].item_name

        if (Item_List[position].selected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setBackgroundColor(
                    MainApplication.getApplication().getColor(R.color.darkGreen)
                )
            }
        } else{
            val packageInfo = MainApplication.getAppContext().getPackageManager().getPackageInfo(
                MainApplication.getAppContext().getPackageName(),
                PackageManager.GET_META_DATA
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (packageInfo.applicationInfo.theme == R.style.Theme_PersonalInterestsManagerPrimary)
                    holder.itemView.setBackgroundColor(
                        MainApplication.getApplication().getColor(R.color.white)
                    )
                else if (packageInfo.applicationInfo.theme == R.style.Theme_PersonalInterestsManagerDark)
                    holder.itemView.setBackgroundColor(
                        MainApplication.getApplication().getColor(R.color.darkGray)
                    )
            }
        }

        MainApplication.getAppContext().let {
            Glide.with(it)
                .load(Item_List.get(position).item_image)
                .circleCrop()
                .into(holder.itemImage)
        }

        holder.itemView.setOnClickListener {
            if (!holder.selected) {
                holder.selected = true
                Item_List[position].selected = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.itemView.setBackgroundColor(
                        MainApplication.getApplication().getColor(R.color.darkGreen)
                    )
                }
            } else {
                holder.selected = false
                Item_List[position].selected = false
                val packageInfo = MainApplication.getAppContext().getPackageManager().getPackageInfo(
                    MainApplication.getAppContext().getPackageName(),
                    PackageManager.GET_META_DATA
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (packageInfo.applicationInfo.theme == R.style.Theme_PersonalInterestsManagerPrimary)
                        holder.itemView.setBackgroundColor(
                            MainApplication.getApplication().getColor(R.color.white)
                        )
                    else if (packageInfo.applicationInfo.theme == R.style.Theme_PersonalInterestsManagerDark)
                        holder.itemView.setBackgroundColor(
                            MainApplication.getApplication().getColor(R.color.darkGray)
                        )
                }
            }
            onClick(Item_List[position].item_id)
        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
    }

    fun setItem_List(list: List<MediaItemOfListModel>) {
        Item_List = list as ArrayList<MediaItemOfListModel>
    }

    class FavArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView
        val itemImage: ImageView
        var selected: Boolean = false
        init {
            itemName = itemView.findViewById(R.id.artistName)
            itemImage = itemView.findViewById(R.id.artistImage)
        }
    }

}