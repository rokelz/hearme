package com.example.test;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Created by USER on 5/6/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the hearing testing mechanisms.
 */
public class Testhearing {
    /**
     * Use to set the volume of the left ear piece when the decibel value is circulated.
     * Since this is being used as the left ear piece profile selection
     *
     * @param decibelVal
     * @return
     */
    public float setthevolumeofleft(int decibelVal) {
        float leftDecibelValue = decibelVal;
        float rightDecibelValue = 0;
        float leftOutputValue = new Float(Math.pow(10, leftDecibelValue / 20));
        return leftOutputValue;
    }

    /**
     * Use to set the volume of the right ear piece when the decibel value is circulated.
     * Since this is being used as the right ear piece profile selection.
     *
     * @param decibelVal
     * @return
     */
    public float setthevolumeofright(int decibelVal) {
        float rightDecibelValue = decibelVal;
        float leftDecibelValue = 0;
        float rightOutputValue = new Float(Math.pow(10, rightDecibelValue / 20));
        return rightOutputValue;
    }


}

/**
 * @author R.K.Opatha (IT13510572)
 *         Use to check whether the headset is being plugged or not.
 * @see BroadcastReceiver
 */
class MusicIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    Log.i("", "Headset is unplugged");

                    LayoutInflater layoutInflater = LayoutInflater.from(context);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("-----HearMe-----");
                    alertDialog.setMessage("Please plug your headset inoder to continue...");
                    alertDialog.setIcon(R.drawable.icon);
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent goBack = new Intent(context, mainoption.class);
                                    context.startActivity(goBack);
                                }
                            });

                    alertDialog.show();

                    break;
                case 1:
                    Log.i("", "Headset is plugged");
                    break;
                default:
                    Log.i("", "I have no idea what the headset state is");
            }
        }
    }
}

