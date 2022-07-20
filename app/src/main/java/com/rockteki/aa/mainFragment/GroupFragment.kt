package com.rockteki.aa.mainFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.AddEditMyGroupActivity
import com.rockteki.aa.App
import com.rockteki.aa.R
import com.rockteki.aa.model.MyAccountGroup
import com.rockteki.aa.model.FireStoreRepository
import java.io.Serializable

class GroupFragment : Fragment() {
    private var myAccountGroupList : MutableList<MyAccountGroup> = arrayListOf();
    private lateinit var root: View;
    private lateinit var accountId: String;
    private val db = FireStoreRepository();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_group, container, false);
        App.prefs.fuid?.let { accountId = it }
        val addBtn: ImageView = root.findViewById(R.id.fm_group_btn_add);
            addBtn.setOnClickListener { startActivity(Intent(context, AddEditMyGroupActivity::class.java)) }
        return root;
    }

    override fun onResume() {
        super.onResume()
        updateMyUserGroupList()
    }

    private fun updateMyUserGroupList() {
        myAccountGroupList = mutableListOf();
        db.listMyAccountGroupOf(accountId){ myAccountGroups -> render(myAccountGroups) }
    }

    private fun render(myAccountGroups: List<MyAccountGroup>) {
        context?.let { context ->
            val currentMyAccountGroupListAdapter = CurrentMyAccountGroupListAdapter(context, myAccountGroups)
            val currentMyAccountGroupListRecyclerView: RecyclerView = root.findViewById(R.id.rv_myaccountgrouplist);
                currentMyAccountGroupListRecyclerView.apply {
                    adapter = currentMyAccountGroupListAdapter;
                    layoutManager = LinearLayoutManager(context);
                    setHasFixedSize(true);
            }
            val groupEmpty: TextView = root.findViewById(R.id.fm_group_empty);
            groupEmpty.visibility = if(myAccountGroups.isNotEmpty()) View.GONE else View.VISIBLE
        }

    }

    private fun editMyAccountGroup(accountGroup: MyAccountGroup) {
        Intent(context, AddEditMyGroupActivity::class.java).apply {
            putExtra("myAccountGroup", accountGroup as Serializable)
            startActivity(this)
        }
    }


    inner class CurrentMyAccountGroupListAdapter internal constructor(context: Context, currentMyAccountGroupList: List<MyAccountGroup>)
        : RecyclerView.Adapter<CurrentMyAccountGroupListAdapter.CurrentMyAccountGroupListViewHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context);
        private var items: List<MyAccountGroup> = currentMyAccountGroupList;

        inner class CurrentMyAccountGroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: CardView = itemView.findViewById(R.id.cardView_my_account_group);
            private val myAccountGroupNameItemView: TextView = itemView.findViewById(R.id.group_name_from_item_my_account_group);
            private val myAccountGroupSizeItemView: TextView = itemView.findViewById(R.id.group_size_from_item_my_account_group);

            fun bind(currentMyAccountGroup: MyAccountGroup) {
                cardView.setOnClickListener { editMyAccountGroup(currentMyAccountGroup) }
                myAccountGroupNameItemView.text = currentMyAccountGroup.myAccountGroupName;
                myAccountGroupSizeItemView.text = currentMyAccountGroup.myAccountGroupSize.toString() + "명";
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentMyAccountGroupListViewHolder {
            val itemView = inflater.inflate(R.layout.item_my_account_group, parent, false);
            return CurrentMyAccountGroupListViewHolder(itemView);
        }

        override fun onBindViewHolder(holder: CurrentMyAccountGroupListViewHolder, position: Int) {
            val currentMyAccountGroup = items[position];
            holder.bind(currentMyAccountGroup);
        }

        override fun getItemCount(): Int {
            return items.size;
        }
    }
}