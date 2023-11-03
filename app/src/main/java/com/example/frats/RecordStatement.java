package com.example.frats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.Calendar;

public class RecordStatement extends AppCompatActivity {

    Button send, record, play;
    EditText recipient;
    private static String filename = null;
    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private MediaPlayer player = null;
    private boolean isPlaying = false;

    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        try{
            super.onCreate( savedInstanceState );
            setContentView(R.layout.record_statement);
            record = findViewById(R.id.record);
            play = findViewById( R.id.play);
            send = findViewById( R.id.sendStatement );
            recipient = findViewById(R.id.recipient);

            requestPermissions();

            String s = createNewFile();
            if( s == null )
            {
                Toast.makeText(this, "failed to create file!", Toast.LENGTH_SHORT).show();
                return;
            }

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String recipientAddress = recipient.getText().toString();
                    if( MyFirebaseUtilityClass.validatePhone(RecordStatement.this, recipientAddress ) )
                    {
                        sendAudioMessage( filename, recipientAddress);
                        Toast.makeText(RecordStatement.this, "Sending Record", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(RecordStatement.this, "Please ensure the phone number is valid\n\n" +
                                "should be 10 digits long and \n" +
                                "start with either 07... or 01...", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(RecordStatement.this, "clicked record", Toast.LENGTH_SHORT).show();
                    if( isRecording )
                    {
                        Toast.makeText(RecordStatement.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
                        stopRecording();
                        record.setText("Record");
                    }
                    else {
                        Toast.makeText(RecordStatement.this, "Started recording", Toast.LENGTH_SHORT).show();
                        startRecording();
                        record.setText("Stop");
                    }

                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(RecordStatement.this, "clicked play", Toast.LENGTH_SHORT).show();
                    if( isPlaying )
                    {
                        Toast.makeText(RecordStatement.this, "Stopped Playing", Toast.LENGTH_SHORT).show();
                        stopPlaying();
                        play.setText("Play");
                    }else {
                        Toast.makeText(RecordStatement.this, "Started Playing", Toast.LENGTH_SHORT).show();
                        startPlaying();
                        play.setText("Stop");
                    }
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording()
    {
       try{
           if( isPlaying )
           {
               stopPlaying();
               play.setText("Play");
           }
           recorder = new MediaRecorder();

           recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
           recorder.setOutputFile(filename);
           recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
           recorder.prepare();
           recorder.start();
           isRecording = true;
       }catch( Exception e )
       {
           Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }

    private void stopRecording()
    {
        try{
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startPlaying()
    {
        player = new MediaPlayer();
        try{
            if( isRecording )
            {
                stopRecording();
                record.setText("Record");
            }
            player.setDataSource(filename);
            player.prepare();
            player.start();
            isPlaying = true;
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.release();
                    player = null;
                    isPlaying = false;
                    play.setText("Play");
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying()
    {
        try{
            if(! (player == null) )
            {
                player.release();
                player = null;
                isPlaying = false;
            }
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAudioMessage(String path, String recipient)
    {
        try{
            //SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId( SmsManager.getDefaultSmsSubscriptionId() );
            //smsManager.sendMultimediaMessage(this, path, null, null, null);

            Intent sendIntent = new Intent( Intent.ACTION_SEND );
            sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, recipient);
            sendIntent.putExtra("address", Integer.parseInt(recipient));
            sendIntent.setType("audio/*");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse( path ) );

            startActivity( Intent.createChooser( sendIntent, "Send") );
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions()
    {
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO )
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

    }

    private String createNewFile()
    {
        String p = null;

        try{
            // String name = "frats_recording_audio_statement";
            String name = Calendar.getInstance().getTimeInMillis() + "_" + Calendar.getInstance().get(Calendar.DATE)
                    + "_" + Calendar.getInstance().get(Calendar.MONTH) + "_" + Calendar.getInstance().get(Calendar.YEAR);
            filename = getExternalCacheDir().getAbsolutePath();
            //filename = Environment.getExternalStorageDirectory().getAbsolutePath();
            filename += "/frats_statement_" + name + ".mp3";
            File f = new File(filename);

            p = f.getAbsolutePath();
        }catch ( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return p;
    }
}
