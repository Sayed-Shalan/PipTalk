package com.pip.talk.pip.pc.piptalk.search;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pip.talk.pip.pc.piptalk.R;


public class SearchFragment extends Fragment {

    private Typeface ralewayMessage;
    private TextView message;
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        message = (TextView) rootView.findViewById(R.id.findPeople);
        message.setTypeface(ralewayMessage);
        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ralewayMessage = Typeface.createFromAsset(getContext().getAssets(),"Raleway-SemiBold.ttf");

    }
}
