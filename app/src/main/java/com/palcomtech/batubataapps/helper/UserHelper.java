package com.palcomtech.batubataapps.helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.palcomtech.batubataapps.model.User;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String email, String name, String phone) {
        User user = new User(uid, email, name, phone);
        return UserHelper.getUserCollection().document(uid).set(user);
    }

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUserCollection().document(uid).get();
    }

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUserCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return UserHelper.getUserCollection().document(uid).update("isMentor", isMentor);
    }

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUserCollection().document(uid).delete();
    }
}
