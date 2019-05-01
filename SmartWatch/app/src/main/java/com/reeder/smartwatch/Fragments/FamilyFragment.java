package com.reeder.smartwatch.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.reeder.smartwatch.Adapters.DoctorAdapter;
import com.reeder.smartwatch.Adapters.FamilyMemberAdapter;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.Model.FamilyMember;
import com.reeder.smartwatch.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FamilyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FamilyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView listViewFamily;
    private ListView listViewDoctors;
    private List<FamilyMember> familyMemberList;
    private List<Doctor> doctorList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FamilyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FamilyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FamilyFragment newInstance(String param1, String param2) {
        FamilyFragment fragment = new FamilyFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family, container, false);
        listViewDoctors = (ListView) view.findViewById(R.id.listViewDoctors);

        doctorList = new ArrayList<>();

        doctorList.add(new Doctor("John Doe","Kalp cerrahı",""));
        doctorList.add(new Doctor("Ervin Howell","Beyin cerrahı",""));
        doctorList.add(new Doctor("Clementine Bauch","Genel cerrahi",""));
        DoctorAdapter doctorAdapter = new DoctorAdapter(doctorList,getContext());
        listViewDoctors.setAdapter(doctorAdapter);

        listViewFamily = (ListView) view.findViewById(R.id.listViewFamilies);
        familyMemberList = new ArrayList<>();
        familyMemberList.add(new FamilyMember("Patricia Lebsack","Mother",""));
        familyMemberList.add(new FamilyMember("Chelsey Dietrich","Father",""));
        familyMemberList.add(new FamilyMember("Kurtis Weissnat","Sister",""));

        FamilyMemberAdapter familyMemberAdapter = new FamilyMemberAdapter(familyMemberList,getContext());
        listViewFamily.setAdapter(familyMemberAdapter);

        return view;
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
}
