package org.gmele.android.pada.a06broadcastreciever;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainAct extends AppCompatActivity
{

    TextView TvSMS;
    TextView TvCall;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.main_lay);
        TvSMS = findViewById (R.id.TvSMS);
        TvCall = findViewById (R.id.TvCall);
    }

    @Override
    protected void onStart ()
    {
        super.onStart ();
        LocalBroadcastManager.getInstance (this). registerReceiver (MyReceiver, new IntentFilter ("Fantom-Message"));
        System.out.println ("*** On Start");
    }

    @Override
    protected void onStop ()
    {
        super.onStop ();
        LocalBroadcastManager.getInstance (this). unregisterReceiver (MyReceiver);
        TvSMS.setText ("SMS Info here.....");
        //TvCall.setText ("Phone Call Info Here...");       Or we will never see the speaking to message
        System.out.println ("*** On Stop...");
    }

    @Override
    public void onBackPressed ()
    {
        super.onBackPressed ();
        finish ();
    }

    BroadcastReceiver MyReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent BroadInt)
        {
            System.out.println ("*** On Receive...");
            String Type = BroadInt.getStringExtra ("To:");
            String Mess = BroadInt.getStringExtra ("Message:");
            if (Type.equals ("SMS"))
                TvSMS.setText (Mess);
            if (Type.equals ("PHONE"))
                TvCall.setText (Mess);

        }
    };
}