package com.ayaavdews.appsistatib.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayaavdews.appsistatib.Adapter.AdapterPeraturan;
import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentPeraturan extends Fragment implements SearchView.OnQueryTextListener {

    // Created by :
    // Aya Avdews
    // XII RPL A / 17006892
    // SMKN 2 Surakarta

    private Context mContext;
    private ListView listView;
    private SearchView searchView;
    private AdapterPeraturan adapterPeraturan;
    private ArrayList<ModelPeraturan> peraturanArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_peraturan, container, false);

        searchView = (SearchView) root.findViewById(R.id.searchView);
        listView   = (ListView) root.findViewById(R.id.lv_peraturan);

        String url = getResources().getString(R.string.url_peraturan);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array = response.getJSONArray("data");

                    peraturanArrayList = new ArrayList<ModelPeraturan>();
                    for(int i=0; i<array.length(); i++){
                        JSONObject object2 = array.getJSONObject(i);
                        peraturanArrayList.add(new ModelPeraturan(object2.getString("kode"), object2.getString("jenis_pelanggaran"), object2.getString("skor")));
                    }
                    adapterPeraturan = new AdapterPeraturan(getActivity(), peraturanArrayList);
                    listView.setAdapter(adapterPeraturan);
                    listView.setTextFilterEnabled(false);
                    setupSearchView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        return root;
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        // searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Filter filter = adapterPeraturan.getFilter();
        filter.filter(newText);
//        if (TextUtils.isEmpty(newText)) {
//            listView.clearTextFilter();
//        } else {
//            listView.setFilterText(newText);
//        }
        return true;
    }
}
