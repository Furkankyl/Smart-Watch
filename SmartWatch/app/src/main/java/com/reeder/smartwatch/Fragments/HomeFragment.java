package com.reeder.smartwatch.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reeder.smartwatch.Helpers.HeartBeatView;
import com.reeder.smartwatch.Model.User;
import com.reeder.smartwatch.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

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
    private User user;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int ACTION_REQUEST_ENABLE = 0;
    BluetoothAdapter bluetooth; //Bluetooth açma, kapama, keşfetme işlemleri için
    BluetoothSocket bluetoothSocket; //Bluetooth ile bağlanma, veri aktarımı için
    ArrayAdapter<String> messageBuffer;
    ReceiveData receiveData;
    InputStream mInputStream;
    final int MESSAGE_RECEIVED = 1;
    public Handler mHandler;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address;
    public boolean isConnected = false;
    Set<BluetoothDevice> listOfDevices;
    ArrayList<String> devices;
    private ProgressDialog progress;
    private OnFragmentInteractionListener mListener;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserData();

        heartBeatView = (HeartBeatView) view.findViewById(R.id.heartBeatView);
        haertBeatTextView = (TextView) view.findViewById(R.id.bpmTextView);
        heartBeatView.setDurationBasedOnBPM(70);
        heartBeatView.toggle();
        textViewWeightHealth = (TextView) view.findViewById(R.id.textViewWeightHealth);
        textViewWeightHealthMessage = (TextView) view.findViewById(R.id.textViewWeightHealthMessage);


        return view;
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
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        user = document.toObject(User.class);
                        setWeightHealtInfo(user.getHeight(),user.getWeight());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void setWeightHealtInfo(int height,int weight){
        double index = ((double)weight / (double) (height*height))*10000;
        Log.d(TAG, "setWeightHealtInfo: "+index);
        if(index < 18){
            textViewWeightHealth.setText("Zayıfsın");
            textViewWeightHealthMessage.setText("Birşeyler yemen gerekli");
        }else if(index >= 18 && index <= 24.9){
            textViewWeightHealth.setText("Normalsin");
            textViewWeightHealthMessage.setText("Böyle kalmaya devam et");
        }else if(index >= 25 && index <= 29.9){
            textViewWeightHealth.setText("Fazla kilolusun");
            textViewWeightHealthMessage.setText("Yediklerine biraz dikkat et");
        }else if(index >= 30 && index <= 39.9){
            textViewWeightHealth.setText("Obezsin");
            textViewWeightHealthMessage.setText("Artık yemek yemeyi kes");
        }


    }
    public void initBluetooth() {
        devices = new ArrayList<>();
        messageBuffer = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
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
                    try {
                        heartPulse = data.split("\n")[0];
                        heartPulse = heartPulse.substring(0, heartPulse.length() - 1);
                        oxygen = data.split("\n")[1];
                        oxygen = oxygen.substring(0, oxygen.length() - 1);
                        Log.d("heartoks", "NAB " + heartPulse);
                        Log.d("heartoks", "OKS " + oxygen);
                        Toast.makeText(getActivity(), "Nabız: " + heartPulse, Toast.LENGTH_SHORT).show();
                        haertBeatTextView.setText(heartPulse);
                        heartBeatView.setDurationBasedOnBPM(Integer.valueOf(heartPulse));
                        Toast.makeText(getContext(), "Oksijen: " + oxygen, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "SPLIT HATA " + e, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("gelen", "handleMessage: " + data);
                    return true;
                } else {
                    Log.d("hata", "MESAJ RECEIVED OLMADI");
                    return false;
                }
            }
        });
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null) {
            Log.d("bluetooth", "Bluetooth Desteklenmiyor");
            Toast.makeText(getActivity(), "Bluetooth desteklenmiyor", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("bluetooth", "Bluetooth Destekleniyor");
            if (bluetooth.isEnabled())
                connectHC06();
            else
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);

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
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth baslatildi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth baslatilamadi!", Toast.LENGTH_SHORT).show();
            }
        }
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
            address = devices.get(0).substring(devices.get(0).length() - 17); //Baslangic indexi
            Log.d("liste", devices.toString());
            Log.d("adres", address);
        } else {
            Toast.makeText(getActivity(), "Keşfedilen cihazlar bulunamadı", Toast.LENGTH_SHORT).show();
        }
        new BTbaglan().execute();
    }

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
            if (!ConnectSuccess) {
                Toast.makeText(getActivity(), "Baglantı Hatası, Lütfen Tekrar Deneyin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Baglantı Basarılı", Toast.LENGTH_SHORT).show();
                receiveData = new ReceiveData(bluetoothSocket);
                receiveData.start();
            }
            progress.dismiss();
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

        public ReceiveData(BluetoothSocket socket) {
            Log.d("gelen", "ReceiveData: ");
            BluetoothSocket bluetoothSocket = socket;
            try {
                mInputStream = bluetoothSocket.getInputStream();
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
                }
            }
        }
    }

}


