package com.ayaavdews.appsistatib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ayaavdews.appsistatib.Adapter.AdapterPelajar;
import com.ayaavdews.appsistatib.Adapter.AdapterSiswaGuru;
import com.ayaavdews.appsistatib.Model.ModelPelajar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KelasGuruActivity extends AppCompatActivity {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private ListView listView;

    private TextView tvKelas;
    private AdapterSiswaGuru adapterSiswaGuru;
    private ArrayList<ModelPelajar> modelPelajar = new ArrayList<ModelPelajar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas_guru);

        String kelas = getIntent().getStringExtra("kelas");
        listView = (ListView) findViewById(R.id.lv_siswaGuru);

        Log.v("Kelas", kelas);
        Log.v("Nama", getIntent().getStringExtra("namaGuru"));
        Log.v("NIP", getIntent().getStringExtra("nipGuru"));

        modelPelajar.clear();
        adapterSiswaGuru = new AdapterSiswaGuru(this, modelPelajar);

        try {
            JSONObject object1 = new JSONObject(kelas);
            JSONArray array = object1.getJSONArray("data");

            ArrayList<ModelPelajar> modelPelajarArrayList = new ArrayList<>();
            for(int i=0; i<array.length(); i++){
                JSONObject object2 = array.getJSONObject(i);
                ModelPelajar modelPelajar = new ModelPelajar();

                modelPelajar.setNis(object2.getString("nis"));
                modelPelajar.setNama(object2.getString("nama"));
                modelPelajar.setKelas(object2.getString("kelas"));
                modelPelajar.setJenis_kelamin(object2.getString("jenis_kelamin"));

                modelPelajarArrayList.add(modelPelajar);
            }

            JSONObject object3 = array.getJSONObject(1);
            String dataKelas = object3.getString("kelas");

            tvKelas = (TextView) findViewById(R.id.tv_kelas);
            tvKelas.setText("( " + dataKelas  + " )");

            adapterSiswaGuru = new AdapterSiswaGuru(KelasGuruActivity.this, modelPelajarArrayList);
            listView.setAdapter(adapterSiswaGuru);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
