package com.example.frats;

import android.content.Context;
import android.content.Intent;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

import java.util.ArrayList;

public class userOrAssistantAdapter extends ArrayAdapter<userOrAssistant> {

    Context context;
    private ArrayList<userOrAssistant> list = new ArrayList<>(10);
    public userOrAssistantAdapter(Context context, ArrayList<userOrAssistant> list)
    {

        super( context, 0, list);

        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentView = convertView;

        if( currentView == null )
        {
            currentView = LayoutInflater.from( getContext() ).inflate( R.layout.assistant_contact , parent, false);
        }

        userOrAssistant currentUserOrAssistant = getItem( position );

        TextView username, phone;
        LinearLayout  l = currentView.findViewById( R.id.loadChat );
        ImageView more = currentView.findViewById( R.id.more );

        username = currentView.findViewById(R.id.name);
        phone = currentView.findViewById( R.id.phone );

        assert currentUserOrAssistant != null;

        phone.setText( currentUserOrAssistant.getPhone() );
        username.setText( currentUserOrAssistant.getUserName() );
        /* l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChat(view);
            }
        }); */

        currentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChat(view);
            }
        });

        if( isUser(currentView) )
        {
            more.setVisibility(View.INVISIBLE);
        }

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMore(view, username.getText().toString(), phone.getText().toString());
            }
        });


        return currentView;

    }

    private boolean isUser(View view)
    {

        user u = new user(view.getContext());
        u.open();
        String[] data =u.readData();
        u.close();

        if( data[3].equals("assistant") )
            return false;

        return true;
    }

    public void loadChat(View view)
    {
        TextView n = view.findViewById(R.id.name);
        TextView p = view.findViewById(R.id.phone);
        String nameStr = n.getText().toString();
        String phoneStr = p.getText().toString();

        Toast.makeText(view.getContext(), "Clicked on user contact: " + nameStr + " ~ " + phoneStr, Toast.LENGTH_SHORT).show();

        Intent chatIntent = new Intent( view.getContext(), chat.class);
        chatIntent.putExtra("recipient",phoneStr);
        chatIntent.putExtra("chatName", nameStr);
        view.getContext().startActivity(chatIntent);
    }

    public void showMore(View view, String username, String phone)
    {
        Toast.makeText(view.getContext(), "Clicked on more", Toast.LENGTH_SHORT).show();
        PopupMenu popup = new PopupMenu( view.getContext(), view);
        popup.getMenu().add("Add To");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Toast.makeText(context, "clicked " + menuItem.getItemId(), Toast.LENGTH_SHORT).show();
                PopupMenu subPopup = new PopupMenu( view.getContext(), view);
                subPopup.inflate( R.menu.group_options);

                subPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String titleCondensed = menuItem.getTitleCondensed().toString();

                        Toast.makeText(context, "Clicked " + titleCondensed, Toast.LENGTH_SHORT).show();
                        //add this contact or user to the group as a member
                        MyFirebaseUtilityClass.addUserToGroup(titleCondensed, username, phone);

                        return false;
                    }
                });

                subPopup.show();

                return false;
            }
        });

        popup.show();
    }
}
