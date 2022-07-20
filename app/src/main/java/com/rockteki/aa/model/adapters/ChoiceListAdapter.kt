package com.rockteki.aa.model.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.R
import com.rockteki.aa.model.Choice

class ChoiceListAdapter internal constructor(context: Context, currentChoiceList: List<Choice>)
    : RecyclerView.Adapter<ChoiceListAdapter.CurrentChoiceListViewHolder>() {

    private lateinit var adapterListener: OnItemClickListener;
    private lateinit var closeListener: OnCloseClickListener;
    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<Choice> = currentChoiceList;


    inner class CurrentChoiceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView_choice_item);
        private val choiceProductThumbItemView: ImageView = itemView.findViewById(R.id.cardView_item_choice_product_thumb);
        private val choiceProductNameItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_product_name);
        private val choiceProductSizeItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_product_size);
        private val choiceQuantityItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_quantity);
        private val choiceTotalPriceItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_total_price);
        private val choiceMemoItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_memo);
        private val choiceCloseItemView: ImageView = itemView.findViewById(R.id.cardView_item_choice_close);



        @SuppressLint("SetTextI18n")
        fun bind(currentChoice: Choice) {
            cardView.setOnClickListener { adapterListener.onItemClick(currentChoice); }
            choiceProductThumbItemView.setImageResource(R.drawable.ice_americano);
            when(currentChoice.choiceProductThumb) {
                "HOT" -> { choiceProductThumbItemView.setImageResource(R.drawable.papercup); }
                "ICE" -> { choiceProductThumbItemView.setImageResource(R.drawable.ice_americano); }
                "SMT" -> { choiceProductThumbItemView.setImageResource(R.drawable.smoothe); }
                "TEA" -> { choiceProductThumbItemView.setImageResource(R.drawable.tea); }
                "JCE" -> { choiceProductThumbItemView.setImageResource(R.drawable.juice); }
                "ADE" -> { choiceProductThumbItemView.setImageResource(R.drawable.juice); }
                "ETC" -> { choiceProductThumbItemView.setImageResource(R.drawable.bread); }
                else -> { choiceProductThumbItemView.setImageResource(R.drawable.papercup); }
            }
            choiceProductNameItemView.text = currentChoice.choiceProductName;
            choiceProductSizeItemView.text = currentChoice.choiceProductSize;
            choiceQuantityItemView.text = "* " + currentChoice.choiceQuantity.toString() + " :";
            choiceTotalPriceItemView.text = "%,d".format (currentChoice.choiceQuantity * currentChoice.choiceProductPrice) + "원";
            choiceMemoItemView.text = currentChoice.choiceMemo;
            choiceCloseItemView.setOnClickListener { closeListener.onCloseClick(currentChoice) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentChoiceListViewHolder {
        val itemView = inflater.inflate(R.layout.item_choice, parent, false);
        return CurrentChoiceListViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: CurrentChoiceListViewHolder, position: Int) {
        val currentChoice = items[position];
        holder.bind(currentChoice);
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    fun getItemAt(position: Int): Choice {
        return items[position];
    }

    interface OnItemClickListener {
        fun onItemClick(choice: Choice);
    }

    interface OnCloseClickListener {
        fun onCloseClick(choice: Choice);
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapterListener = listener;
    }

    fun setOnCloseClickListener(listener: OnCloseClickListener) {
        this.closeListener = listener
    }

}