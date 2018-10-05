package live.noxbox.state;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.annotation.Nullable;

import live.noxbox.model.Profile;
import live.noxbox.tools.Task;

public class Firestore {

    private static FirebaseFirestore db() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build());
        return db;
    }

    private static DocumentReference profile() {
        return db().collection("profiles")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    // Profiles API
    static void listenProfile(@NonNull final Task<Profile> task) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        profile().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    Profile profile = snapshot.toObject(Profile.class);
                    task.execute(profile);
                } else {
                    listenProfile(task);
                }
            }
        });
    }

    public static void persistNotificationToken(String notificationToken) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            profile().update("notificationKeys.android", notificationToken);
        }
    }
}
