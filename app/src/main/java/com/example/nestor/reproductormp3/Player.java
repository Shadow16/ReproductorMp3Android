package com.example.nestor.reproductormp3;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class Player extends ActionBarActivity implements View.OnClickListener {

    static MediaPlayer mp;
    ArrayList<File> mySongs;

    SeekBar sb;
    ImageButton btPlay;
    ImageButton btFF;
    ImageButton btFB;
    ImageButton btNxt;
    ImageButton btPv;

    int position;
    Uri u;
    Thread updateSeekBar;
    int progress;

    TextView nom;

    TextView inicio, fin;
    int in, fn, is, fs;
    String a, f, x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btPlay = (ImageButton) findViewById(R.id.btPlay);
        btFF = (ImageButton) findViewById(R.id.btFF);
        btFB = (ImageButton) findViewById(R.id.btFB);
        btNxt = (ImageButton) findViewById(R.id.btNxt);
        btPv = (ImageButton) findViewById(R.id.btPv);

        btPlay.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btFB.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPv.setOnClickListener(this);

        sb = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){

            public void run(){
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mp != null){
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos",0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();

        nom = (TextView)findViewById(R.id.n);
        nom.setText(u.toString());
        // n.setText((CharSequence) mySongs.get(position));

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                inicio = (TextView)findViewById(R.id.inicio);
                in = (progress/1000)/60;
                a = Integer.toString(in);
                is = (progress/1000)%60;
                x = Integer.toString(is);
                inicio.setText(a + ":" + x);

                fin = (TextView)findViewById(R.id.fin);
                fn = (mp.getDuration()/1000)/60;
                f = Integer.toString(fn);
                fs = (mp.getDuration()/1000)%60;
                y = Integer.toString(fs);
                fin.setText(f + ":" + y);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btPlay:
                if(mp.isPlaying()){
                    btPlay.setBackground(getResources().getDrawable(R.drawable.play));
                    mp.pause();
                } else {
                    btPlay.setBackground(getResources().getDrawable(R.drawable.pause));
                    mp.start();
                }
                break;

            case R.id.btFF:
                mp.seekTo(mp.getCurrentPosition()+5000);
                inicio = (TextView)findViewById(R.id.inicio);
                in = (progress/1000)/60;
                a = Integer.toString(in);
                is = (progress/1000)%60;
                x = Integer.toString(is);
                inicio.setText(a + " : " + x);
                break;

            case R.id.btFB:
                mp.seekTo(mp.getCurrentPosition() - 5000);
                inicio = (TextView)findViewById(R.id.inicio);
                in = (progress/1000)/60;
                a = Integer.toString(in);
                is = (progress/1000)%60;
                x = Integer.toString(is);
                inicio.setText(a + " : " + x);
                break;

            case R.id.btNxt:
                mp.stop();
                mp.release();
                position = (position+1)%mySongs.size();
                inicio.setText("");
                fin.setText("");
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                nom.setText(u.toString());
                break;

            case R.id.btPv:
                mp.stop();
                mp.release();
                if(position-1 < 0){
                    position = mySongs.size()-1;
                }
                else { position = position-1; }
                inicio.setText("");
                fin.setText("");
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                nom.setText(u.toString());
                break;
        }
    }
}
