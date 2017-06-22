package com.pip.talk.pip.pc.piptalk;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String>{

    public SpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =convertView;
        if(v==null)
        {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v=vi.inflate(R.layout.spinner_rows,null);
        }
        String p = getItem(position);
        if (p!=null)
        {
            TextView f_Name=(TextView)v.findViewById(R.id.spinner_row_Txt);
            f_Name.setText(p);
        }
        return v;
    }
}

