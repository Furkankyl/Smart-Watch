package com.reeder.smartwatch.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.reeder.smartwatch.Adapters.FamilyMemberAdapter;
import com.reeder.smartwatch.Model.FamilyMember;
import com.reeder.smartwatch.Model.User;
import com.reeder.smartwatch.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String array_spinner[];
    private List<FamilyMember> familyMemberList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView profileImageView;
    private OnFragmentInteractionListener mListener;

    private TextView textViewUserName;
    private TextView textViewUserAge;
    private TextView textViewGender;
    private TextView textViewHeight;
    private TextView textViewWeight;
    private TextView textViewBio;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = (ImageView) view.findViewById(R.id.profileImage);

        textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewBio = (TextView) view.findViewById(R.id.textViewBio);
        textViewGender =(TextView) view.findViewById(R.id.textViewGender);
        textViewHeight = (TextView) view.findViewById(R.id.textViewHeight);
        textViewUserAge = (TextView) view.findViewById(R.id.textViewUserAge);
        textViewWeight = (TextView) view.findViewById(R.id.textViewWeight);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserData();

        Spinner s = (Spinner) view.findViewById(R.id.spinner);

        familyMemberList = new ArrayList<>();
        familyMemberList.add(new FamilyMember("John Doe","Kalp cerrahı",""));
        familyMemberList.add(new FamilyMember("Ervin Howell","Beyin cerrahı",""));
        familyMemberList.add(new FamilyMember("Clementine Bauch","Genel cerrahi",""));
        FamilyMemberAdapter doctorAdapter = new FamilyMemberAdapter(familyMemberList, getContext());
        s.setAdapter(doctorAdapter);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.haberver);
        Switch haberVer = (Switch)  view.findViewById(R.id.haberverSwitch);
        haberVer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //slideDown(layout);
                    layout.setVisibility(VISIBLE);
                }else{
                    //slideUp(layout);
                    layout.setVisibility(GONE);
                }
            }
        });


        ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.profileEdit);
        final TextView textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), UserProfileActivity.class));


                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(profileImageView, "profileImage")
                        .addSharedElement(textViewUserName, "textViewUserName")
                        .setCustomAnimations(R.anim.fade_in_animation,R.anim.fade_out_animation)
                        .replace(R.id.frameLayout, new UserProfileFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });
        return view;
    }

    private void getUserData() {
        db.collection("Users").document(firebaseUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        user = document.toObject(User.class);
                        setUserInfo();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setUserInfo() {
        textViewUserName.setText(user.getDisplayName());
        textViewWeight.setText(""+user.getWeight());
        textViewUserAge.setText(""+user.getAge());
        textViewHeight.setText(""+user.getHeight());
        textViewGender.setText(user.getGender());
        textViewBio.setText(user.getBio());
    }

    public void slideUp(final View view){

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // slide the view from its current position to below itself
    public void slideDown(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
