package com.example.whatsapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.FragmentStatusBinding;


public class StatusFragment extends Fragment {


    FragmentStatusBinding binding;


    // Required empty public constructor
    public StatusFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

}
