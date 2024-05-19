package org.gmele.android.pada.a06broadcastreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StartUpReceiver extends BroadcastReceiver
{
    Context Cont;

    @Override
    public void onReceive (Context context, Intent intent)
    {
        Cont = context;
        String Action = intent.getAction ();
        System.out.println ("****** Broadcast received " + Action);
        if (Action == null)
            ShowMessage ("No Action ?????");
        if (Action == Intent.ACTION_BOOT_COMPLETED)
            DoBoot ();
        if (Action == "android.provider.Telephony.SMS_RECEIVED")
            DoSMS (intent);
        if (Action == "android.intent.action.PHONE_STATE")
            DoPhone (intent);

    }

    void DoBoot ()
    {
        ShowMessage ("Boot Completed... I started");
        WriteLog ("Application started on boot");
        WriteLog ("--");
    }

    void DoSMS (Intent SmsInt)
    {
        ShowMessage ("SMS received");
        Bundle bundle = SmsInt.getExtras ();
        SmsMessage[] Messages = null;
        String SmsSender;
        String SmsSenderM;
        String ToActivity;
        if (bundle != null)
        {
            try
            {
                ToActivity = "";
                Object[] pdus = (Object[]) bundle.get("pdus");
                Messages = new SmsMessage[pdus.length];
                for(int i=0; i<Messages.length; i++)
                {
                    Messages[i] = SmsMessage.createFromPdu ((byte[]) pdus[i], bundle.getString ("format" ));
                    SmsSender = Messages[i].getOriginatingAddress();
                    SmsSenderM = MaskPhoneNum (SmsSender, 6);

                    if (i == 0)
                        ToActivity = "From: " + SmsSenderM + ",   ";
                    String SmsBody = Messages[i].getMessageBody();
                    ToActivity = ToActivity + SmsBody;
                    WriteLog ("SMS message Sender: " + SmsSender);
                    WriteLog ("SMS message: " + SmsBody);
                }
                WriteLog ("--");
                BroadcastMessage ("SMS", ToActivity);
            }
            catch(Exception e)
            {
                System.out.println ("*** Exception Here???");
            }
        }
    }

    void DoPhone (Intent PhoneInt)
    {
        ShowMessage ("Phone Event...");
        String State = PhoneInt.getStringExtra (TelephonyManager.EXTRA_STATE);
        String Caller = PhoneInt.getStringExtra (TelephonyManager.EXTRA_INCOMING_NUMBER);
        String CallerM = MaskPhoneNum (Caller, 6);
        if (State.equals (TelephonyManager.EXTRA_STATE_RINGING))
        {
            ShowMessage ("Phone Ringing : " + CallerM);
            if (Caller != null)
            {
                WriteLog ("Phone Ringing : " + Caller);
                BroadcastMessage ("PHONE", "Call from " + CallerM);
            }
        }
        if (State.equals (TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            ShowMessage ("Off Hook: " + CallerM);
            if (Caller != null)
            {
                WriteLog ("Off Hook: " + Caller);
                BroadcastMessage ("PHONE", "Speaking to " + CallerM);
            }
        }
        if (State.equals (TelephonyManager.EXTRA_STATE_IDLE))
        {
            ShowMessage ("Phone Rests: " + CallerM);
            if (Caller != null)
            {
                WriteLog ("Phone Rests: " + Caller);
                WriteLog ("--");
                BroadcastMessage ("PHONE", "Call to " + CallerM + " ended" );
            }
        }
    }

    void ShowMessage (String Mess)
    {
        Toast Tst = Toast.makeText (Cont, Mess, Toast.LENGTH_LONG);
        Tst.show ();
    }

    String MaskPhoneNum (String PhoneNum, int NoDigs)
    {
        if (PhoneNum == null)
            return "*NULL*";
        PhoneNum = PhoneNum.substring (0, PhoneNum.length () - NoDigs);
        for (int i = 1; i <= NoDigs; i++)
            PhoneNum += '*';
        return PhoneNum;
    }
    void WriteLog (String Line)
    {
        String FName = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).getPath () +
                "/BroadReport.txt";
        try
        {
            PrintWriter out = new PrintWriter (new BufferedWriter (new FileWriter (FName, true)));
            String TimeStamp = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (new Date ());
            if (Line.equals ("--"))
                out.println ("----------------------------------------------------------------------------\n");
            else
                out.println (Line + "   (" + TimeStamp + ")");
            out.close ();
        }
        catch (IOException e)
        {
            ShowMessage ("What??? Exception??? Why??? : " + e.getMessage ());
            System.out.println ("***" + e.getMessage ());
        }
    }

    void BroadcastMessage(String Type, String Mess)
    {
        Intent BroadInt = new Intent ("Fantom-Message");
        BroadInt.putExtra ("To:", Type);
        BroadInt.putExtra ("Message:", Mess);
        LocalBroadcastManager.getInstance (Cont).sendBroadcast (BroadInt);
    }

}