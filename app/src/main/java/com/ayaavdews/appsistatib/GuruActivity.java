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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ayaavdews.appsistatib.Adapter.AdapterViewPager;
import com.ayaavdews.appsistatib.Fragment.FragmentPeraturan;
import com.ayaavdews.appsistatib.Fragment.FragmentProfileGuru;
import com.ayaavdews.appsistatib.Fragment.FragmentProfileSiswa;
import com.ayaavdews.appsistatib.Fragment.FragmentSiswa;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GuruActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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
    FragmentSiswa fragmentSiswa;
    FragmentProfileGuru fragmentProfileGuru;
    FragmentPeraturan fragmentPeraturan;

    MenuItem prevMenuItem;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru);

        title = (TextView) findViewById(R.id.tv_title);

        viewPager            = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);

        // Pengaturan di Toolbar
        dialog = new Dialog(this);

        ImageView imageView = (ImageView) findViewById(R.id.iv_logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(GuruActivity.this, view);
                popupMenu.setOnMenuItemClickListener(GuruActivity.this);
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

                    case R.id.navigation_peraturan:
                        viewPager.setCurrentItem(1);
                        break;

                    case R.id.navigation_siswa:
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
                        title.setText(getResources().getString(R.string.title_peraturan));
                    } else {
                        title.setText(getResources().getString(R.string.title_siswa));
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
        fragmentPeraturan   = new FragmentPeraturan();
        fragmentSiswa       = new FragmentSiswa();

        adapter.addFragment(fragmentProfileGuru);
        adapter.addFragment(fragmentPeraturan);
        adapter.addFragment(fragmentSiswa);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.logout){
            Intent intent = new Intent(GuruActivity.this, MainActivity.class);
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
