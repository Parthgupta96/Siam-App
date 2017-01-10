package com.example.parth.siamapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Parth on 09-01-2017.
 */

public class Utils {
   public static boolean isInternetConnected(Context context){
       final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
       return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
   }
}
