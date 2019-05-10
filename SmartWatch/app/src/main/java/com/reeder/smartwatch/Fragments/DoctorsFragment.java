package com.reeder.smartwatch.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.reeder.smartwatch.Adapters.AutoCompleteTextViewAdapter;
import com.reeder.smartwatch.Adapters.DoctorAdapter;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoctorsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DoctorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView listViewDoctors;

    private List<Doctor> doctorList;
    private List<Doctor> allDoctorList;
    DoctorAdapter doctorAdapter;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private Dialog addDoctorDialog;
    private ProgressBar progressBar;
    private Doctor autoCompleteSelectedDoctor;
    private static final String TAG = "DoctorsFragmetn";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public DoctorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorsFragment newInstance(String param1, String param2) {
        DoctorsFragment fragment = new DoctorsFragment();
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
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctors, container, false);

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addDoctorButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDoctorDialog();
            }
        });

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        allDoctorList = new ArrayList<>();
        getAllDoctors();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        listViewDoctors = (ListView) view.findViewById(R.id.listViewMember);
        doctorList = new ArrayList<>();
        getUsersDoctors();

        listViewDoctors.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                if (view.getId() == R.id.listViewMember) {
                    AdapterView.AdapterContextMenuInfo info =
                            (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                    contextMenu.setHeaderTitle("Doktor işlemleri");
                    contextMenu.add(0, 0, 0, "Sil");
                }
            }
        });
        listViewDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
                ImageView profileImageView = (ImageView) view.findViewById(R.id.imageViewPhoto);
                TextView textViewUserName = (TextView) view.findViewById(R.id.textViewTitle);
                Toast.makeText(getActivity(), "Seçilen: " + doctorList.get(i).getName(), Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(profileImageView, "imageViewPhoto")
                        .addSharedElement(textViewUserName, "textViewTitle")
                        .setCustomAnimations(R.anim.fade_in_animation, R.anim.fade_out_animation)
                        .replace(R.id.frameLayout, new FragmentDoctorDetail(doctorList.get(i)))
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean val = false;
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == 0) {
            Toast.makeText(getContext(), "Düzenle", Toast.LENGTH_SHORT).show();
            deleteDoctor(doctorList.get(info.position));
            val = true;
        }
        return val;
    }

    private void deleteDoctor(final Doctor doctor) {
        db.collection("Users")
                .document(firebaseUser.getUid())
                .collection("Doctors")
                .document(doctor.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Silindi", Toast.LENGTH_SHORT).show();
                            doctorList.remove(doctor);
                            doctorAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Hata" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUsersDoctors() {
        if(doctorList.isEmpty())
            progressBar.setVisibility(View.VISIBLE);
        db.collection("Users")
                .document(firebaseUser.getUid())
                .collection("Doctors")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                doctorList.clear();
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments() != null && !task.getResult().getDocuments().isEmpty()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Doctor doctor = documentSnapshot.toObject(Doctor.class);
                            doctorList.add(doctor);
                        }


                    } else {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Doktorlarım");
                        alertDialog.setMessage("Henüz doktor eklememişsin");
                        alertDialog.setPositiveButton("Doktor ekle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showAddDoctorDialog();
                            }
                        });
                        alertDialog.setNegativeButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        alertDialog.show();
                    }
                    doctorAdapter = new DoctorAdapter(doctorList, getActivity(), getActivity().getSupportFragmentManager());
                    listViewDoctors.setAdapter(doctorAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Doktorlar yüklenemedi hata:" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllDoctors() {
        db.collection("Doctors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getDocuments();
                    if (!task.getResult().getDocuments().isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Doctor doctor = documentSnapshot.toObject(Doctor.class);
                            allDoctorList.add(doctor);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Doktorlar yüklenemedi hata:" + task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showAddDoctorDialog() {
        addDoctorDialog = new Dialog(getContext());
        addDoctorDialog.setContentView(R.layout.add_family_member);
        addDoctorDialog.setTitle("Doktor ekle");

        AutoCompleteTextViewAdapter adapter = new AutoCompleteTextViewAdapter(getContext(), allDoctorList);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) addDoctorDialog.findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);
        Button buttonAdd = (Button) addDoctorDialog.findViewById(R.id.addButton);
        Button buttonCancel = (Button) addDoctorDialog.findViewById(R.id.cancelButton);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                autoCompleteSelectedDoctor = allDoctorList.get(i);
                Log.d(TAG, "onItemSelected: " + autoCompleteSelectedDoctor);
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteSelectedDoctor != null) {
                    addDoctorCurrentUser();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDoctorDialog.dismiss();
                Toast.makeText(getActivity(), "Vazgeçtin", Toast.LENGTH_SHORT).show();
            }
        });
        addDoctorDialog.show();
        Window window = addDoctorDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void addDoctorCurrentUser() {

        db.collection("Users")
                .document(firebaseUser.getUid())
                .collection("Doctors")
                .document(autoCompleteSelectedDoctor.getId())
                .set(autoCompleteSelectedDoctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Başarıyla eklendi", Toast.LENGTH_SHORT).show();
                            getUsersDoctors();
                            addDoctorDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Hata:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

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
        Log.d(TAG, "onAttach: ");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
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
}
