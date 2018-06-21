package by.nicolay.lipnevich.noxbox.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import by.nicolay.lipnevich.noxbox.model.Noxbox;
import by.nicolay.lipnevich.noxbox.model.NoxboxType;
import by.nicolay.lipnevich.noxbox.model.Profile;
import by.nicolay.lipnevich.noxbox.model.Rating;

public class MarkerCreator {
    //provide googleMap,noxbox,profile
   /* public static Bitmap createCustomMarker() {


    }*/
    public static Marker createCustomMarker(Noxbox noxbox, Profile profile, GoogleMap googleMap,Activity activity){
        if (noxbox.getPayer().getId().equals(profile.getId())) {
            //Noxbox.Perfomer
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .title(noxbox.getType().name())
                    .snippet("Description for noxbox type")
                    .position(noxbox.getPosition().toLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(drawImage(
                            getIconBitmap(activity, noxbox.getType()),
                            getRatingColor(noxbox.getPerformer().getRating().getReceived()))))
                    .anchor(0.5f, 1f));

            marker.setTag(noxbox);
            return marker;
        }else {
            //marker for Noxbox.Payer
        }
       return null;
    }
    private static Bitmap drawImage(Bitmap bitmap, int color){
        int borderSize = 8;
        Bitmap bmpWithBorder = Bitmap.createBitmap(bitmap.getWidth() + borderSize, bitmap.getHeight() + borderSize, bitmap.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, borderSize, borderSize, null);
        return bmpWithBorder;
    }
    private static int getRatingColor(Rating rating) {
        if (rating.getLikes() >= 100) {
            return Color.GREEN;
        } else if (rating.getLikes() < 100L && rating.getLikes() >= 95L) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    private static Bitmap getIconBitmap(Activity activity, NoxboxType noxboxType) {
        return BitmapFactory.decodeResource(activity.getResources(), noxboxType.getImage());
    }

}