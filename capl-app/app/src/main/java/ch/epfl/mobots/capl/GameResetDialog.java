package ch.epfl.mobots.capl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import ch.epfl.mobots.capl.ui.GeographyActivity;


public class GameResetDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final GeographyActivity geographyActivity = (GeographyActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Game reset")
                .setMessage("By clicking on 'ok', a new destination to visit will be given to you! 😃 Pay attention to the fact that your battery level is not reset to 100%...")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        geographyActivity.randomInitialization();

                    }
                });

        return builder.create();
    }



}



