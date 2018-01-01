package com.example.egi_fcb.firebase.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.egi_fcb.firebase.R;
import com.example.egi_fcb.firebase.model.Barang;
import com.example.egi_fcb.firebase.model.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {
    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //Firebase Yang Merefresh Ke Firebase Realtime Database
    StorageReference storageReference;
    DatabaseReference databaseReference;

    //Uri File
    private Uri filePath;

    //Variabel Field In Layout
    Button btSubmit;
    EditText etNama, etMerk, etHarga;
    CircleImageView imageView;

    String key, gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Update Data");
        setContentView(R.layout.activity_edit);

        etNama = (EditText)findViewById(R.id.et_namabarang);
        etMerk = (EditText)findViewById(R.id.et_merkbarang);
        etHarga = (EditText)findViewById(R.id.et_hargabarang);
        imageView = (CircleImageView)findViewById(R.id.image);
        btSubmit = (Button)findViewById(R.id.bt_submit);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        final Intent intent = getIntent();
        key = intent.getStringExtra("key");
        final String nama = intent.getStringExtra("nama");
        String merk = intent.getStringExtra("merk");
        gambar = intent.getStringExtra("gambar");
        int harga = intent.getIntExtra("harga", 0);

        etNama.setText(nama);
        etMerk.setText(merk);
        Glide.with(getApplicationContext()).load(gambar).into(imageView);
        etHarga.setText(String.valueOf(harga));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private boolean updateBarang(String id, String nama, String merk, int harga, String gambar){
        databaseReference = FirebaseDatabase.getInstance().getReference("barang").child(id);

        Barang barang = new Barang(id, nama, merk, harga, gambar);
        databaseReference.setValue(barang);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.bt_submit), "Data Berhasil Diupdate", Snackbar.LENGTH_LONG);
        snackbar.show();

        return true;
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void uploadFile(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading To Server..");
        progressDialog.show();

        if (filePath != null){
            StorageReference storage = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));
            storage.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String etnama = etNama.getText().toString();
                            String etmerk = etMerk.getText().toString();
                            int harga = Integer.parseInt(String.valueOf(etHarga.getText()));

                            updateBarang(key, etnama, etmerk, harga, taskSnapshot.getDownloadUrl().toString());

                            progressDialog.dismiss();

                            Intent intent1 = new Intent(getApplicationContext(), ViewActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Untuk Melihat Berapa Persen Di Upload
                            //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }else {
            String etnama = etNama.getText().toString();
            String etmerk = etMerk.getText().toString();
            int harga = Integer.parseInt(String.valueOf(etHarga.getText()));

            updateBarang(key, etnama, etmerk, harga, gambar);

            progressDialog.dismiss();

            Intent intent1 = new Intent(getApplicationContext(), ViewActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent1);
        }
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
