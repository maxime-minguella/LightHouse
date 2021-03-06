package me.maxime.lighthouse.ui.map;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

import me.maxime.lighthouse.DetailPhareActivity;
import me.maxime.lighthouse.MainActivity;
import me.maxime.lighthouse.R;
import me.maxime.lighthouse.ui.lighthouses.phare.LighthouseContent;

public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnInfoWindowClickListener {


    private static final int DUREE_ECLAT = 200;
    private static final boolean SMOOTH_ECLAT = true;
    private static boolean IS_LIGHTHOUSES_ALIVE = true;

    private static final LatLng AIXENPROVENCE;
    private static LatLng START_LOCATION;
    private static final String[] REQUIRED_SDK_PERMISSIONS = {"android.permission.ACCESS_FINE_LOCATION"};

    private Circle[] circles = new Circle[LighthouseContent.ITEMS.size()];
    private GroundOverlay[] circles2 = new GroundOverlay[LighthouseContent.ITEMS.size()];

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

        final FloatingActionButton fab = getView().findViewById(R.id.power_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IS_LIGHTHOUSES_ALIVE ^= true;
                Snackbar.make(view, getResources().getString(R.string.map_power) + " " +
                        (
                                IS_LIGHTHOUSES_ALIVE
                                        ? getResources().getString(R.string.map_power_on)
                                        : getResources().getString(R.string.map_power_off)
                        ), Snackbar.LENGTH_SHORT).show();
                fab.setColorFilter(ContextCompat.getColor(getView().getContext(),
                        (IS_LIGHTHOUSES_ALIVE ? R.color.power_buttton_on : R.color.power_buttton_off)),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                if (SMOOTH_ECLAT)
                    for (GroundOverlay c : circles2)
                        c.setVisible(IS_LIGHTHOUSES_ALIVE);
                else
                    for (Circle c : circles)
                        c.setVisible(IS_LIGHTHOUSES_ALIVE);
            }
        });
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

            if (SMOOTH_ECLAT) {
                // The drawable to use for the circle
                GradientDrawable d = new GradientDrawable();
                d.setShape(GradientDrawable.OVAL);
                d.setSize(500, 500);
                d.setColor(Color.parseColor(lighthouseItem.couleur));
                d.setStroke(5, Color.TRANSPARENT);

                Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                        , d.getIntrinsicHeight()
                        , Bitmap.Config.ARGB_8888);

                // Convert the drawable to bitmap
                Canvas canvas = new Canvas(bitmap);
                d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                d.draw(canvas);

                // Radius of the circle
                final int radius = lighthouseItem.portee * 1609;

                // Add the circle to the map
                final GroundOverlay circle = googleMap.addGroundOverlay(new GroundOverlayOptions()
                        .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));
                this.circles2[phareID] = circle;
                if (!IS_LIGHTHOUSES_ALIVE) {
                    circle.setVisible(false);
                }

                ValueAnimator valueAnimator = new ValueAnimator();
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
                valueAnimator.setIntValues(0, radius);
                valueAnimator.setDuration(lighthouseItem.periode * 2 * DUREE_ECLAT);
                valueAnimator.setEvaluator(new IntEvaluator());
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (IS_LIGHTHOUSES_ALIVE)
                            circle.setDimensions(valueAnimator.getAnimatedFraction() * radius * 2);
                    }
                });

                valueAnimator.start();
            } else {
                CircleOptions circleOptions = (new CircleOptions()).center(latLng).strokeColor(0).fillColor(Color.parseColor(lighthouseItem.couleur)).radius((lighthouseItem.portee * 1609));
                this.circles[phareID] = googleMap.addCircle(circleOptions);
//                final int radius = lighthouseItem.portee * 1609;
//                ValueAnimator valueAnimator = new ValueAnimator();
//                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
//                valueAnimator.setIntValues(0, radius);
//                valueAnimator.setDuration(lighthouseItem.nbEclat * 2 * DUREE_ECLAT);
//                valueAnimator.setEvaluator(new IntEvaluator());
//                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//                final int finalPhareID = phareID;
//                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                        float animatedFraction = valueAnimator.getAnimatedFraction();
//                        circles[finalPhareID].setRadius(animatedFraction * radius * 2);
//                    }
//                });
//
//                valueAnimator.start();
            }
            googleMap.addMarker((new MarkerOptions()).position(latLng).title(lighthouseItem.name).snippet(lighthouseItem.region).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_phare_map)));
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.getContext(), R.raw.map));
            googleMap.setOnInfoWindowClickListener(this);
            phareID++;
        }

        if (!SMOOTH_ECLAT)
            (new CountDownTimer(500000L, 1000L) {
                int time = 0;

                @Override
                public void onTick(long millisUntilFinished) {
                    time++;
                    int phareID = 0;
                    for (LighthouseContent.LighthouseItem phare : LighthouseContent.ITEMS) {
                        if (IS_LIGHTHOUSES_ALIVE && time % phare.periode == 0) {
                            circles[phareID].setVisible(true);
                        } else {
                            circles[phareID].setVisible(false);
//                            final int finalPhareID = phareID;
//                            (new CountDownTimer((phare.nbEclat * 2 * DUREE_ECLAT), DUREE_ECLAT) {
//                                boolean visible = false;
//
//                                @Override
//                                public void onFinish() {
//                                    circles[finalPhareID].setVisible(false);
//                                }
//
//                                @Override
//                                public void onTick(long param2Long) {
//                                    if (IS_LIGHTHOUSES_ALIVE) {
//                                        visible ^= true;
//                                        circles[finalPhareID].setVisible(visible);
//                                    }
//                                }
//                            }).start();
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
