package com.example.egi_fcb.firebase.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.egi_fcb.firebase.MainActivity;
import com.example.egi_fcb.firebase.R;
import com.example.egi_fcb.firebase.model.Barang;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateActivity extends AppCompatActivity {

    //Firebase Yang Merefresh Ke Firebase Realtime Database
    DatabaseReference databaseReference;

    //Variabel Field In Layout
    Button btSubmit;
    EditText etNama, etMerk, etHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Create Data");
        setContentView(R.layout.activity_create);

        etNama = (EditText)findViewById(R.id.et_namabarang);
        etMerk = (EditText)findViewById(R.id.et_merkbarang);
        etHarga = (EditText)findViewById(R.id.et_hargabarang);
        btSubmit = (Button)findViewById(R.id.bt_submit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNama.getText().toString().equals("") && etMerk.getText().toString().equals("") && etHarga.getText().toString().equals("")){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bt_submit), "Data Tidak Boleh Kosong", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else {
                    String nama = etNama.getText().toString();
                    String merk = etMerk.getText().toString();
                    int harga = Integer.parseInt(String.valueOf(etHarga.getText()));

                    databaseReference = FirebaseDatabase.getInstance().getReference("barang");

                    String id = databaseReference.push().getKey();

                    Barang barang = new Barang(id,nama, merk, harga);
                    databaseReference.child(id).setValue(barang);

                    etNama.setText("");
                    etMerk.setText("");
                    etHarga.setText("");

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bt_submit), "Data Berhasil Disimpan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    //submitBarang(new Barang(nama, merk, harga));
                }

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etNama.getWindowToken(), 0);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
