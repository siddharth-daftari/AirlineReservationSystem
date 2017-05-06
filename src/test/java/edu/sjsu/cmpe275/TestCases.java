package edu.sjsu.cmpe275;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Before;
import org.junit.Test;

public class TestCases {
	
	String baseURL = "";
	
	@Before
	public void beforeTests(){
		baseURL = "http://localhost:8080";
	}
	
	@Test
	public void createPassenger(){
		StringBuffer response = new StringBuffer();
		try{
			String url = baseURL + "/passenger";
			URL obj = new URL(url);
			String urlParameters = "firstname=XX&lastname=YY&age=11&gender=female&phone=123";
			
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			//con.setRequestProperty("Content-Type", "text/plain");
			con.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	
			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			InputStream inputStream;
			if(responseCode != 200){
				
				inputStream = con.getErrorStream();
				
			}else{
				inputStream = con.getInputStream();
			}	
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(inputStream));
			String inputLine;
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				System.out.println(inputLine);
			}
			in.close();
			
		}catch(Exception e){
			
			e.printStackTrace();
			fail();
		}

	}
	
}