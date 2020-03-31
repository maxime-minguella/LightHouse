package me.maxime.lighthouse.ui.map;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;

import me.maxime.lighthouse.DetailPhareActivity;
import me.maxime.lighthouse.MainActivity;
import me.maxime.lighthouse.R;
import me.maxime.lighthouse.ui.lighthouses.phare.LighthouseContent;

public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnInfoWindowClickListener {

    private static final int DUREE_ECLAT = 200;

    private static final LatLng AIXENPROVENCE;
    private static LatLng START_LOCATION;
    private static final String[] REQUIRED_SDK_PERMISSIONS = {"android.permission.ACCESS_FINE_LOCATION"};

    private Circle[] circles = new Circle[LighthouseContent.ITEMS.size()];

    static {
        AIXENPROVENCE = new LatLng(43.5283D, 5.4497D);
    }

    @Override
    public void onActivityCreated(Bundle paramBundle) {
        super.onActivityCreated(paramBundle);
        checkPermissions();
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map)).getMapAsync(this);
        try {
            if (getArguments().getInt("index", 0) != 0) {
                START_LOCATION = new LatLng(
                        Double.valueOf(LighthouseContent.findById(getArguments().getInt("index")).lat),
                        Double.valueOf(LighthouseContent.findById(getArguments().getInt("index")).lon)
                );
            } else {
                START_LOCATION = AIXENPROVENCE;
            }
        } catch (NullPointerException e) {
            START_LOCATION = AIXENPROVENCE;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        return root;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for (LighthouseContent.LighthouseItem item : LighthouseContent.ITEMS) {
            Log.d("MapFragment", item.name + ", " + item.id + ", marker: " + marker.getTitle());
            if (item.name.equals(marker.getTitle())) {
                Intent intent = new Intent(MainActivity.getContext(), DetailPhareActivity.class);
                intent.putExtra("index", Integer.parseInt(item.id));
                startActivity(intent, ActivityOptions.makeCustomAnimation(MainActivity.getContext(), R.anim.fade, R.anim.hold).toBundle());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(START_LOCATION), 2000, null);

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION") != 0
                && ActivityCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_COARSE_LOCATION") != 0)
            return;

        googleMap.setMyLocationEnabled(true);

        int phareID = 0;
        for (LighthouseContent.LighthouseItem lighthouseItem : LighthouseContent.ITEMS) {
            LatLng latLng = new LatLng(lighthouseItem.lat, lighthouseItem.lon);
            Log.d("MapFragment", lighthouseItem.couleur);
            CircleOptions circleOptions = (new CircleOptions()).center(latLng).strokeColor(0).fillColor(Color.parseColor(lighthouseItem.couleur)).radius((lighthouseItem.portee * 1609));
            this.circles[phareID] = googleMap.addCircle(circleOptions);
            googleMap.addMarker((new MarkerOptions()).position(latLng).title(lighthouseItem.name).snippet(lighthouseItem.region).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_phare_map)));
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.getContext(), R.raw.map));
            googleMap.setOnInfoWindowClickListener(this);
            phareID++;
        }

        (new CountDownTimer(500000L, 1000L) {
            int time = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                time++;
                int phareID = 0;
                for (LighthouseContent.LighthouseItem phare : LighthouseContent.ITEMS) {
                    if (time % phare.periode == 0) {
                        circles[phareID].setVisible(true);
                    } else {
                        final int finalPhareID = phareID;
                        (new CountDownTimer((phare.nbEclat * 2 * 200), 200L) {
                            boolean visible = false;

                            @Override
                            public void onFinish() {
                                circles[finalPhareID].setVisible(false);
                            }

                            @Override
                            public void onTick(long param2Long) {
                                visible ^= true;
                                circles[finalPhareID].setVisible(visible);
                            }
                        }).start();
                    }
                    phareID++;
                }

            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.getContext(), "Good Night", Toast.LENGTH_LONG).show();
            }
        }).start();

    }

    protected void checkPermissions() {
        ArrayList arrayList = new ArrayList();
        for (String str : REQUIRED_SDK_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(MainActivity.getContext(), str) != 0)
                arrayList.add(str);
        }
        if (!arrayList.isEmpty()) {
            String[] arrayOfString = (String[]) arrayList.toArray(new String[arrayList.size()]);
            ActivityCompat.requestPermissions(getActivity(), arrayOfString, 1);
        } else {
            int[] arrayOfInt = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(arrayOfInt, 0);
            onRequestPermissionsResult(1, REQUIRED_SDK_PERMISSIONS, arrayOfInt);
        }
    }

    public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {
        if (paramInt == 1)
            for (paramInt = paramArrayOfString.length - 1; paramInt >= 0; paramInt--) {
                if (paramArrayOfInt[paramInt] != 0) {
                    Context context = MainActivity.getContext();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Required permission '");
                    stringBuilder.append(paramArrayOfString[paramInt]);
                    stringBuilder.append("' not granted, exiting");
                    Toast.makeText(context, stringBuilder.toString(), (short) 1).show();
                    break;
                }
            }
    }
}
