package me.maxime.lighthouse.ui.about;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import me.maxime.lighthouse.R;

public class AboutFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        VideoView videoView = root.findViewById(R.id.video_view);
        String url = "android.resource://" + getContext().getPackageName() + "/raw/" + R.raw.phares_liroise;
        Log.d("AboutFragment", url);
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();
        return root;
    }
}
