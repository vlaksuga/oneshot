package com.rockteki.aa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rockteki.aa.mainFragment.ConfigFragment
import com.rockteki.aa.mainFragment.GroupFragment
import com.rockteki.aa.mainFragment.HomeFragment
import com.rockteki.aa.viewModel.ChoiceRequestViewModel

class MainActivity : BaseActivity() {

    private val fm = supportFragmentManager;
    private val container = R.id.main_fragmentContainerView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val fab: FloatingActionButton = findViewById(R.id.bab_fab);
            fab.setOnClickListener { startActivity(Intent(this, AddChoiceRequestActivity::class.java)); }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavView);
            bottomNav.setOnItemSelectedListener { menuItem ->
            val ft = fm.beginTransaction();
            when (menuItem.itemId) {
                R.id.menu_home -> {ft.replace(container, HomeFragment());}
                R.id.menu_group -> {ft.replace(container, GroupFragment());}
                R.id.menu_more -> {ft.replace(container, ConfigFragment());}
            }
            ft.commit();
            true;
        }
        fm.beginTransaction().add(container, HomeFragment()).commit();
    }
}