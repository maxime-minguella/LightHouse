package me.maxime.lighthouse;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import android.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import me.maxime.lighthouse.ui.lighthouses.LighthouseFragment;
import me.maxime.lighthouse.ui.lighthouses.phare.LighthouseContent;

public class MainActivity extends BaseActivity implements LighthouseFragment.OnListFragmentInteractionListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static Context context;
    private DrawerLayout drawer;
    private NavController navController;

    private static final int REQUEST_CODE_MAP = 0x9988;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        LighthouseContent.loadAsyncPhareAllJson();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_lighthouses, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MAP && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra("index")) {
                Log.d("MainActivity", "phare:" + data.getIntExtra("index", 0));
                Bundle bd = new Bundle();
                bd.putInt("index", data.getIntExtra("index", 0));
                navController.navigate(R.id.nav_map, bd);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.local_french:
                setNewLocale(this, LocalManager.FRENCH);
                return true;
            case R.id.local_english:
                setNewLocale(this, LocalManager.ENGLISH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNewLocale(AppCompatActivity mContext, @LocalManager.LocaleDef String language) {
        LocalManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static Context getContext() {
        return context;
    }

    public void onListFragmentInteraction(LighthouseContent.LighthouseItem lighthouseItem) {
        Intent intent = new Intent(this, DetailPhareActivity.class);
        intent.putExtra("index", Integer.parseInt(lighthouseItem.id));
        startActivityForResult(intent, REQUEST_CODE_MAP, ActivityOptions.makeCustomAnimation(this, R.anim.fade, R.anim.hold).toBundle());
    }

}
