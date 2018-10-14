package de.hhu.cs;/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.hhu.cs.firebase.Feedback;
import de.hhu.cs.firebase.FeedbackHandler;
import de.hhu.cs.firebase.FirebaseAdmin;
import de.hhu.cs.osm.Overpass;
import de.hhu.cs.osm.XMLParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class OsmFeedback extends Application {
	private static String FXML_MAIN = "/fxml/main.fxml";

	@FXML
	private TextArea data;

	@Override
	public void start(Stage stage) {
		try {
			// set up the application interface
			stage.setTitle("OsmFeedback");
			stage.setOnCloseRequest(event -> close());
			stage.setResizable(false);

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_MAIN));
			fxmlLoader.setController(this);

			Scene scene = new Scene(fxmlLoader.load());

			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	/**
	 * Post the text from the textarea to the api from osm.
	 * The xml-answer will be written to file (osm.xml) we then use the Feedbackhandler to generate
	 * Feedback-Objects from the xml and afterwards we directly write them to the firebase database.
	 *
	 * Since we use the firebase-admin sdk we got full admin privileges on the realtime database data
	 */
	public void post() throws IOException {
		// get osm data
		// we post the osm query to the api and get an xml as result
		// the xml will be written to file osm.xml
		Overpass.post(data.getText());

		// parse xml and use the feedbackHandler to generate the feedback objects
		FeedbackHandler handler = new FeedbackHandler();
		XMLParser.parse(handler);

		FirebaseAdmin admin = new FirebaseAdmin();
		// when the parsing is done we can get a list of the retrieved feedbacks and iterate over them
		for (Feedback feedback : handler.getFeedbacks()) {

			// manipulate data if needed
			if (Math.random() > 0.5) {
				// default is true
				feedback.setPositive(false);
			}

			// add a rating from 0 to 5
			feedback.setRating((int) (Math.random() * 6));

			// use the firebase admin api to save the feedback to the database
			admin.saveFeedback(feedback);
		}
	}

	private void close() {
		Platform.exit();
		// terminate process
		System.exit(0);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		close();
	}
}
