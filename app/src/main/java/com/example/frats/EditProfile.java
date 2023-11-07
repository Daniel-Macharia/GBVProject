package com.example.frats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    EditText /* username, phone, */ password, confirm;

    Button save;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        try {
            super.onCreate(SavedInstanceState);
            setContentView(R.layout.edit_profile);

            //username = findViewById(R.id.username);
            //phone = findViewById(R.id.phone);
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

            //username.setHint(currentUsername);
            //phone.setHint( "0" + currentPhone.substring( currentPhone.length() - 9 ));

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     try{
                        //String name = "", contact = "", pass = "", confirmPass = "";
                       // String name = username.getText().toString();
                        //String contact = phone.getText().toString();
                        String pass = password.getText().toString();
                        String confirmPass = confirm.getText().toString();

                        validateData(/*name, contact,*/ "","", pass, confirmPass);

                        resetAllFields();
                    }catch( Exception e )
                    {
                        Toast.makeText(EditProfile.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void validateData(String name, String contact, String pass, String confirmPass)
    {
        try{
                   /* if( name.isEmpty() )
                    {
                        Toast.makeText(EditProfile.this, "Name is empty", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        user u = new user( EditProfile.this );
                        u.open();
                        u.updateUserName( name );
                        String[] data = u.readData();
                        u.close();

                        if( data[2].equals("") )
                            return;
                        MyFirebaseUtilityClass.updateUsername( EditProfile.this, data[2], name );
                        MyFirebaseUtilityClass.updateGroupMessagesWhereSenderIs( EditProfile.this,  null, data[2], name);
                        Toast.makeText(EditProfile.this, "updated username", Toast.LENGTH_SHORT).show();
                    }
                    if( contact.isEmpty() )
                    {
                        Toast.makeText(EditProfile.this, "Phone is blank", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if( MyFirebaseUtilityClass.validatePhone( EditProfile.this, contact ) )
                        {
                            user u = new user( EditProfile.this );
                            u.open();
                            u.updateUserPhone( contact );
                            String[] data = u.readData();
                            u.close();

                            MyFirebaseUtilityClass.updatePhone( EditProfile.this, data[2], contact);
                            MyFirebaseUtilityClass.updateGroupMessagesWhereSenderIs( EditProfile.this, data[2], contact, data[0]);
                            MyFirebaseUtilityClass.updateContactToAllMyChats( EditProfile.this, data[2], contact);
                            Toast.makeText(EditProfile.this, "updated phone", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            Toast.makeText(EditProfile.this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                        }

                    } */

                    if( pass.isEmpty() )
                    {
                        Toast.makeText(EditProfile.this, "Password cannot be blank blank! ", Toast.LENGTH_SHORT).show();
                    }
                    else if(  confirmPass.isEmpty() )
                    {
                        Toast.makeText(EditProfile.this, "Confirm Password cannot be blank blank! ", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(EditProfile.this, "updated password", Toast.LENGTH_SHORT).show();
                        if (pass.equals(confirmPass))
                        {
                            user u = new user( EditProfile.this );
                            u.open();
                            u.updateUserPassword(pass);
                            u.close();
                        } else {
                            Toast.makeText(EditProfile.this, "Password and the confirm password do not match\n", Toast.LENGTH_SHORT).show();
                            password.setText("");
                            confirm.setText("");
                        }
                    }


        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void resetAllFields() {
       // username.setText("");
       // phone.setText("");
        password.setText("");
        confirm.setText("");
    }

}
