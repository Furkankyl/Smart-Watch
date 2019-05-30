package com.reeder.smartwatch.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.reeder.smartwatch.Helpers.HeartBeatAnomally;
import com.reeder.smartwatch.Helpers.HeartBeatView;
import com.reeder.smartwatch.Model.CriticStatus;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.Model.HeartBeat;
import com.reeder.smartwatch.Model.User;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HeartBeatView heartBeatView;
    private TextView haertBeatTextView;
    private TextView textViewWeightHealth;
    private TextView textViewWeightHealthMessage;
    private TextView textViewRisk;
    private ImageView weightImageView;
    private ImageButton imageButtonSendAlert;
    private User user;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    // TODO: Rename and change types of parameters
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int ACTION_REQUEST_ENABLE = 0;
    private BluetoothAdapter bluetooth; //Bluetooth açma, kapama, keşfetme işlemleri için
    private BluetoothSocket bluetoothSocket; //Bluetooth ile bağlanma, veri aktarımı için
    private ArrayAdapter<String> messageBuffer;
    private ReceiveData receiveData;
    private InputStream mInputStream;
    private final int MESSAGE_RECEIVED = 1;
    private Handler mHandler;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    private boolean isConnected = false;
    private Set<BluetoothDevice> listOfDevices;
    private ArrayList<String> devices;
    private ProgressDialog progress;
    private OnFragmentInteractionListener mListener;
    GraphView graph;
    private ProgressBar progressBar;
    private HeartBeatAnomally heartBeatAnomally;

    private LinearLayout horizontalItems;
    private LineGraphSeries<DataPoint> heartBeatSeries;
    private LineGraphSeries<DataPoint> avarageSeries;
    private HashMap<String, Date> dayMap;

    private List<Doctor> doctorList;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserData();
        heartBeatView = (HeartBeatView) view.findViewById(R.id.heartBeatView);
        haertBeatTextView = (TextView) view.findViewById(R.id.bpmTextView);
        textViewRisk = (TextView) view.findViewById(R.id.textViewRisk);
        heartBeatView.setDurationBasedOnBPM(70);
        heartBeatView.toggle();
        textViewWeightHealth = (TextView) view.findViewById(R.id.textViewWeightHealth);
        textViewWeightHealthMessage = (TextView) view.findViewById(R.id.textViewWeightHealthMessage);
        horizontalItems = (LinearLayout) view.findViewById(R.id.casts_container);
        weightImageView = (ImageView) view.findViewById(R.id.weightImageView);
        heartBeatAnomally = new HeartBeatAnomally();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imageButtonSendAlert = (ImageButton )view.findViewById(R.id.imageButtonSendAlert);
        imageButtonSendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAlertSOSPhoneNumber();
                sendAlertDoctor(100,70);
            }
        });
        getDoctors();

        setHorizontalScroll();
        graph = (GraphView) view.findViewById(R.id.graph);
        setGraph(graph);


        initBluetooth();
        return view;
    }

    private void getDoctors() {
        doctorList = new ArrayList<>();
        db.collection("Users").document(firebaseUser.getUid()).collection("Doctors").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                Doctor doctor = snapshot.toObject(Doctor.class);
                                Log.d(TAG, "onComplete: " + doctor);
                                doctorList.add(doctor);
                            }
                            //sendAlertDoctor(50, 40);
                        }
                    }
                });
    }

    private void saveHeartBeat(final HeartBeat heartBeat) {

        /*Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -4);
        heartBeat.setDate(cal.getTime());
*/
        db.collection("Users").document(firebaseUser.getUid())
                .collection("HeartBeats").add(heartBeat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: success: " + heartBeat.toString());
                } else {
                    Log.d(TAG, "onComplete: error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void setGraph(GraphView graph) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        getHeartBeats(cal.getTime());


        graph.setTitle(new Date().toString());
        graph.setTitleColor(getResources().getColor(R.color.colorAccent));
        graph.setTitleTextSize(18);

        //
        graph.setBackgroundColor(Color.argb(20, 21, 97, 243));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorAccent));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorAccent));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorAccent));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorAccent));
        graph.getGridLabelRenderer().reloadStyles();
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Sayı");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Nabız");

        //

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

    }

    private void getHeartBeats(Date startDate) {
        progressBar.setVisibility(View.VISIBLE);
        graph.setVisibility(View.GONE);
        graph.removeAllSeries();
        heartBeatSeries = new LineGraphSeries<>();
        avarageSeries = new LineGraphSeries<>();
        heartBeatSeries.setAnimated(true);
        heartBeatSeries.setDataPointsRadius(10);
        heartBeatSeries.setColor(Color.MAGENTA);


        db.collection("Users").document(firebaseUser.getUid())
                .collection("HeartBeats")
                .whereGreaterThan("date", startDate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 1;
                            int anomallyCount = 0;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                HeartBeat heartBeat = snapshot.toObject(HeartBeat.class);

                                if (!heartBeat.isNormal())
                                    anomallyCount++;
                                heartBeatSeries.appendData(new DataPoint(count, heartBeat.getHeartBeat()), true, 100000);
                                avarageSeries.appendData(new DataPoint(count++, 70), true, 10000);
                            }

                            Toast.makeText(getContext(), "Anormallik durumu=> Toplam nabız:" + count + "  Anormal nabız:" + anomallyCount, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: " + "Anormallik durumu=> Toplam nabız:" + count + "  Anormal nabız:" + anomallyCount);
                            textViewRisk.setText(count + " Nabızda " + anomallyCount + " anormal nabız görüldü!");
                            progressBar.setVisibility(View.GONE);
                            graph.addSeries(heartBeatSeries);
                            graph.addSeries(avarageSeries);
                            graph.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "Hata:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setHorizontalScroll() {
        //create LayoutInflator class
        dayMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        dayMap.put("1 Gün", cal.getTime());

        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -3);
        dayMap.put("3 Gün", cal.getTime());

        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -7);
        dayMap.put("1 Hafta", cal.getTime());

        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -30);
        dayMap.put("1 ay", cal.getTime());

        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(LAYOUT_INFLATER_SERVICE);
        for (String str : dayMap.keySet()) {
            RelativeLayout clickableColumn = (RelativeLayout) inflater.inflate(
                    R.layout.horizontal_scroll_item, null);
            final Button button = (Button) clickableColumn.findViewById(R.id.buttonGraphTime);
            button.setText(str);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getHeartBeats(dayMap.get(button.getText().toString()));
                    graph.setTitle(dayMap.get(button.getText().toString()).toString());
                    Toast.makeText(getActivity(), "Vuuu", Toast.LENGTH_SHORT).show();
                }
            });
            horizontalItems.addView(clickableColumn);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void getUserData() {
        db.collection("Users").document(firebaseUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        user = document.toObject(User.class);
                        setWeightHealtInfo(user != null ? user.getHeight() : 0, user != null ? user.getWeight() : 0);
                        Log.d(TAG, "onComplete: " + user.toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setWeightHealtInfo(int height, int weight) {
        double index = ((double) weight / (double) (height * height)) * 10000;
        Log.d(TAG, "setWeightHealtInfo: " + index);
        if (index < 18) {
            textViewWeightHealth.setText("Zayıfsın");
            textViewWeightHealthMessage.setText("Kilo alman gerekli");
            weightImageView.setImageResource(R.drawable.ic_weak);
        } else if (index >= 18 && index <= 24.9) {
            textViewWeightHealth.setText("Kilon normal");
            textViewWeightHealthMessage.setText("Böyle kalmaya devam et");
            weightImageView.setImageResource(R.drawable.ic_slim);
        } else if (index >= 25 && index <= 29.9) {
            textViewWeightHealth.setText("Fazla kilolusun");
            textViewWeightHealthMessage.setText("Yediklerine dikkat etmelisin");
            weightImageView.setImageResource(R.drawable.ic_boy);
        } else if (index >= 30 && index <= 39.9) {
            textViewWeightHealth.setText("Obez tehlikesi var");
            textViewWeightHealthMessage.setText("Artık yemek yemeyi kes");
            weightImageView.setImageResource(R.drawable.ic_boy);
        }
    }

    public void initBluetooth() {
        devices = new ArrayList<>();
        messageBuffer = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, android.R.id.text1);

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.d("gelen", "HANDLERIN İÇİNDE ");
                if (message.what == MESSAGE_RECEIVED) {
                    byte[] readData = (byte[]) message.obj;
                    int dataLength = message.arg1;
                    String data = new String(readData, 0, dataLength);
                    //data = new String(readData, 0, dataLength);
                    messageBuffer.add(data);
                    String heartPulse, oxygen;
                    Log.d("heartoks", data);
                    try {
                        heartPulse = data.split("\n")[0];
                        int currentPulse = (int) Double.parseDouble(heartPulse);

                        haertBeatTextView.setText("" + currentPulse + " Bpm");
                        heartBeatView.setDurationBasedOnBPM(currentPulse);
                        oxygen = data.split("\n")[1];

                        int currentOxy = (int) Double.parseDouble(oxygen);
                        if (currentPulse != 0 && currentOxy != 0) {
                            HeartBeat heartBeat = new HeartBeat(Double.parseDouble(heartPulse), Double.parseDouble(oxygen), new Date(), false);
                            heartBeat.setNormal(heartBeatAnomally.addHeartBeat(heartBeat));
                            saveHeartBeat(heartBeat);
                            if (heartBeatAnomally.isCriticStatus()) {
                                //sendAlertDoctor(heartBeatAnomally.totalHeartBeatCount(), heartBeatAnomally.totalAnomallyHeartBeatCount());
                                //sendAlertSOSPhoneNumber();
                                CriticStatus criticStatus = new CriticStatus();
                                criticStatus.setDate(new Date());
                                criticStatus.setTotalAnomallyHeartBeat(heartBeatAnomally.totalHeartBeatCount());
                                criticStatus.setTotalHeartBeat(heartBeatAnomally.totalHeartBeatCount());
                                saveCriticStatus(criticStatus);
                            }
                            Log.d("heartoks", "NAB " + heartPulse);
                            Log.d("heartoks", "OKS " + oxygen);
                            Toast.makeText(getActivity(), "Nabız: " + heartPulse, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Oksijen: " + oxygen, Toast.LENGTH_SHORT).show();
                            Log.d("gelen", "handleMessage: " + data);
                            return true;
                        } else return false;
                    } catch (Exception e) {
                        Log.w(TAG, "handleMessage: ", e);
                        return false;
                    }


            } else

            {
                Log.d("hata", "MESAJ RECEIVED OLMADI");
                return false;
            }
        }
    });
    bluetooth =BluetoothAdapter.getDefaultAdapter();
            if(bluetooth ==null)

    {
        Log.d("bluetooth", "Bluetooth Desteklenmiyor");
        Toast.makeText(getActivity(), "Bluetooth desteklenmiyor", Toast.LENGTH_SHORT).show();
    } else

    {
        Log.d("bluetooth", "Bluetooth Destekleniyor");
        if (bluetooth.isEnabled())
            connectHC06();
        else
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
    }

}

    private void saveCriticStatus(CriticStatus criticStatus) {
        db.collection("Users").document(firebaseUser.getUid()).collection("AnormalStatus").add(criticStatus).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + task.getResult().toString());
                } else {
                    Log.w(TAG, "onComplete: ", task.getException());
                }
            }
        });
    }

    private void sendAlertSOSPhoneNumber() {
        /*LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getActivity(), "Konuma izin vermedin!", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
*/
        String smsMessage = "Nabzımda %75'ten fazla anormallik var. Beni burada bulabilirsin: https://www.google.com/maps/@41.364429,36.184894,17z \nSmart-Watch takımı. ";
        sendSms(user.getSosPhoneNumber(), smsMessage);
    }

    private void sendAlertDoctor(int totalHeartBeat, int totalAnomallyHeartBeat) {
        for (Doctor doctor : doctorList) {
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            JSONObject jsonObject = new JSONObject();
            try {


                JSONObject dataExtra = new JSONObject();
                dataExtra.put("title", "Hey " + doctor.getName());
                dataExtra.put("body", "" + firebaseUser.getDisplayName()
                        + " adlı hastanızın " + totalHeartBeat + " adet nabız verisi içinde" + " " + totalAnomallyHeartBeat
                        + " adet anormal nabız değeri tespit edildi! \nHastanız ile iletişim kurmak isteyebilirsiniz.");
                dataExtra.put("imageUrl", "url");

                jsonObject.put("to", doctor.getToken());
                jsonObject.put("content_available", true);
                jsonObject.put("data", dataExtra);


            } catch (Exception e) {
                Log.d(TAG, "sendNotification: " + e);

            }
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("HttpClient", "error: " + error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AIzaSyDU5T0AOx6XA-sbGH-pmFtIzdwPYyjSYPg"); //şeyma firebase fcm mesajlaşmanın keyi
                    //şeyma bu keyi firebaseden proje ayarları>cloud messaging e girerek eski anahtar kısmından alabilirsin
                    return params;
                }
            };
            queue.add(sr);
        }

    }

    private void sendSms(String phoneNumber, String message) {
        Log.d(TAG, "sendSms: " + phoneNumber + " " + message);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d(TAG, "sended: " + phoneNumber + " " + message);

        } catch (Exception ex) {
            Log.w(TAG, "sendSms: ", ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            bluetoothSocket.close();
        } catch (Exception e) {
            Log.d(TAG, "Bluetooth bağlantısı kapatılamadı" + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //request code = 1
        //İzin verirse result -1; izin vermezse 0
        Toast.makeText(getActivity(), "REQUEST = " + requestCode, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "ONACTIVITY RESULT İÇİNDE", Toast.LENGTH_SHORT).show();
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "RESULT_OK", Toast.LENGTH_SHORT).show();
                isConnected = true;
                connectHC06();
                Toast.makeText(getActivity(), "Bluetooth baslatildi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Bluetooth baslatilamadi!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void msg(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showRisk(int risk) {
        textViewRisk.setText("Risk seviyesi:" + risk);
        Log.d("risk", String.valueOf(risk));
    }

    private void connectHC06() {
        listOfDevices = bluetooth.getBondedDevices();

        if (listOfDevices.size() > 0) {
            Toast.makeText(getActivity(), "Keşfedilen cihazlar bulundu", Toast.LENGTH_SHORT).show();
            for (BluetoothDevice device : listOfDevices) {
                devices.add(device.getName() + "**" + device.getAddress());
            }
            Log.d("liste", devices.toString());
            Toast.makeText(getActivity(), devices.toString(), Toast.LENGTH_SHORT).show();
            address = devices.get(1).substring(devices.get(1).length() - 17); //Baslangic indexi
            Log.d("liste", devices.toString());
            Log.d("adres", address);
        } else {
            Toast.makeText(getActivity(), "Keşfedilen cihazlar bulunamadı", Toast.LENGTH_SHORT).show();
        }
        new BTbaglan().execute();
    }

@SuppressLint("StaticFieldLeak")
private class BTbaglan extends AsyncTask<Void, Void, Void> {
    private boolean ConnectSuccess = true;

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(getActivity(), "Baglanıyor...", "Lütfen Bekleyin");
    }

    @Override
    protected Void doInBackground(Void... devices) {
        try {
            if (bluetoothSocket == null || !isConnected) {
                bluetooth = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice cihaz = bluetooth.getRemoteDevice(address);
                bluetoothSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();
            }
        } catch (IOException e) {
            ConnectSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        try {


            if (!ConnectSuccess) {
                Toast.makeText(getActivity(), "Baglantı Hatası, Lütfen Tekrar Deneyin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Baglantı Basarılı", Toast.LENGTH_SHORT).show();
                receiveData = new ReceiveData(bluetoothSocket);
                receiveData.start();
            }
            progress.dismiss();
        } catch (Exception e) {
            Log.w(TAG, "onPostExecute: ", e);
        }
    }
}

    public void sendData(String message) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write(message.getBytes());
            } catch (IOException err) {
                Toast.makeText(getActivity(), "HATA" + err, Toast.LENGTH_SHORT).show();
                Log.d("hata", err.toString());
            }
        }
    }

public class ReceiveData extends Thread {
    InputStream mInputStream = null;
    //Message message;

    ReceiveData(BluetoothSocket socket) {
        Log.d("gelen", "ReceiveData: ");
        try {
            mInputStream = socket.getInputStream();
        } catch (IOException e) {
            Log.d("hata", "InputStreami alma" + e);
            e.printStackTrace();
        }
    }

    public void run() {
        Log.d("gelen", "RUN İÇİNDEEEEEEEEEEE");
        int bytes;
        while (true) {
            try {
                if (mInputStream.available() > 0) {
                    sleep(1000);
                    byte[] mBuffer = new byte[mInputStream.available()];
                    bytes = mInputStream.read(mBuffer);
                    Log.d("okunan", "run: " + bytes);
                    mHandler.obtainMessage(MESSAGE_RECEIVED, bytes, -1, mBuffer).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

}


