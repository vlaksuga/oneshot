package com.rockteki.aa.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.R
import com.rockteki.aa.model.Account


class AccountListAdapter internal constructor(context: Context, currentAccountList: List<Account>)
    : RecyclerView.Adapter<AccountListAdapter.CurrentAccountListViewHolder>() {

    private lateinit var adapterListener: OnItemClickListener
    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<Account> = currentAccountList;


    inner class CurrentAccountListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView_account);
        private val accountNameItemView: TextView = itemView.findViewById(R.id.account_name_from_item_account);

        fun bind(currentAccount: Account) {
            cardView.setOnClickListener { adapterListener.onItemClick(currentAccount); }
            accountNameItemView.text = currentAccount.accountName;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentAccountListViewHolder {
        val itemView = inflater.inflate(R.layout.item_account, parent, false);
        return CurrentAccountListViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: CurrentAccountListViewHolder, position: Int) {
        val currentAccount = items[position];
        holder.bind(currentAccount);
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    interface OnItemClickListener {
        fun onItemClick(account: Account);
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapterListener = listener;
    }
}