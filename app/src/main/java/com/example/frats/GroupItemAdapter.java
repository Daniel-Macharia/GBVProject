package com.example.frats;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GroupItemAdapter extends ArrayAdapter <Pair<String, String>>{


    public GroupItemAdapter(@NonNull Context context, ArrayList<Pair<String, String>> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentView = convertView;

       try{

           if( currentView == null )
           {
               currentView = LayoutInflater.from( getContext() ).inflate( R.layout.group_item, parent, false);
           }

           Pair<String, String> groupName = getItem(position);

           assert groupName != null;
           int count = 0;
           NewMessageCounter nmc = new NewMessageCounter(getContext());
           nmc.open();
           count = nmc.getCount( groupName.first);
           nmc.close();

           TextView name = currentView.findViewById(R.id.groupName);
           TextView newCount = currentView.findViewById(R.id.count);

           name.setText( groupName.second);
           newCount.setText( ( count == 0) ? "" : "" + count);

       }catch( Exception e )
       {
           Toast.makeText(getContext(), "GroupItemAdapter Error: " + e.toString(), Toast.LENGTH_SHORT).show();
       }

        return currentView;
    }
}
