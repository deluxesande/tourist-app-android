package com.example.tourist_app_android.fragments;

import com.example.tourist_app_android.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.ImageHolder;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

public class HomePage extends Fragment {

    private MapView mapView;
    private FloatingActionButton floatingActionButton;
    private ActivityResultLauncher<String> activityResultLauncher;

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = v ->
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = point -> {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(18.0).build());
        getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        mapView = view.findViewById(R.id.mapView);
        floatingActionButton = view.findViewById(R.id.focusLocation);
        floatingActionButton.hide();

        // Initialize permission launcher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                    }
                });

        // Check location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Load map style
        mapView.getMapboxMap().loadStyle("mapbox://styles/delsean/cmcf3giti005n01sb5unca2g8", this::initLocationComponent);
    }

    private void initLocationComponent(@NonNull Style style) {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(18.0).build());
        LocationComponentPlugin locationComponent = getLocationComponent(mapView);

        locationComponent.setEnabled(true);
        LocationPuck2D puck = new LocationPuck2D();
        puck.setBearingImage(ImageHolder.from(R.drawable.ic_location_marker));
        locationComponent.setLocationPuck(puck);

        locationComponent.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        locationComponent.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        floatingActionButton.setOnClickListener(v -> {
            locationComponent.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            locationComponent.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).addOnMoveListener(onMoveListener);
            floatingActionButton.hide();
        });
    }
}
