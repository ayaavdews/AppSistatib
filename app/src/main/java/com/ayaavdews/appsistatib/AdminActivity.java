package com.ayaavdews.appsistatib;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ayaavdews.appsistatib.Adapter.AdapterViewPager;
import com.ayaavdews.appsistatib.Fragment.FragmentDashboard;
import com.ayaavdews.appsistatib.Fragment.FragmentProfileGuru;
import com.ayaavdews.appsistatib.Fragment.FragmentProfileSiswa;
import com.ayaavdews.appsistatib.Fragment.FragmentTatib;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AdminActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    // Deklarasi BottomNavigation (Fungsi Click)
    BottomNavigationView bottomNavigationView;

    // Deklarasi ViewPager (Fungsi Swipe)
    private ViewPager viewPager;

    private TextView title;

    // Deklarasi Fragment
    FragmentDashboard fragmentDashboard;
    FragmentProfileGuru fragmentProfileGuru;
    FragmentTatib fragmentTatib;

    MenuItem prevMenuItem;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        title = (TextView) findViewById(R.id.tv_title);

        viewPager            = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);

        // Pengaturan di Toolbar
        dialog = new Dialog(this);

        ImageView imageView = (ImageView) findViewById(R.id.iv_logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(AdminActivity.this, view);
                popupMenu.setOnMenuItemClickListener(AdminActivity.this);
                popupMenu.inflate(R.menu.menu);
                popupMenu.show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(0);
                        break;

                    case R.id.navigation_dashboard:
                        viewPager.setCurrentItem(1);
                        break;

                    case R.id.navigation_tatib:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
            }

            @Override
            public void onPageSelected(int position) {


                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.v("Page", "onPageSelected : " + position);

                if(position == 0) {
                    title.setText(getResources().getString(R.string.title_profile));
                } else {
                    if(position == 1){
                        title.setText(getResources().getString(R.string.title_dashboard));
                    } else {
                        title.setText(getResources().getString(R.string.title_tatib));
                    }
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager();
    }

    private void setupViewPager() {
        AdapterViewPager adapter = new AdapterViewPager(getSupportFragmentManager());

        fragmentProfileGuru = new FragmentProfileGuru();
        fragmentDashboard   = new FragmentDashboard();
        fragmentTatib       = new FragmentTatib();

        adapter.addFragment(fragmentProfileGuru);
        adapter.addFragment(fragmentDashboard);
        adapter.addFragment(fragmentTatib);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.logout){
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if(menuItem.getItemId() == R.id.about) {
            dialog.setContentView(R.layout.popup_about_me);
            dialog.show();
        }
        return true;
    }
}
