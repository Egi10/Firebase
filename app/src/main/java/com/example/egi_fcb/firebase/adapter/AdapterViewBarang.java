package com.example.egi_fcb.firebase.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.egi_fcb.firebase.R;
import com.example.egi_fcb.firebase.activity.EditActivity;
import com.example.egi_fcb.firebase.model.Barang;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by egi_fcb on 1/1/18.
 */

public class AdapterViewBarang extends RecyclerView.Adapter<AdapterViewBarang.RecyclerViewHolder> {
    ArrayList<Barang> daftarbarang;
    Context context;
    FirebaseDataListener listener;

    public AdapterViewBarang(Context ctx, ArrayList<Barang> daftar_barang){
        context = ctx;
        daftarbarang = daftar_barang;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.costum_layout_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Barang barang = daftarbarang.get(position);

        holder.tvNamaBarang.setText(barang.getNama());
        holder.tvMerkBarang.setText(barang.getMerk());
        holder.tvHargaBarang.setText(String.valueOf(barang.getHarga()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Pilih Aksi");
                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View view = layoutInflater.inflate(R.layout.costum_layout_dialog, null);
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();

                TextView tvEdit = (TextView)view.findViewById(R.id.tv_Edit);
                TextView tvDelete = (TextView)view.findViewById(R.id.tv_delete);

                tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        Intent intent = new Intent(context, EditActivity.class);
                        intent.putExtra("key", daftarbarang.get(position).getKey());
                        intent.putExtra("nama", daftarbarang.get(position).getNama());
                        intent.putExtra("merk", daftarbarang.get(position).getMerk());
                        intent.putExtra("harga", daftarbarang.get(position).getHarga());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        v.getContext().startActivity(intent);
                    }
                });

                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(barang.getKey());
                        databaseReference.removeValue();
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return daftarbarang.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvNamaBarang, tvMerkBarang, tvHargaBarang;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardview);
            tvNamaBarang = (TextView)itemView.findViewById(R.id.tv_namabarang);
            tvMerkBarang = (TextView)itemView.findViewById(R.id.tv_merkbarang);
            tvHargaBarang = (TextView)itemView.findViewById(R.id.tv_hargabarang);
        }
    }

    public interface FirebaseDataListener{
        void onDeleteData(Barang barang, int position);
    }
}
