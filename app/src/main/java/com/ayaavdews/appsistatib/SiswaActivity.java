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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.ayaavdews.appsistatib.Adapter.AdapterViewPager;
import com.ayaavdews.appsistatib.Fragment.FragmentPelajar;
import com.ayaavdews.appsistatib.Fragment.FragmentPeraturan;
import com.ayaavdews.appsistatib.Fragment.FragmentProfileSiswa;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class SiswaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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

    private TextView title, siswa;

    // Deklarasi Fragment
    FragmentPelajar fragmentPelajar;
    FragmentProfileSiswa fragmentProfileSiswa;
    FragmentPeraturan fragmentPeraturan;

    MenuItem prevMenuItem;

    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa);

        title = (TextView) findViewById(R.id.tv_title);
        siswa = (TextView) findViewById(R.id.tv_user);

        // Mengganti nama di toolbar dengan nama user (siswa) yang login
        Intent intent = getIntent();
        try {
            JSONObject user = new JSONObject(intent.getStringExtra("stringSiswa"));
            siswa.setText(user.getString("nama"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewPager            = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);

        // Pengaturan di Toolbar
        ImageView imageView = (ImageView) findViewById(R.id.iv_logo);
        dialog = new Dialog(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(SiswaActivity.this, view);
                popupMenu.setOnMenuItemClickListener(SiswaActivity.this);
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

        fragmentProfileSiswa = new FragmentProfileSiswa();
        fragmentPeraturan = new FragmentPeraturan();
        fragmentPelajar   = new FragmentPelajar();

        adapter.addFragment(fragmentProfileSiswa);
        adapter.addFragment(fragmentPeraturan);
        adapter.addFragment(fragmentPelajar);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.logout){
            Intent intent = new Intent(SiswaActivity.this, MainActivity.class);
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
