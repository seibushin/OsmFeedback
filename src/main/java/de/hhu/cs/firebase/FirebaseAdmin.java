/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.hhu.cs.firebase;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;

public class FirebaseAdmin {
	private FirebaseApp app;

	private static FirebaseDatabase database;
	private static DatabaseReference rootRef;
	private static DatabaseReference feedbackRef;
	private static DatabaseReference userRef;
	private static DatabaseReference geofireRef;
	private static GeoFire geoFire;

	public FirebaseAdmin() {
		init();
	}

	/**
	 * Establish an admin connection to the firebase application
	 */
	private void init() {
		try {
			FileInputStream serviceAccount = new FileInputStream("feedbackr-5fc1b-firebase-adminsdk.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://feedbackr-5fc1b.firebaseio.com")
					.build();

			app = FirebaseApp.initializeApp(options);

			database = FirebaseDatabase.getInstance();
			rootRef = database.getReference();
			feedbackRef = rootRef.child("feedback");
			userRef = rootRef.child("users");
			geofireRef = rootRef.child("geofire");
			geoFire = new GeoFire(geofireRef);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFeedback(Feedback feedback) {
		if (app != null) {
			feedback.setId(generateFeedbackID());
			System.out.println("Save Feedback " + feedback);

			// update feedback
			feedbackRef.child(feedback.getId()).setValueAsync(feedback);
			// save geofire information
			geoFire.setLocation(feedback.getId(), new GeoLocation(feedback.getLatitude(), feedback.getLongitude()), (key, error) -> {
				if (error != null) {
					System.out.println(error.getMessage());
				}
			});
		}
	}

	public String generateFeedbackID() {
		return feedbackRef.push().getKey();
	}
}
