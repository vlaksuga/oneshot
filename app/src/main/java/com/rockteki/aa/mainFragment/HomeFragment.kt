package com.rockteki.aa.mainFragment

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.rockteki.aa.App
import com.rockteki.aa.ChoiceResultActivity
import com.rockteki.aa.OrderActivity
import com.rockteki.aa.R
import com.rockteki.aa.model.ChoiceRequest
import com.rockteki.aa.model.FireStoreRepository

import java.io.Serializable
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var reqLayout: LinearLayoutCompat
    private lateinit var orderLayout: LinearLayoutCompat
    private lateinit var root: View;
    private lateinit var tabLayout: TabLayout
    private lateinit var accountId: String;
    private val db = FireStoreRepository();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        App.prefs.fuid?.let { fuid -> accountId = fuid }//kjhgfd

        tabLayout = root.findViewById(R.id.home_tab)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let{ tab ->
                    when(tab.position) {
                        0 -> { showReqView() }
                        1 -> { showOrderView() }
                        else -> { showReqView() }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
        reqLayout = root.findViewById(R.id.layout_req);
        orderLayout = root.findViewById(R.id.layout_order);
        updateOpenChoiceRequestList()
        updateMyChoiceRequestList()
        return root;
    }

    private fun showOrderView() {
        reqLayout.visibility = View.GONE;
        orderLayout.visibility = View.VISIBLE;
    }

    private fun showReqView() {
        orderLayout.visibility = View.GONE;
        reqLayout.visibility = View.VISIBLE;

    }

    override fun onResume() {
        super.onResume()
        when(tabLayout.selectedTabPosition) {
            0 -> { showReqView() }
            1 -> { showOrderView() }
            else -> { showReqView() }
        }
    }


    private fun renderMyChoiceRequestList(choiceRequests : List<ChoiceRequest>) {
        context?.let { context ->
            val myChoiceRequestListAdapter = MyChoiceRequestListAdapter(context, choiceRequests)
            val myChoiceRequestListRecyclerView : RecyclerView = root.findViewById(R.id.rv_mychoicerequestlist)
            myChoiceRequestListRecyclerView.apply {
                layoutManager = LinearLayoutManager(context);
                adapter = myChoiceRequestListAdapter;
                setHasFixedSize(true);
            }
            val empty: TextView = root.findViewById(R.id.fm_home_my_req_empty)
            empty.visibility = if(choiceRequests.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun updateOpenChoiceRequestList() {
        db.listOpenChoiceRequestOf(accountId) { choiceRequests -> renderCurrentChoiceRequestList(choiceRequests) }
    }

    private fun updateMyChoiceRequestList() {
        db.listChoiceRequestCreatedBy(accountId) { choiceRequests -> renderMyChoiceRequestList(choiceRequests) }
    }

    private fun renderCurrentChoiceRequestList(choiceRequests: List<ChoiceRequest>) {
        context?.let { context ->
            val currentChoiceRequestListAdapter = CurrentChoiceRequestListAdapter(context, choiceRequests)
            val currentChoiceRequestListRecyclerView : RecyclerView = root.findViewById(R.id.rv_choicerequestlist)
                currentChoiceRequestListRecyclerView.apply {
                layoutManager = LinearLayoutManager(context);
                adapter = currentChoiceRequestListAdapter;
                setHasFixedSize(true);
            }
            val empty: TextView = root.findViewById(R.id.fm_home_req_empty)
            empty.visibility = if(choiceRequests.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun goChoiceRequest(choiceRequest: ChoiceRequest) {
        with(choiceRequest) {
            Intent(context, ChoiceResultActivity::class.java).apply {
                putExtra("choiceRequest", this@with as Serializable)
                startActivity(this);
            }
        }
    }

    private fun goOrderByReq(currentReq: ChoiceRequest) {
        with(currentReq) {
            Intent(context, OrderActivity::class.java).apply {
                putExtra("choiceRequest", this@with as Serializable)
                startActivity(this)
            }
        }
    }

    inner class CurrentChoiceRequestListAdapter internal constructor(context: Context, currentChoiceRequestList: List<ChoiceRequest>)
        : RecyclerView.Adapter<CurrentChoiceRequestListAdapter.CurrentChoiceRequestListViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context);
        private var items: List<ChoiceRequest> = currentChoiceRequestList;

        inner class CurrentChoiceRequestListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: CardView = itemView.findViewById(R.id.cardView_choice_request_item);
            private val createUserNameItemView: TextView = itemView.findViewById(R.id.cardView_choice_request_item_create_user_name);
            private val expireDate: TextView = itemView.findViewById(R.id.cardView_choice_request_item_expire_date);
            private val createUserIcon: ImageView = itemView.findViewById(R.id.cardView_choice_request_item_create_user_icon);

            fun bind(currentChoiceRequest: ChoiceRequest) {
                cardView.setOnClickListener { goChoiceRequest(currentChoiceRequest) }
                createUserNameItemView.text = currentChoiceRequest.createUserName;
                expireDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(currentChoiceRequest.expireDate) + "까지";
                if(currentChoiceRequest.createUserId == accountId) { createUserIcon.visibility = View.VISIBLE; }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentChoiceRequestListViewHolder {
            val itemView = inflater.inflate(R.layout.item_choice_request, parent, false);
            return CurrentChoiceRequestListViewHolder(itemView);
        }

        override fun onBindViewHolder(holder: CurrentChoiceRequestListViewHolder, position: Int) {
            val currentChoiceRequest = items[position];
            holder.bind(currentChoiceRequest);
        }

        override fun getItemCount(): Int {
            return items.size;
        }
    }

    inner class MyChoiceRequestListAdapter internal constructor(context: Context, reqList: List<ChoiceRequest>)
        : RecyclerView.Adapter<MyChoiceRequestListAdapter.OrderListViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context);
        private var items: List<ChoiceRequest> = reqList;

        inner class OrderListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: CardView = itemView.findViewById(R.id.cardView_my_choice_request);
            private val storeNameItemView: TextView = itemView.findViewById(R.id.my_req_store_name_from_item_order)
            private val createDateItemView: TextView = itemView.findViewById(R.id.my_req_create_date_from_item_order)

            fun bind(currentReq: ChoiceRequest) {
                cardView.setOnClickListener { goOrderByReq(currentReq) }
                storeNameItemView.text = currentReq.storeName;
                createDateItemView.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(currentReq.createDate);
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
            val itemView = inflater.inflate(R.layout.item_my_choice_request, parent, false);
            return OrderListViewHolder(itemView);
        }

        override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
            val currentReq = items[position];
            holder.bind(currentReq);
        }

        override fun getItemCount(): Int {
            return items.size;
        }
    }


}