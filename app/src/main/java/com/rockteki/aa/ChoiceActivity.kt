package com.rockteki.aa

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.rockteki.aa.model.ChoiceRequest
import com.rockteki.aa.model.FireStoreRepository
import com.rockteki.aa.model.Product
import com.rockteki.aa.model.adapters.ProductListAdapter
import java.util.*
import kotlin.collections.HashMap

class ChoiceActivity : FragmentActivity() {

    private val db = FireStoreRepository()
    private lateinit var accountId: String;
    private lateinit var tabLayout: TabLayout
    private var productList : MutableList<Product> = arrayListOf();
    private lateinit var empty: TextView
    private lateinit var choiceRequest: ChoiceRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        App.prefs.fuid?.let { accountId = it }
        intent.extras?.let {
            choiceRequest = it.get("choiceRequest") as ChoiceRequest
        }

        empty = findViewById(R.id.choice_empty);
        tabLayout = findViewById(R.id.choice_tab);
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateProductList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        val title: TextView = findViewById(R.id.choice_title);
            title.text = choiceRequest.storeName;
        val backBtn: ImageView = findViewById(R.id.choice_back);
            backBtn.setOnClickListener { super.onBackPressed(); }

        updateProductList()
    }


    private fun updateProductList() {
        val productKind = when(tabLayout.selectedTabPosition){
            0 -> { "HOT" }
            1 -> { "ICE" }
            2 -> { "SMT" }
            3 -> { "TEA" }
            4 -> { "JCE" }
            5 -> { "ADE" }
            6 -> { "ETC" }
            else -> { "ETC" }
        }
        productList = mutableListOf();
        db.listProductByKind(choiceRequest.storeId, productKind) { productList ->
            render(productList)
        }
    }

    private fun render(productList: List<Product>) {

        val currentProductListAdapter = ProductListAdapter(this, productList);
        val currentProductListRecyclerView: RecyclerView = findViewById(R.id.rv_productlist);
            currentProductListRecyclerView.apply {
                adapter = currentProductListAdapter;
                layoutManager = LinearLayoutManager(this@ChoiceActivity);
                setHasFixedSize(true);
            }

        currentProductListAdapter.setOnItemClickListener(object : ProductListAdapter.OnItemClickListener{
            override fun onItemClick(product: Product) { showChoiceDialog(product) }
        })
        empty.visibility = if(productList.isNotEmpty()) View.INVISIBLE else View.VISIBLE
    }

    private fun showChoiceDialog(product: Product) {
        val builder = AlertDialog.Builder(this@ChoiceActivity);
        val view = layoutInflater.inflate(R.layout.dialog_choice_option, null, false);
            builder.setView(view);
        val dialog = builder.create();
        val dialogTitle: TextView = view.findViewById(R.id.dialog_choice_option_title);
            dialogTitle.text = "${product.productName} - ${product.productSize}"
        val dialogClose: ImageView = view.findViewById(R.id.dialog_choice_option_close);
            dialogClose.setOnClickListener { dialog.dismiss(); }
        val countValue: TextView = view.findViewById(R.id.dialog_choice_option_count_value);
        val countPlus: ImageView = view.findViewById(R.id.dialog_choice_option_count_plus);
            countPlus.setOnClickListener { countValue.text = (countValue.text.toString().toInt() + 1).toString(); }
        val countMinus: ImageView = view.findViewById(R.id.dialog_choice_option_count_minus);
            countMinus.setOnClickListener { if(countValue.text.toString().toInt() > 1) { countValue.text = (countValue.text.toString().toInt() - 1).toString() }; }
        val optionMemo: EditText = view.findViewById(R.id.dialog_choice_option_memo);
        val dialogSubmitBtn: Button = view.findViewById(R.id.dialog_choice_option_submit);
            dialogSubmitBtn.setOnClickListener {
                val now = System.currentTimeMillis();
                val choiceId = "c_$now";
                val newChoice: HashMap<String, Any?> = hashMapOf(
                    "choiceId" to choiceId,
                    "choiceAccountId" to accountId,
                    "choiceQuantity" to countValue.text.toString().toInt(),
                    "choiceProductId" to product.productId,
                    "choiceProductName" to product.productName,
                    "choiceProductSize" to product.productSize,
                    "choiceProductPrice" to product.productPrice,
                    "choiceProductThumb" to product.productKind,
                    "choiceMemo" to optionMemo.text.toString()
                )
                sendChoiceToStore(newChoice)
        }
        val dialogDismissBtn: Button = view.findViewById(R.id.dialog_choice_option_dismiss);
            dialogDismissBtn.setOnClickListener { dialog.dismiss(); }

        dialog.show();
    }

    private fun sendChoiceToStore(newChoice: HashMap<String, Any?>) {
        db.setChoice(choiceRequest.storeId, choiceRequest.storeOrderId, newChoice["choiceId"].toString(), newChoice).addOnSuccessListener {
            finish()
        }
    }
}