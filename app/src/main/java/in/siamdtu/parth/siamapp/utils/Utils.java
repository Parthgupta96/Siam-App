package in.siamdtu.parth.siamapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by Parth on 09-01-2017.
 */

public class Utils {
   public static boolean isInternetConnected(Context context){
       final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
       return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
   }
   public static void makeToast(String message,Context context){
       Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
   }
}
