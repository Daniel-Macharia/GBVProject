package com.example.frats;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class chatMessageAdapter extends ArrayAdapter<chatMessage> {

    public chatMessageAdapter(Context context, ArrayList<chatMessage> messageArrayList )
    {
        super(context,0,messageArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        if( currentItemView == null)
        {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message,parent,false);
        }
        chatMessage currentMessage = getItem(position);

        TextView msg = currentItemView.findViewById(R.id.msg);

        assert currentMessage != null;
        msg.setText(currentMessage.getMessage());

        TextView time = currentItemView.findViewById(R.id.time);
        time.setText(currentMessage.getTime());

        int g = currentMessage.getGravity();
        LinearLayout messageLayout = currentItemView.findViewById(R.id.message_layout);
        messageLayout.setGravity(g);

        return currentItemView;
    }

    public void change()
    {
        this.notifyDataSetChanged();
    }

}
