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
import com.rockteki.aa.model.Product

class ProductListAdapter internal constructor(context: Context, currentProductList: List<Product>)
    : RecyclerView.Adapter<ProductListAdapter.CurrentProductListViewHolder>() {

    private lateinit var adapterListener: OnItemClickListener;
    private val inflater: LayoutInflater = LayoutInflater.from(context);
    private var items: List<Product> = currentProductList;


    inner class CurrentProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView_product_item);
        private val productThumbItemView: ImageView = itemView.findViewById(R.id.cardView_item_product_product_thumb);
        private val productNameItemView: TextView = itemView.findViewById(R.id.cardView_item_product_product_name);
        private val productSizeItemView: TextView = itemView.findViewById(R.id.cardView_item_product_product_size);
        private val productPriceItemView: TextView = itemView.findViewById(R.id.cardView_item_product_product_price);
        private val productDescriptionItemView: TextView = itemView.findViewById(R.id.cardView_item_product_product_description);
        private val productSoldOutItemView: ImageView = itemView.findViewById(R.id.cardView_item_product_is_sold_out);

        @SuppressLint("SetTextI18n")
        fun bind(currentProduct: Product) {
            if(currentProduct.isAvailable) {
                cardView.setOnClickListener { adapterListener.onItemClick(currentProduct); }
            }
            when(currentProduct.productKind) {
                "HOT" -> { productThumbItemView.setImageResource(R.drawable.papercup); }
                "ICE" -> { productThumbItemView.setImageResource(R.drawable.ice_americano); }
                "SMT" -> { productThumbItemView.setImageResource(R.drawable.smoothe); }
                "TEA" -> { productThumbItemView.setImageResource(R.drawable.tea); }
                "JCE" -> { productThumbItemView.setImageResource(R.drawable.juice); }
                "ADE" -> { productThumbItemView.setImageResource(R.drawable.juice); }
                "ETC" -> { productThumbItemView.setImageResource(R.drawable.bread); }
                else -> { productThumbItemView.setImageResource(R.drawable.papercup); }
            }
            productNameItemView.text = currentProduct.productName;
            productSizeItemView.text = currentProduct.productSize;
            productPriceItemView.text = "%,d".format(currentProduct.productPrice) + "원";
            productDescriptionItemView.text = currentProduct.productDescription;
            productSoldOutItemView.visibility = if(currentProduct.isAvailable) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentProductListViewHolder {
        val itemView = inflater.inflate(R.layout.item_product, parent, false);
        return CurrentProductListViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: CurrentProductListViewHolder, position: Int) {
        val currentProduct = items[position];
        holder.bind(currentProduct);
    }

    override fun getItemCount(): Int {
        return items.size;
    }


    interface OnItemClickListener {
        fun onItemClick(product: Product);
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapterListener = listener;
    }
}