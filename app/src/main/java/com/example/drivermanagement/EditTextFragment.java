//package com.example.drivermanagement;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//
//public class EditTextFragment extends Fragment{
//
//    final static String DATA_RECEIVE = "data_receive";
//    Activity listener;
//    private Button correct, retry;
//    Fragment scannedOrdersFragment, editTextFragment;
//    public ScannedOrdersFragment sof;
//    public EditTextFragment etf;
//    private TextView editText;
//
//
////    FragmentManager fm = getSupportFragmentManager();
////    Fragment myFrag, scannedFrag;
////    public EditTextFragment etf;
//
//    String textRecognitionResultFragment = "";
//    String address = "";
//    StringBuilder bs = new StringBuilder();
//    private FragmentTActivity2 listener2;
//
//
//
//    public EditTextFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if(context instanceof OCRExtractionActivity){
//            this.listener = (OCRExtractionActivity) context;
//        }
//        try{
//            listener2 = (EditTextFragment.FragmentTActivity2) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + "must implement DialogListener");
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Bundle extractedText = this.getArguments();
//        if (extractedText != null) {
//            address = getArguments().getString("textRecognitionResult");
////            StringBuilder bs = new StringBuilder();
//            String[] strArray = address.split(",");
//            for(int i = 0; i < strArray.length; i++) {
//                bs.append(strArray[i]+"\n");
////                editText.setText(extractedText.getString("textRecognitionResult"));
////            editText.setText(address);
//            }
////            String address1 = bs.toString();
//            editText.setText("");
//            editText.setText(bs.toString());
//            editText.setEnabled(false);
//            editText.setFocusable(false);
//
//            Log.d("testing", "Received from OCR Extraction: "+editText.getText());
//        }
//        Log.d("testing", "onStart called in fragment: "+editText.getText());
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        editText.setText(address);
////        editText.setText(textRecognitionResultFragment);
//        Log.d("testing", "onResume called in fragment: "+editText.getText());
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        editText.clearComposingText();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view =  inflater.inflate(R.layout.fragment_edit_text, container, false);
//        editText = (TextView) view.findViewById(R.id.edit_text);
//        sof = new ScannedOrdersFragment();
//        etf = new EditTextFragment();
////        editText.setText(textRecognitionResultFragment);
//        Log.d("testing", "onCreateView called in fragment: "+editText.getText());
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////                editText.clearComposingText();
////                editText.setText(bs);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
////                editText.setText(s);
//            }
//        });
//
//        return view;
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//
//        correct = view.findViewById(R.id.correct_button);
//        retry = view.findViewById(R.id.retry_button);
////        editText = view.findViewById(R.id.edit_text);
//
//        correct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragManager = getActivity().getSupportFragmentManager();
//                editTextFragment = fragManager.findFragmentById(R.id.edit_text_fragment);
////                scannedOrdersFragment = fragManager.findFragmentById(R.id.scanned_orders_frag);
//
////                String addressChosen = editText.getText().toString();
////                Bundle chosenAddress = new Bundle();
//
////                chosenAddress.putString("ChosenAddress", address);
//                Log.d("testing", "Sending data through interface");
//                Log.d("testing", "Sending address to orders scanned fragment" +address);
//
//                listener2.passData(address);
//                editText.setText("");
////                fragManager.beginTransaction()
////                        .replace(R.id.edit_text_fragment, etf)
////                        .commit();
//            }
//        });
//
//            retry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                editText.clearComposingText();
//                FragmentManager fragManager = getActivity().getSupportFragmentManager();
//                editTextFragment = fragManager.findFragmentById(R.id.edit_text_fragment);
//                assert editTextFragment != null;
//                fragManager.beginTransaction()
//                        .replace(R.id.edit_text_fragment, etf)
//                        .commit();
//
//                Intent intent = new Intent(getContext(), OCRExtractionActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                editText.setText("");
//            }
//        });
//
//    }
//
//    public interface FragmentTActivity2{
//        void passData(String chosenAddress);
//    }
//
//
////    @Override
////    public void communicate(String textRecog) {
////        Log.d("testing", "Received from interface: "+textRecog);
////        textRecognitionResultFragment = textRecog;
//////        editText.setText(textRecog);
////    }
//
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//}
//
