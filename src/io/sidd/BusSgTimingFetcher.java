package io.sidd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BusSgTimingFetcher extends TimingFetcher {

	private static final String resourceURL = "http://104.199.135.75/api/v1/prediction";
	private String route;
	private String busStop;
	private boolean dataAvailable = false;
	private boolean busRunning = false;
	private StringBuilder busTimingData = new StringBuilder("Not known");
	private static final String USER_AGENT = "";
	
	public BusSgTimingFetcher(String route, String busStop) {
		this.route = route;
		this.busStop = busStop;
		fetchTime();
	}
	
	private void fetchTime() {
		JSONObject response = null;
		try {
			response = getResponse();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		if (!response.containsKey("nextbus")) {
			busTimingData = new StringBuilder("Data unavailable");
			return;
		}
		
		JSONObject nextbus = (JSONObject) response.get("nextbus");
		JSONObject subsequentbus = (JSONObject) response.get("subsequentbus");
		
		System.out.println(nextbus.get("t"));
		
		long t1 = (Long)nextbus.get("t");
		long t2 = (Long)subsequentbus.get("t");
		
		if ((t1 < 0) && (t2 < 0)) {
			busTimingData = new StringBuilder("Bus timing for " + route + "not available");
			dataAvailable = true;
			return;
		}
		
		dataAvailable = true;
		busRunning = true;
		
		busTimingData = new StringBuilder();
		
		if (t1 >= 0) {
			busTimingData.append("Next bus in " + t1 + " minutes.\n");
		}
		if (t2 >= 0) {
			busTimingData.append("Subsequent bus in " + t2 + " minutes.");
		}
	}

	private JSONObject getResponse() throws IOException, ParseException {
		String getUrl = resourceURL + "?route=" + route + "&stop=" + busStop;
		
		URL obj = new URL(getUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + getUrl);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		System.out.println(response.toString());
		JSONObject result = (JSONObject) new JSONParser().parse(response.toString());
		
		return result;
	}
	
	@Override
	public boolean isDataAvailable() {
		return dataAvailable;
	}
	
	@Override
	public boolean isBusRunning() {
		return busRunning;
	}
	
	@Override
	public String timeToBus() {
		return busTimingData.toString();
	}
	

}
