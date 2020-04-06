package com.ayaavdews.appsistatib.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaavdews.appsistatib.Adapter.AdapterPelajar;
import com.ayaavdews.appsistatib.RecyclerView.RecyclerViewAdapter;
import com.ayaavdews.appsistatib.Model.ModelKelas;
import com.ayaavdews.appsistatib.Model.ModelPelajar;
import com.ayaavdews.appsistatib.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentPelajar extends Fragment {

    // Created by :
    // Aya Avdews
    // XII RPL A / 17006892
    // SMKN 2 Surakarta

    private int icon;
    private ListView listView;

    private AdapterPelajar adapterPelajar;
    private ArrayList<ModelPelajar> modelPelajar = new ArrayList<ModelPelajar>();

    List<ModelKelas> kelas;

    // Fragment ini digunakan pada interface Siswa
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pelajar, container, false);

        // Membuat ArrayList Baru
        kelas = new ArrayList<>();

        // Mengambil Array kela dari String (res>values)
        String [] kelasArray= getResources().getStringArray(R.array.kelas);

        for(int i=0; i<kelasArray.length; i++){
            if(kelasArray[i].equals("X RPL A") || kelasArray[i].equals("XI RPL A") || kelasArray[i].equals("XII RPL A") ||
                    kelasArray[i].equals("X RPL B") || kelasArray[i].equals("XI RPL B") || kelasArray[i].equals("XII RPL B")) {
                icon = R.drawable.icon_rpl;
            } else {
                if (kelasArray[i].equals("X TKJ A") || kelasArray[i].equals("XI TKJ A") || kelasArray[i].equals("XII TKJ A") ||
                        kelasArray[i].equals("X TKJ B") || kelasArray[i].equals("XI TKJ B") || kelasArray[i].equals("XII TKJ B") ||
                        kelasArray[i].equals("X TKJ C") || kelasArray[i].equals("XI TKJ C") || kelasArray[i].equals("XII TKJ C")
                ) {
                    icon = R.drawable.icon_tkj;
                } else {
                    icon = R.drawable.icon_tav;
                }
            }
            kelas.add(new ModelKelas(kelasArray[i], icon));
        }

        RecyclerView myrv = (RecyclerView) root.findViewById(R.id.recyclerview_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getActivity(), kelas);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        myrv.setAdapter(myAdapter);

        return root;
    }
}
