package com.reeder.smartwatch.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class FragmentDoctorDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CALL_PERMISSON_REQUEST_CODE = 100;
    private Doctor doctor;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentDoctorDetail() {

    }

    @SuppressLint("ValidFragment")
    public FragmentDoctorDetail(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDoctorDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDoctorDetail newInstance(String param1, String param2) {
        FragmentDoctorDetail fragment = new FragmentDoctorDetail();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_detail, container, false);
        TextView textViewPersonNameTitle = (TextView) view.findViewById(R.id.textViewUserName);
        TextView textViewPersonName = (TextView) view.findViewById(R.id.textViewPersonName);
        TextView textViewPersonExplonation = (TextView) view.findViewById(R.id.textViewPersonExplonation);
        ImageButton buttonBack  =(ImageButton) view.findViewById(R.id.buttonBack);
        ImageView imageViewPerson = (ImageView) view.findViewById(R.id.imageViewPerson);
        Picasso.get().load(doctor.getPhotoUrl()).into(imageViewPerson);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        if(doctor != null){
            textViewPersonNameTitle.setText(doctor.getName());
            textViewPersonName.setText(doctor.getName());
            textViewPersonExplonation.setText(doctor.getBio());
        }

        ImageButton buttonCall = (ImageButton) view.findViewById(R.id.callButton);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    callPhoneNumber(doctor.getPhoneNumber());
                }else{
                    requestPermission();
                }

            }
        });

        ImageButton buttonSms = (ImageButton) view.findViewById(R.id.smsButton);
        buttonSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms("Test Message");
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void sendSms(String message) {
        Uri uri = Uri.parse("smsto:"+ doctor.getPhoneNumber());
        Intent intent = new Intent (Intent.ACTION_SENDTO,uri);
        intent.putExtra("sms_body",message);
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void callPhoneNumber(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
        }else{
            Toast.makeText(getActivity(), "Aranamadı!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission() {
        // result WRITE_EXTERNAL_STORAGE izni var mı? varsa 0 yoksa -1
        int result = ContextCompat.
                checkSelfPermission(getActivity()
                        , Manifest.permission.CALL_PHONE);
        // result1 CALL_PHONE izni var mı? varsa 0 yoksa -1
        //İkisinede izin verilmiş ise true diğer durumlarda false döner
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        //Verilen String[] dizisi içerisindeki izinlere istek atılır
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CALL_PHONE},
                CALL_PERMISSON_REQUEST_CODE);
    }


    //İstek atılır istek onay/red işlemi bittiğinde bu metod çalışır
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode istek atılırken kullanılan kod ile aynıysa
        if (requestCode == CALL_PERMISSON_REQUEST_CODE) {
            if (grantResults.length > 0) { // İzin verilenlerin listesi en az 1 elemanlı ise
                //Record izni verildi mi?
                boolean permissionToCall = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                //External Store izni verildi mi

                if (permissionToCall) {
                    callPhoneNumber("+905373620617");
                    Toast.makeText(getActivity(), "İzinler alındı!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "İzin vermen gerekli!", Toast.LENGTH_SHORT).show();
                }
            }
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
