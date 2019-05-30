package com.reeder.smartwatch.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reeder.smartwatch.Adapters.GenderAdapter;
import com.reeder.smartwatch.Model.User;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateUserInfoActivity extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int IMAGE_ACTION_CODE = 102;
    private static final int CAMERA_PERMISSON_REQUEST_CODE = 103;
    private static final int IMAGE_REQUEST = 104;

    List<String> genderList;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private EditText editTextAge;
    private EditText editTextHeight;
    private EditText editTextWeight;
    private EditText editTextFamilyNumber;
    private EditText editTextBio;
    private Spinner spinnerGender;
    private EditText editTextPhoneNumber;

    private Uri file;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        showInfoDialog();
        imageView = (ImageView) findViewById(R.id.profileImage);

        ImageButton updateProfileImageButton = (ImageButton) findViewById(R.id.updateProfileImageButton);

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextBio = (EditText) findViewById(R.id.editTextBio);
        editTextHeight = (EditText) findViewById(R.id.editTextHeight);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextFamilyNumber = (EditText) findViewById(R.id.editTextFamilyNumber);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Button editProfileButton = (Button) findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        genderList = new ArrayList<>();
        genderList.add("Kadın");
        genderList.add("Erkek");
        GenderAdapter adapter = new GenderAdapter(genderList, UpdateUserInfoActivity.this);
        spinnerGender.setAdapter(adapter);
        registerForContextMenu(updateProfileImageButton);
        updateProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Galeriden yükle", "Fotoğraf çek"};

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserInfoActivity.this);
                builder.setTitle("Profil fotoğrafı güncelle");
                builder.setIcon(R.drawable.ic_camera_alt_blue);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(UpdateUserInfoActivity.this, items[item], Toast.LENGTH_SHORT).show();
                        switch (item) {
                            case 0:
                                choosePhoto();
                                break;
                            case 1:
                                if (!checkPermission()) {//izinler kontrol edilir

                                    requestPermission();//İzin verilmemiş ise izin istenir
                                } else {
                                    takeNewPhoto(); //İzin verilmiş ise fotoğraf çek
                                }
                                break;

                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        updateProfileImageButton.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View
                    view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                if (view.getId() == R.id.updateProfileImageButton) {
                    contextMenu.setHeaderTitle("Profil fotoğrafını güncelle");
                    contextMenu.add(0, 0, 0, "Galeriden yükle");
                    contextMenu.add(0, 1, 0, "Fotoğraf çek");
                }
            }
        });
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserInfoActivity.this);
        builder.setTitle("Dikkat");
        builder.setMessage("Bilgileri eksiksiz doldurmadan uygulamaya giriş yapamazsın! \n " +
                "Acil durum numarası işler ters giderse haber verilecek kişinin numarasıdır");
        builder.setNegativeButton("Anladım", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void updateProfile() {
        int age = Integer.valueOf(editTextAge.getText().toString());
        int weight = Integer.valueOf(editTextWeight.getText().toString());
        int height = Integer.valueOf(editTextHeight.getText().toString());
        String bio = editTextBio.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        final User user = new User();
        user.setAge(age);
        user.setWeight(weight);
        user.setHeight(height);
        user.setBio(bio);
        user.setPhoneNumber(phoneNumber);
        user.setDisplayName(firebaseUser.getDisplayName());
        user.setGender(genderList.get(spinnerGender.getSelectedItemPosition()));
        user.setId(firebaseUser.getUid());
        user.setSosPhoneNumber(editTextFamilyNumber.getText().toString());
        db.collection("Users").document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    HashMap<String,Object> map = new HashMap<>();
                    map.put("id",firebaseUser.getUid());
                    db.collection("Users").document(firebaseUser.getUid()).update(map);

                    Toast.makeText(UpdateUserInfoActivity.this, "Başarılı", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateUserInfoActivity.this, MainActivity.class));
                } else
                    Toast.makeText(UpdateUserInfoActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return; //Fotoğraf veya Video onaylanır ise RESULT_OK döner

        switch (requestCode) {
            case IMAGE_ACTION_CODE:
                Bundle extras = data.getExtras();
                imageView.setImageBitmap((Bitmap) extras.get("data"));
                Toast.makeText(UpdateUserInfoActivity.this, "Kaydedilemedi!", Toast.LENGTH_SHORT).show();
                /*if (createFolder()) {
                    saveImage((Bitmap) extras.get("data"));
                    Picasso.get().load(file).centerCrop().fit().into(imageView);
                } else {

                }*/
                break;
            case IMAGE_REQUEST:

                Log.d("Url", "onActivityResult: " + data.getData().getPath());
                file = data.getData();
                try {
                    Picasso.get().load(data.getData()).centerCrop().fit().into(imageView);
                } catch (Exception e) {
                    Log.w("Picasso", "onActivityResult: ", e);
                }
                break;
        }
    }

    public void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim seçiniz"), IMAGE_REQUEST);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean val = false;
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                choosePhoto();
                val = true;

                break;
            case 1:
                if (!checkPermission()) {//izinler kontrol edilir

                    requestPermission();//İzin verilmemiş ise izin istenir
                } else {
                    takeNewPhoto(); //İzin verilmiş ise fotoğraf çek
                }
                val = true;
                break;
        }

        return val;
    }

    public void takeNewPhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Fotoğraf çekme intenti
        startActivityForResult(takePhotoIntent, IMAGE_ACTION_CODE); //intenti belirlenen kod ile başlat
    }

    public boolean checkPermission() {
        // result WRITE_EXTERNAL_STORAGE izni var mı? varsa 0 yoksa -1
        int result = ContextCompat.
                checkSelfPermission(UpdateUserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // result1 RECORD_AUDIO izni var mı? varsa 0 yoksa -1
        int result1 = ContextCompat.
                checkSelfPermission(UpdateUserInfoActivity.this, Manifest.permission.CAMERA);
        //İkisinede izin verilmiş ise true diğer durumlarda false döner
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        //Verilen String[] dizisi içerisindeki izinlere istek atılır
        ActivityCompat.requestPermissions(UpdateUserInfoActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CAMERA_PERMISSON_REQUEST_CODE);
    }


    //İstek atılır istek onay/red işlemi bittiğinde bu metod çalışır
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode istek atılırken kullanılan kod ile aynıysa
        if (requestCode == CAMERA_PERMISSON_REQUEST_CODE) {
            if (grantResults.length > 0) { // İzin verilenlerin listesi en az 1 elemanlı ise
                //Record izni verildi mi?
                boolean permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                //External Store izni verildi mi
                boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //izinler kontrol edilir
                if (permissionToCamera && permissionToStore) {
                    Toast.makeText(UpdateUserInfoActivity.this, "İzinler alındı!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UpdateUserInfoActivity.this, "İzin vermen gerekli!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
