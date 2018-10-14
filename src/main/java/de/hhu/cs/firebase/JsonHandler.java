package de.hhu.cs.firebase;/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import com.firebase.geofire.core.GeoHash;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.FileWriter;

public class JsonHandler extends DefaultHandler {
	private static String JSON_OUT = "osm.json";

	private JsonObjectBuilder feedbacks = Json.createObjectBuilder();
	private JsonObjectBuilder feedback = null;

	private JsonObjectBuilder geofire = Json.createObjectBuilder();
	private JsonObjectBuilder geo = null;

	private int count = 0;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equalsIgnoreCase("node")) {
			feedback = Json.createObjectBuilder();
			geo = Json.createObjectBuilder();

			double lat = Double.parseDouble(attributes.getValue("lat"));
			double lon = Double.parseDouble(attributes.getValue("lon"));

			// geohash
			GeoHash geoHash = new GeoHash(lat, lon);
			String hash = geoHash.getGeoHashString();
			geo.add(".priority", hash);
			geo.add("g", hash);

			// location
			JsonArrayBuilder loc = Json.createArrayBuilder();
			loc.add(lat);
			loc.add(lon);
			geo.add("l", loc);

			feedback.add("latitude", lat);
			feedback.add("longitude", lon);
		} else if (qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("name")) {
			if (Math.random() > 0.5) {
				feedback.add("details", attributes.getValue("v"));
			} else {
				feedback.add("details", "");
			}
		} else if (qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("addr:city")) {
			feedback.add("city", attributes.getValue("v"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("node")) {
			// add data
			if (Math.random() > 0.5) {
				feedback.add("positive", true);
				feedback.add("category", "POS_GENERAL");
			} else {
				feedback.add("positive", false);
				feedback.add("category", "NEG_GENERAL");
			}

			feedback.add("date", System.currentTimeMillis());
			feedback.add("id", count + "");
			feedback.add("image", "");
			feedback.add("owner", "admin");
			feedback.add("published", true);
			feedback.add("rating", (int) (Math.random() * 6));

			// add new feedback
			feedbacks.add(count + "", feedback.build());
			geofire.add(count + "", geo.build());
			count++;
		}

		if (qName.equalsIgnoreCase("osm")) {
			try (JsonWriter jw = Json.createWriter(new FileWriter(JSON_OUT))) {
				JsonObjectBuilder doc = Json.createObjectBuilder();
				doc.add("feedback", feedbacks);
				doc.add("geofire", geofire);
				jw.write(doc.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
