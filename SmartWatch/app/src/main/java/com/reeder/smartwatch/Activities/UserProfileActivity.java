package com.reeder.smartwatch.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.reeder.smartwatch.R;

public class UserProfileActivity extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int IMAGE_ACTION_CODE = 102;
    private static final int CAMERA_PERMISSON_REQUEST_CODE = 103;
    private static final int IMAGE_REQUEST = 104;
    private ProgressDialog progressDialog;

    private Uri file;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileActivity.this.onBackPressed();
            }
        });
        ImageButton updateProfileImageButton = (ImageButton) findViewById(R.id.updateProfileImageButton);

        registerForContextMenu(updateProfileImageButton);
        updateProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Galeriden yükle", "Fotoğraf çek"};

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setTitle("Profil fotoğrafı güncelle");
                builder.setIcon(R.drawable.ic_camera_alt_blue);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(UserProfileActivity.this, items[item], Toast.LENGTH_SHORT).show();
                        switch (item) {
                            case 0:
                                choosePhoto();
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

                val = true;

                break;
            case 1:

                val = true;
                break;
        }

        return val;
    }
}
