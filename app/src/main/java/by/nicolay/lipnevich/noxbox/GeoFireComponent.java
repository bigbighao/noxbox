/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package by.nicolay.lipnevich.noxbox;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

import by.nicolay.lipnevich.noxbox.performer.massage.R;
import by.nicolay.lipnevich.noxbox.tools.Timer;
import by.nicolay.lipnevich.noxbox.tools.TravelMode;

import static by.nicolay.lipnevich.noxbox.tools.Firebase.getAvailablePerformers;
import static by.nicolay.lipnevich.noxbox.tools.Firebase.getProfile;

public abstract class GeoFireComponent extends MapActivity {

    private GeoQuery geoQuery;

    protected void goOnline() {
        getAvailablePerformers().setLocation(createKey(), getCurrentPosition().toGeoLocation());
    }

    protected void goOffline() {
        getAvailablePerformers().removeLocation(createKey());
    }

    private String createKey() {
        return getProfile().getId() + ":" + getProfile().getTravelMode().toString();
    }

    private void listenAvailablePerformers(GeoLocation geoLocation) {
        if(geoQuery != null) geoQuery.removeAllListeners();
        geoQuery = getAvailablePerformers().queryAtLocation(geoLocation, getResources().getInteger(R.integer.radius_in_kilometers));
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                String performerId = key.split(":")[0];
                if(getProfile().getId().equals(performerId)) return;
                TravelMode travelMode = TravelMode.valueOf(key.split(":")[1]);
                GroundOverlay marker = createMarker(performerId, new LatLng(location.latitude, location.longitude), getPerformerDrawable());
                marker.setTag(travelMode);
            }

            @Override
            public void onKeyExited(String key) {
                String performerId = key.split(":")[0];
                if(getProfile().getId().equals(performerId)) return;
                removeMarker(performerId);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                String performerId = key.split(":")[0];
                if(getProfile().getId().equals(performerId)) return;
                TravelMode travelMode = TravelMode.valueOf(key.split(":")[1]);
                GroundOverlay marker = createMarker(performerId, new LatLng(location.latitude, location.longitude), getPerformerDrawable());
                marker.setTag(travelMode);
            }

            @Override public void onGeoQueryReady() {}
            @Override public void onGeoQueryError(DatabaseError error) {}
        });
    }

    protected void listenAvailablePerformers() {
        if(googleMap == null) {
//            wait a second for google map loading
            new Timer() {
                @Override
                protected void timeout() {
                    listenAvailablePerformers();
                }
            }.start(1);
            return;
        }

        listenAvailablePerformers(getCameraPosition().toGeoLocation());
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                listenAvailablePerformers(getCameraPosition().toGeoLocation());
                scaleMarkers();
                removeOutOfRangeMarkers();
            }
        });
    }

    protected void stopListenAvailablePerformers() {
        if(geoQuery != null) {
            geoQuery.removeAllListeners();
            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    scaleMarkers();
                }
            });
        }
    }

}
