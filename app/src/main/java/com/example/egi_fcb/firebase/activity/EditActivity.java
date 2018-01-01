package com.example.egi_fcb.firebase.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.egi_fcb.firebase.MainActivity;
import com.example.egi_fcb.firebase.R;
import com.example.egi_fcb.firebase.model.Barang;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditActivity extends AppCompatActivity {
    //Firebase Yang Merefresh Ke Firebase Realtime Database
    DatabaseReference databaseReference;

    //Variabel Field In Layout
    Button btSubmit;
    EditText etNama, etMerk, etHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Update Data");
        setContentView(R.layout.activity_edit);

        etNama = (EditText)findViewById(R.id.et_namabarang);
        etMerk = (EditText)findViewById(R.id.et_merkbarang);
        etHarga = (EditText)findViewById(R.id.et_hargabarang);
        btSubmit = (Button)findViewById(R.id.bt_submit);

        final Intent intent = getIntent();
        final String key = intent.getStringExtra("key");
        final String nama = intent.getStringExtra("nama");
        String merk = intent.getStringExtra("merk");
        int harga = intent.getIntExtra("harga", 0);

        etNama.setText(nama);
        etMerk.setText(merk);
        etHarga.setText(String.valueOf(harga));

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etnama = etNama.getText().toString();
                String etmerk = etMerk.getText().toString();
                int harga = Integer.parseInt(String.valueOf(etHarga.getText()));

                updateBarang(key, etnama, etmerk, harga);

                Intent intent1 = new Intent(getApplicationContext(), ViewActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
            }
        });
    }

    private boolean updateBarang(String id, String nama, String merk, int harga){
        databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(id);

        Barang barang = new Barang(id, nama, merk, harga);
        databaseReference.setValue(barang);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.bt_submit), "Data Berhasil Diupdate", Snackbar.LENGTH_LONG);
        snackbar.show();

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent myIntent = new Intent(getApplicationContext(), ViewActivity.class);
            startActivity(myIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
