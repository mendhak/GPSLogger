package com.mendhak.gpslogger.views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mendhak.gpslogger.R;

/**
 * Created by oceanebelle on 03/04/14.
 */
public class GpsSimpleViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Inflates the simple layout

        View mainView = inflater.inflate(R.layout.fragment_simple_view, container, false);

        return mainView;
    }
}
