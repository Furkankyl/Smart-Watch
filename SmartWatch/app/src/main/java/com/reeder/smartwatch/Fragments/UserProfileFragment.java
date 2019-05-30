package com.reeder.smartwatch.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reeder.smartwatch.Adapters.GenderAdapter;
import com.reeder.smartwatch.Model.User;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    private EditText editTextBio;
    private Spinner spinnerGender;
    private EditText editTextPhoneNumber;
    private EditText editTextFamilyNumber;
    private ProgressDialog progressDialog;

    private Uri file;
    private ImageView imageView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonBack);
        imageView = (ImageView) view.findViewById(R.id.profileImage);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ImageButton updateProfileImageButton = (ImageButton) view.findViewById(R.id.updateProfileImageButton);

        spinnerGender = (Spinner) view.findViewById(R.id.spinnerGender);
        editTextAge = (EditText) view.findViewById(R.id.editTextAge);
        editTextBio= (EditText) view.findViewById(R.id.editTextBio);
        editTextHeight= (EditText) view.findViewById(R.id.editTextHeight);
        editTextWeight= (EditText) view.findViewById(R.id.editTextWeight);
        editTextPhoneNumber = (EditText) view.findViewById(R.id.editTextPhoneNumber);
        editTextFamilyNumber = (EditText) view.findViewById(R.id.editTextFamilyNumber);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        TextView textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewUserName.setText(firebaseUser.getDisplayName());
        Button editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        genderList = new ArrayList<>();
        genderList.add("Kadın");
        genderList.add("Erkek");
        GenderAdapter adapter = new GenderAdapter(genderList,getActivity());
        spinnerGender.setAdapter(adapter);
        registerForContextMenu(updateProfileImageButton);
        updateProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Galeriden yükle", "Fotoğraf çek"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Profil fotoğrafı güncelle");
                builder.setIcon(R.drawable.ic_camera_alt_blue);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getActivity(), items[item], Toast.LENGTH_SHORT).show();
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
        return view;
    }

    public void updateProfile(){
        HashMap<String,String> profile = new HashMap<>();
        profile.put("displayName", firebaseUser.getDisplayName());
        int age = Integer.valueOf(editTextAge.getText().toString());
        int weight = Integer.valueOf(editTextWeight.getText().toString());
        int height = Integer.valueOf(editTextHeight.getText().toString());
        String bio = editTextBio.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        User user = new User();
        user.setAge(age);
        user.setWeight(weight);
        user.setHeight(height);
        user.setBio(bio);
        user.setPhoneNumber(phoneNumber);
        user.setDisplayName(firebaseUser.getDisplayName());
        user.setGender(genderList.get(spinnerGender.getSelectedItemPosition()));
        user.setSosPhoneNumber(editTextFamilyNumber.getText().toString());
        db.collection("Users").document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("id",firebaseUser.getUid());
                    db.collection("Users").document(firebaseUser.getUid()).update(map);
                    Toast.makeText(getContext(), "Başarılı", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getContext(), "Hata: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return; //Fotoğraf veya Video onaylanır ise RESULT_OK döner

        switch (requestCode) {
            case IMAGE_ACTION_CODE:
                Bundle extras = data.getExtras();
                imageView.setImageBitmap((Bitmap) extras.get("data"));
                Toast.makeText(getActivity(), "Kaydedilemedi!", Toast.LENGTH_SHORT).show();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void takeNewPhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Fotoğraf çekme intenti
        startActivityForResult(takePhotoIntent, IMAGE_ACTION_CODE); //intenti belirlenen kod ile başlat
    }
    public boolean checkPermission() {
        // result WRITE_EXTERNAL_STORAGE izni var mı? varsa 0 yoksa -1
        int result = ContextCompat.
                checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // result1 RECORD_AUDIO izni var mı? varsa 0 yoksa -1
        int result1 = ContextCompat.
                checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA);
        //İkisinede izin verilmiş ise true diğer durumlarda false döner
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        //Verilen String[] dizisi içerisindeki izinlere istek atılır
        ActivityCompat.requestPermissions(getActivity(),
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
                    Toast.makeText(getActivity(), "İzinler alındı!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "İzin vermen gerekli!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
