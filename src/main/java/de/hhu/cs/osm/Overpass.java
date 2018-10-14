package de.hhu.cs.osm;/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Overpass {
	private static final String API = "https://overpass-api.de/api/interpreter";
	public static final String OUT_XML = "osm.xml";

	public static void post(String data) throws IOException {
		URL url = new URL(API);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		try {
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Feedbackr - OSM-POI");

			// post the data
			try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
				dos.write(data.getBytes(StandardCharsets.UTF_8));
			}

			// get the result and write it to file
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(OUT_XML))))) {
				String line;
				while ((line = br.readLine()) != null) {
					bw.append(line);
					bw.append(System.lineSeparator());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
	}
}
