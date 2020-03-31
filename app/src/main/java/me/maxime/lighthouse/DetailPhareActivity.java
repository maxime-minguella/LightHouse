package me.maxime.lighthouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.maxime.lighthouse.ui.lighthouses.phare.LighthouseContent;
import me.maxime.lighthouse.ui.map.MapFragment;


public class DetailPhareActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        final LighthouseContent.LighthouseItem lighthouseItem = LighthouseContent.findById(getIntent().getIntExtra("index", 0));
        setContentView(R.layout.activity_detail_phare);
        ((TextView) findViewById(R.id.detail_name)).setText(lighthouseItem.name);
        ((TextView) findViewById(R.id.detail_region)).setText(lighthouseItem.region);
        ((TextView) findViewById(R.id.detail_construction)).setText(String.valueOf(lighthouseItem.construction));
        ((TextView) findViewById(R.id.detail_automatisation)).setText(String.valueOf(lighthouseItem.automatisation));
        ((TextView) findViewById(R.id.detail_hauteur)).setText(lighthouseItem.hauteur + "m");
        ((TextView) findViewById(R.id.detail_longitude)).setText(GPSdecimal2degrees(lighthouseItem.lon));
        ((TextView) findViewById(R.id.detail_latitude)).setText(GPSdecimal2degrees(lighthouseItem.lat));
        ((TextView) findViewById(R.id.detail_url)).setText(lighthouseItem.auteur);

        Context context = MainActivity.getContext();
        ((ImageView) findViewById(R.id.imgDetail)).setImageResource(context.getResources().getIdentifier(lighthouseItem.imgFile, "drawable", context.getPackageName()));
        findViewById(R.id.imgDetail).setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.lookup_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra("index", Integer.parseInt(lighthouseItem.id));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        v.animate().alpha(0.5F);
        v.animate().translationY(200.0F);
    }

    private String GPSdecimal2degrees(double decimal) {
        int d = (int) decimal;
        double t1 = ((decimal - d) * 60);
        int m = (int) t1;
        int s = (int) ((t1 - m) * 60);
        return d + "°" + m + "’" + s + "”";
    }

}