package com.reeder.smartwatch.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reeder.smartwatch.Adapters.CustomArrayAdapter;
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

        View view=inflater.inflate(R.layout.fragment_doctors, container,false);

        listViewDoctors = (ListView) view.findViewById(R.id.listViewMember);
        Log.d(TAG, "onCreateView: DoctorsFragment");
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("John Doe","Kalp cerrahı","s"));
        doctorList.add(new Doctor("Ervin Howell","Beyin cerrahı","s"));
        doctorList.add(new Doctor("Clementine Bauch","Genel cerrahi","s"));
        DoctorAdapter doctorAdapter = new DoctorAdapter(doctorList,getActivity(),getActivity().getSupportFragmentManager());
        String[] ulkeler =
                {"Türkiye", "Almanya", "Avusturya", "Amerika","İngiltere",
                        "Macaristan", "Yunanistan", "Rusya", "Suriye", "İran", "Irak",
                        "Şili", "Brezilya", "Japonya", "Portekiz", "İspanya",
                        "Makedonya", "Ukrayna", "İsviçre"};
        //ArrayAdapter<String> doctorAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_2,android.R.id.text1,ulkeler);
        //CustomArrayAdapter doctorAdapter = new CustomArrayAdapter(doctorList,getActivity());
        listViewDoctors.setAdapter(doctorAdapter);

        listViewDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
                ImageView profileImageView = (ImageView) view.findViewById(R.id.imageViewPhoto);
                TextView textViewUserName = (TextView) view.findViewById(R.id.textViewTitle);
                Toast.makeText(getActivity(), "Seçilen: "+doctorList.get(i).getName(), Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(profileImageView, "imageViewPhoto")
                        .addSharedElement(textViewUserName, "textViewTitle")
                        .setCustomAnimations(R.anim.fade_in_animation,R.anim.fade_out_animation)
                        .replace(R.id.frameLayout, new FragmentDoctorDetail())
                        .addToBackStack(null)
                        .commit();

            }
        });

        return view;
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
