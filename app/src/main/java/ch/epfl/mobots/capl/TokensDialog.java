package ch.epfl.mobots.capl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import ch.epfl.mobots.capl.ui.GeographyActivity;


public class TokensDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final GeographyActivity geographyActivity = (GeographyActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tokens detection required")
                .setMessage("Hey group! You are " + getArguments().getString("number of players") + " right? Take of picture of your tokens!")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        geographyActivity.askCameraPermission();

                    }
                });

        return builder.create();
    }



}



