package com.decodetalkers.personalinterestsmanager.ui.adapters;

import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.GenreModel
import com.decodetalkers.radioalarm.application.MainApplication

class FavGenresAdapter(var onClick: (itemId: String) -> Unit) : RecyclerView.Adapter<FavGenresAdapter.FavGenresViewHolder>() {
    private var Item_List = ArrayList<GenreModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavGenresViewHolder {
        return FavGenresViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_genres_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavGenresViewHolder, position: Int) {
        holder.itemName.text = Item_List[position].name

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
            onClick(Item_List[position].name)
        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
    }

    fun setItem_List(list: List<GenreModel>) {
        Item_List = list as ArrayList<GenreModel>
    }

    class FavGenresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView
        var selected: Boolean = false
        init {
            itemName = itemView.findViewById(R.id.genreText)
        }
    }
}