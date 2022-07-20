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

class ChoiceSummaryAdapter internal constructor(context: Context, choiceSummary: List<Choice>)
    : RecyclerView.Adapter<ChoiceSummaryAdapter.ChoiceSummaryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<Choice> = choiceSummary;


    inner class ChoiceSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val choiceProductThumbItemView: ImageView = itemView.findViewById(R.id.cardView_item_choice_summary_product_thumb);
        private val choiceProductNameItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_summary_product_name);
        private val choiceProductSizeItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_summary_product_size);
        private val choiceQuantityItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_summary_quantity);
        private val choiceTotalPriceItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_summary_total_price);
        private val choiceMemoItemView: TextView = itemView.findViewById(R.id.cardView_item_choice_summary_memo);

        fun bind(currentChoice: Choice) {
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceSummaryViewHolder {
        val itemView = inflater.inflate(R.layout.item_choice_summary, parent, false);
        return ChoiceSummaryViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: ChoiceSummaryViewHolder, position: Int) {
        val currentChoice = items[position];
        holder.bind(currentChoice);
    }

    override fun getItemCount(): Int {
        return items.size;
    }

}