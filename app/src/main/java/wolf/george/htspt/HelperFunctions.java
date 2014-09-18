package wolf.george.htspt;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class HelperFunctions {

    public static double toRad(Double value)
    {
        return value * Math.PI / 180;
    }


    public static boolean setRefreshRate(String currentState, LinkedList<LatLng> latlngArray, LatLng co)
    {
        //Calibration -> active
        if(currentState.equals("A"))
            return AtoB(latlngArray, co);

        //active -> idle
        if(currentState.equals("B"))
            return BtoC(latlngArray, co);

        //idle -> active
        if(currentState.equals("C"))
            return CtoB(latlngArray, co);

        return false;
    }

    //Returns true when time to switch to regular rate of 1 report every 60 seconds
    public static boolean AtoB(LinkedList<LatLng> latlngArray, LatLng co)
    {
        boolean changeFreq = false;
        if(latlngArray.size() >= 5)
        {
            changeFreq = true;
            for (LatLng temp : latlngArray) {
                if (distanceTwoPoints(co, temp) > 50)
                    changeFreq = false;
            }
            latlngArray.remove();
        }
        latlngArray.add(co);
        return changeFreq;
    }

    //Returns true when time to switch to idle rate of 1 report every 5 minutes
    public static boolean BtoC(LinkedList<LatLng> latlngArray, LatLng co)
    {
        boolean toReturn = false;
        for(LatLng temp : latlngArray)
        {
            if(distanceTwoPoints(co, temp) > 15)
                toReturn = true;
        }
        latlngArray.remove();
        latlngArray.add(co);
        return toReturn;
    }

    public static boolean CtoB(LinkedList<LatLng> latlngArray, LatLng co)
    {
        return true;
    }


    public static double distanceTwoPoints(LatLng LatLng1, LatLng LatLng2)
    {
        int radius = 6371000;
        double lat = HelperFunctions.toRad(LatLng1.latitude - LatLng2.latitude);
        double lon = HelperFunctions.toRad(LatLng1.longitude - LatLng2.longitude);
        double a = Math.sin(lat / 2) * Math.sin(lat / 2) +
                Math.cos(HelperFunctions.toRad(LatLng1.latitude)) * Math.cos(HelperFunctions.toRad(LatLng2.latitude)) *
                        Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius * c;
    }
}
