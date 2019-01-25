package com.eleganz.msafiri.updateprofile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;


/**
 * Created by mahadev on 6/23/16.
 */
public class  AppDialogs {

    Context mContext;
    ProgressDialog pDialog;

    public AppDialogs(Context mContext) {
        this.mContext = mContext;
    }

    public void showProgressDialog() {
        if (mContext != null) {
            if (pDialog == null) {
                pDialog = new ProgressDialog(mContext);
            }
            pDialog.setMessage("Please Wait");
            pDialog.setCancelable(false);
            pDialog.show();
        }

    }

    public void hideProgressDialog() {
        if (mContext != null) {
            if ((pDialog != null) && (pDialog.isShowing())) {
                pDialog.dismiss();
                pDialog = null;
            }
        }
    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNetworkDialog(final Activity activity) {
        if (!(activity).isFinishing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mContext);
            alertDialogBuilder
                    .setMessage("No network connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK"
                            , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


}
