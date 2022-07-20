package com.rockteki.aa.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.R
import com.rockteki.aa.model.MyAccountGroup


class MyAccountGroupListAdapter internal constructor(context: Context, currentMyAccountGroupList: List<MyAccountGroup>)
    : RecyclerView.Adapter<MyAccountGroupListAdapter.CurrentMyUserGroupListViewHolder>() {

    private lateinit var adapterListener: OnItemClickListener
    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<MyAccountGroup> = currentMyAccountGroupList;


    inner class CurrentMyUserGroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView_my_account_group);
        private val myUserGroupNameItemView: TextView = itemView.findViewById(R.id.group_name_from_item_my_account_group);
        private val myUserGroupSizeItemView: TextView = itemView.findViewById(R.id.group_size_from_item_my_account_group);

        fun bind(currentMyAccountGroup: MyAccountGroup) {
            cardView.setOnClickListener { adapterListener.onItemClick(currentMyAccountGroup); }
            myUserGroupNameItemView.text = currentMyAccountGroup.myAccountGroupName;
            myUserGroupSizeItemView.text = currentMyAccountGroup.myAccountGroupSize.toString() + "명";
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentMyUserGroupListViewHolder {
        val itemView = inflater.inflate(R.layout.item_my_account_group, parent, false);
        return CurrentMyUserGroupListViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: CurrentMyUserGroupListViewHolder, position: Int) {
        val currentMyUserGroup = items[position];
        holder.bind(currentMyUserGroup);
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    interface OnItemClickListener {
        fun onItemClick(myAccountGroup: MyAccountGroup);
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapterListener = listener;
    }
}