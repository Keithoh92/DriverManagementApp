package com.example.drivermanagement.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drivermanagement.R;
import com.example.drivermanagement.TabsAdaptor1;
import com.google.android.material.tabs.TabLayout;


public class chooserDashFragmnet extends Fragment {

    Activity listener;
//    Button chooseThisDash;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAdaptor1 myTabsAdaptor;

    public chooserDashFragmnet() {
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
        return inflater.inflate(R.layout.fragment_chooser_dash_fragmnet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        chooseThisDash = (Button) view.findViewById(R.id.chooseThisDash);

        viewPager = (ViewPager) view.findViewById(R.id.dash_chooser_tab_pager);
        myTabsAdaptor = new TabsAdaptor1(getChildFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

//        tabLayout = (TabLayout) view.findViewById(R.id.tablayout_dash_chooser);
//        tabLayout.setupWithViewPager(viewPager);


//        chooseThisDash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "This is the "+viewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
//
//                if(viewPager.getCurrentItem() == 1){
//                    FragmentManager fragment = getActivity().getSupportFragmentManager();
//                    Fragment myFrag = fragment.findFragmentById(R.id.manDash1);
//                    fragment.beginTransaction()
//                            .replace(R.id.manDash1, myFrag)
//                            .commit();
//
//                    FragmentManager fragment2 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag2 = fragment2.findFragmentById(R.id.chooserFrag);
//                    fragment.beginTransaction()
//                            .hide(myFrag2)
//                            .commit();
//
//                    FragmentManager fragment3 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag3 = fragment3.findFragmentById(R.id.manDash2);
//                    fragment3.beginTransaction()
//                            .show(myFrag3)
//                            .commit();
//                }
//                if(viewPager.getCurrentItem() == 2){
//
//                    FragmentManager fragment = getActivity().getSupportFragmentManager();
//                    Fragment myFrag = fragment.findFragmentById(R.id.manDash1);
//                    fragment.beginTransaction()
//                            .hide(myFrag)
//                            .commit();
//
//                    FragmentManager fragment1 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag1 = fragment1.findFragmentById(R.id.manDash2);
//                    fragment1.beginTransaction()
//                            .replace(R.id.manDash2, myFrag)
//                            .show(myFrag1)
//                            .commit();
//
//                    FragmentManager fragment2 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag2 = fragment2.findFragmentById(R.id.chooserFrag);
//                    fragment.beginTransaction()
//                            .hide(myFrag2)
//                            .commit();
//
//                    FragmentManager fragment3 = getActivity().getSupportFragmentManager();
//                    Fragment myFrag3 = fragment3.findFragmentById(R.id.manDash2);
//                    fragment3.beginTransaction()
//                            .show(myFrag3)
//                            .commit();
//                }
//            }
//        });
    }
}