package ch.epfl.mobots.capl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import ch.epfl.mobots.capl.ui.GeographyActivity;


public class DestinationReachedDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final GeographyActivity geographyActivity = (GeographyActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Destination reached!! 🏆🎉")
                .setMessage("Congratulations! 🎊 You reached the destination! You can now reinitialize the game by clicking on 'ok' to get a new destination to reach! 😃 (Because it's not very fair to bring Cardbot to the same place twice). But pay attention to the fact that your battery level is not reset to 100% ... ⚠️")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        geographyActivity.randomInitialization();

                    }
                });

        return builder.create();
    }



}



