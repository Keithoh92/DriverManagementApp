package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditTextFragment extends Fragment {

    Activity listener;
    private Button correct, retry;
    private EditTextFragment editTextFragment;
    EditText editText;
    String address = "";



    public EditTextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OCRExtractionActivity){
            this.listener = (OCRExtractionActivity) context;
        }
//        FragmentManager fragmentManager = getChildFragmentManager();
//        Activity act = getActivity();
//        Fragment myFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        editText.setText(address);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        correct = view.findViewById(R.id.correct_button);
        retry = view.findViewById(R.id.retry_button);
        editText = view.findViewById(R.id.edit_text);

        Bundle extractedText = this.getArguments();
        if (extractedText != null) {
            address = getArguments().getString("textRecognitionResult");
            Log.d("testing", "Received from OCR Extraction: "+address);
//            editText.setText(address);
        }

            retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearComposingText();
                Intent intent = new Intent(getContext(), OCRExtractionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}