package com.decodetalkers.personalinterestsmanager.ui.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.SectionModel

class SectionAdapter(val onClick:(itemId: String, image: ImageView, type: String) -> Unit) : RecyclerView.Adapter<SectionAdapter.SectionViewModel>() {
    private var Item_List = ArrayList<SectionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewModel {
        return SectionViewModel(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_recycler, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SectionViewModel, position: Int) {
        holder.sectionName.text = Item_List.get(position).section_name
        holder.itemsRecyclerAdapter.setItem_List(Item_List.get(position).section_mediaItems)
        holder.itemsRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = holder.itemsRecyclerAdapter
        }
        if(Item_List.get(position).section_mediaItems.size == 0){
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
    }

    fun setItem_List(list: List<SectionModel>) {
        Item_List = list as ArrayList<SectionModel>
    }

    private fun getItemList(): List<SectionModel>{
        return Item_List
    }

    fun addSectionItem(item: SectionModel){
        Item_List.add(item)
        notifyItemInserted(Item_List.size-1)
        Item_List.sortedWith(compareBy({it.order}))
        notifyDataSetChanged()
    }

    inner class SectionViewModel : RecyclerView.ViewHolder {
        val sectionName: TextView
        val itemsRecycler: RecyclerView
        val itemsRecyclerAdapter: MediaItemAdapter
        constructor(itemView: View) : super(itemView) {
            sectionName = itemView.findViewById(R.id.txt_section_name)
            itemsRecycler = itemView.findViewById(R.id.section_items_recycler)
            itemsRecyclerAdapter = MediaItemAdapter(onClick)
        }
    }
}