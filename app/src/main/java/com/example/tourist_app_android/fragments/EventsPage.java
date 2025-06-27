package com.example.tourist_app_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tourist_app_android.R;

public class EventsPage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedIsanceState) {
        super.onCreate(savedIsanceState);

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        return view;
    }
}
