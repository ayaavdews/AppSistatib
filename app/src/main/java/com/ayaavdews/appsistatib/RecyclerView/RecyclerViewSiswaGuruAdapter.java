package com.ayaavdews.appsistatib.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaavdews.appsistatib.KelasGuruActivity;
import com.ayaavdews.appsistatib.Model.ModelKelas;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RecyclerViewSiswaGuruAdapter extends RecyclerView.Adapter<RecyclerViewSiswaGuruAdapter.MyViewHolder> {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Context mContext;
    private List<ModelKelas> mData;
    private String namaGuru, nipGuru;

    public RecyclerViewSiswaGuruAdapter(Context mContext, List<ModelKelas> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.raw_result_kelas, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.class_name.setText(mData.get(position).getKelas());
        holder.class_img.setImageResource(mData.get(position).getThumbnail());

        // Set click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mContext.getResources().getString(R.string.url_kelas);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", mContext.getResources().getString(R.string.API_KEY));
                params.put("kelas", mData.get(position).getKelas());

                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        Intent i = ((Activity)mContext).getIntent();
                        final String dataUser = i.getStringExtra("stringGuru");

                        try {
                            JSONObject user = new JSONObject(dataUser);
                            namaGuru = (user.getString("nama"));
                            nipGuru  = (user.getString("nip"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(mContext, KelasGuruActivity.class);
                        intent.putExtra("kelas",response.toString());
                        intent.putExtra("namaGuru",namaGuru);
                        intent.putExtra("nipGuru",nipGuru);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.empty_class), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView class_name;
        ImageView class_img;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView   = (CardView) itemView.findViewById(R.id.cardview_id);
            class_name = (TextView) itemView.findViewById(R.id.class_name);
            class_img  = (ImageView) itemView.findViewById(R.id.class_img);
        }
    }
}
