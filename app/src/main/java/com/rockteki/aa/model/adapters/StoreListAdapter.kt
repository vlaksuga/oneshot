package com.rockteki.aa.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.R
import com.rockteki.aa.model.Store


class StoreListAdapter internal constructor(context: Context, currentStoreList: List<Store>)
    : RecyclerView.Adapter<StoreListAdapter.CurrentStoreListViewHolder>() {

    private lateinit var adapterListener: OnItemClickListener
    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<Store> = currentStoreList;


    inner class CurrentStoreListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView_store_item);
        private val storeNameItemView: TextView = itemView.findViewById(R.id.cardView_store_item_store_name);

        fun bind(currentStore: Store) {
            cardView.setOnClickListener { adapterListener.onItemClick(currentStore); }
            storeNameItemView.text = currentStore.storeName;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentStoreListViewHolder {
        val itemView = inflater.inflate(R.layout.item_store, parent, false);
        return CurrentStoreListViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: CurrentStoreListViewHolder, position: Int) {
        val currentStore = items[position];
        holder.bind(currentStore);
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    interface OnItemClickListener {
        fun onItemClick(store: Store);
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapterListener = listener;
    }
}