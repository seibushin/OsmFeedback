package de.hhu.cs.firebase;/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class FeedbackHandler extends DefaultHandler {
	private List<Feedback> feedbacks = new ArrayList<>();
	private Feedback feedback = null;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equalsIgnoreCase("node")) {
			feedback = new Feedback();

			double lat = Double.parseDouble(attributes.getValue("lat"));
			double lon = Double.parseDouble(attributes.getValue("lon"));

			feedback.setLatitude(lat);
			feedback.setLongitude(lon);
		} else if (qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("name")) {
			feedback.setDetails(attributes.getValue("v"));
		} else if (qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("addr:city")) {
			feedback.setCity(attributes.getValue("v"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("node")) {

			// add new feedback
			feedbacks.add(feedback);
		}
	}

	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}
}
