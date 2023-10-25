package com.example.frats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    EditText username, phone, password, confirm;

    Button save;
    @Override
    protected void onCreate( Bundle SavedInstanceState )
    {
        try{
            super.onCreate( SavedInstanceState );
            setContentView( R.layout.edit_profile );

            username = findViewById(R.id.username);
            phone = findViewById(R.id.phone);
            password = findViewById(R.id.password);
            confirm = findViewById(R.id.confirm);

            save = findViewById(R.id.save);

            String currentUsername = "", currentPhone = "";
            user u = new user(this);
            u.open();
            String[] data = u.readData();
            u.close();

            currentUsername = data[0];
            currentPhone = data[2];

            username.setHint(currentUsername);
            phone.setHint(currentPhone);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name, contact, pass, confirmPass;
                    name = username.getText().toString();
                    contact = phone.getText().toString();
                    pass = password.getText().toString();
                    confirmPass = confirm.getText().toString();

                    Toast.makeText(EditProfile.this, "Saved: \n" +
                            "name : " + name + "\nContact: " + contact + "\nPassword: " +
                            pass + "\nConfirmed Password: " + confirmPass, Toast.LENGTH_SHORT).show();

                    if( pass.equals( confirmPass ) )
                    {
                        updateDatabase(name, contact, pass);
                    }
                    else {
                        Toast.makeText(EditProfile.this, "Password and the confirm password do not match\n", Toast.LENGTH_SHORT).show();
                        password.setText("");
                        confirm.setText("");
                        return;
                    }

                    resetAllFields();
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void resetAllFields()
    {
        username.setText("");
        phone.setText("");
        password.setText("");
        confirm.setText("");
    }

    private void updateDatabase(String username, String phone, String password)
    {
        user u = new user(this);
        String[] data = u.readData();
        u.close();
    }
}
