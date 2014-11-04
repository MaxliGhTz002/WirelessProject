import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetNeigbor {
	private static ArrayList<String> ipv4 = new ArrayList<String>();
	private static int waitingTime = 1000; // 1000 = 1 second

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String ipv4) throws IOException,JSONException {
		InputStream is = null;
		try{
			is = new URL(ipv4).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			is.close();
			return json;
		}catch(Exception e){
			String piID = findPiID(ipv4);
			System.out.println("can't connect to Pi" + piID.substring(0, piID.indexOf(":")) + " : " + ipv4);
			return null;
		}
	}

	public static void getNeighbor(String ipv4) {
		ArrayList<String> myArrList = null;
		try {
			JSONObject json = readJsonFromUrl("http://" + ipv4 + ":9090");
			// System.out.println("json = " + json.toString());
			if(json == null) return;
			JSONArray neighbors = (JSONArray) json.get("neighbors");
			// System.out.println("neighbors = " + neighbors);

			JSONArray data = neighbors;
			myArrList = new ArrayList<String>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject c = data.getJSONObject(i);
				myArrList.add(c.getString("ipv4Address"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Pi" + findPiID(ipv4) + " : " + ipv4);
		for (int i = 0; i < myArrList.size(); i++){
			System.out.println(myArrList.get(i));
		}
		System.out.println("==========================");
	}
	
	public static void getNeighborList(final ArrayList<String> ipv4) {
		Timer myTimer;
		myTimer = new Timer();

		myTimer.schedule(new TimerTask() {
			public void run() {
				for (int i = 0; i < ipv4.size(); i++) {
					getNeighbor(ipv4.get(i));
				}
			}
		}, 0, waitingTime);
		// 1000 = 1 second
	}
	
	public static String findPiID(String ipv4){
		String piID = ipv4;
		for(int i=0;i<piID.length();i++){
			piID = piID.substring(piID.indexOf(".") + 1, piID.length());
		}

		return piID;
	}
	
	public static void main(String[] args) {
		ipv4.add("1.1.1.1");
		ipv4.add("1.1.1.2");
		ipv4.add("1.1.1.3");
		getNeighborList(ipv4);
//		System.out.println(findPiID("1.1.1.2:9090"));
	}
}
