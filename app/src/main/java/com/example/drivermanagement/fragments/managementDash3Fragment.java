package com.example.drivermanagement.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.drivermanagement.R;


public class managementDash3Fragment extends Fragment {

    Activity listener;
    Button sendButton;
    String[] locations1, locations2, locations3, status1, status2, status3;
    NumberPicker numPicker1, numPicker2, numPicker3;

    public managementDash3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManagementDashboard) {
            this.listener = (ManagementDashboard) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_management_dash3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locations1 = getResources().getStringArray(R.array.locations_array);
        locations2 = getResources().getStringArray(R.array.locations_array);
        locations3 = getResources().getStringArray(R.array.locations_array);
        status1 = getResources().getStringArray(R.array.status_array);
        status2 = getResources().getStringArray(R.array.status_array);
        status3 = getResources().getStringArray(R.array.status_array);
        sendButton = (Button) view.findViewById(R.id.sendButtonDash3);
        numPicker1 = (NumberPicker) view.findViewById(R.id.num_pick1);
        numPicker2 = (NumberPicker) view.findViewById(R.id.num_pick2);
        numPicker3 = (NumberPicker) view.findViewById(R.id.num_pick3);

        numPicker1.setMinValue(0);
        numPicker1.setMaxValue(5);

        numPicker2.setMinValue(0);
        numPicker2.setMaxValue(5);

        numPicker3.setMinValue(0);
        numPicker3.setMaxValue(5);


        ArrayAdapter adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locations1);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1 = view.findViewById(R.id.dash3_not1);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });

        ArrayAdapter adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locations2);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner2 = view.findViewById(R.id.dash3_not2);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });

        ArrayAdapter adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locations3);
        adapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner3 = view.findViewById(R.id.dash3_not3);
        spinner3.setAdapter(adapter3);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });

        ArrayAdapter adapter4 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status1);
        adapter4.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner4 = view.findViewById(R.id.dash3_status1);
        spinner4.setAdapter(adapter4);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });

        ArrayAdapter adapter5 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status2);
        adapter5.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner5 = view.findViewById(R.id.dash3_status2);
        spinner5.setAdapter(adapter5);

        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });

        ArrayAdapter adapter6 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status3);
        adapter6.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner6 = view.findViewById(R.id.dash3_status3);
        spinner6.setAdapter(adapter6);

        spinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            }

            @Override
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });
    }
}