package com.dcproject.nodues;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SAI on 02-04-2018.
 */

public class CustomAdapter extends BaseAdapter {

    public ArrayList<String> duelist,reslist;
    Activity activity;

    public CustomAdapter(Activity activity,ArrayList<String> duelist,ArrayList<String> reslist){
        super();
        this.activity=activity;
        this.duelist=duelist;
        this.reslist=reslist;
    }

    @Override
    public int getCount() {
        return duelist.size();
    }

    @Override
    public Object getItem(int position) {
        return duelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view =activity.getLayoutInflater().inflate(R.layout.duelist_row,parent,false);

        TextView rem_tv=(TextView)view.findViewById(R.id.tv_rem);
        TextView res_tv=(TextView)view.findViewById(R.id.tv_res);

        rem_tv.setText(duelist.get(position));
        res_tv.setText(reslist.get(position));

        return view;

    }
}
