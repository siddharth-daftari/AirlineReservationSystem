package edu.sjsu.cmpe275;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class TestCases {
	
	String baseURL = "";
	
	@Before
	public void beforeTests(){
		baseURL = "http://localhost:8080";
	}
	
	//Test case for testing create passenger and get passenger calls
	@Test
	public void createPassenger(){
		
		//creating  anew passenger
		String urlTemp = "/passenger";
		String urlParameters = "firstname=Siddharth&lastname=Patel&age=21&gender=male&phone=6692949524";
		String requestMethod = "POST";
		
		JSONObject returnJSON = makeRequest(urlTemp, urlParameters, requestMethod);
		
		int responseCode = returnJSON.getInt("responseCode");
		
		assertEquals(responseCode, 200);
		
		//testing get passenger (in JSON format) for the newly created passenger
		String passengerId = returnJSON.getJSONObject("passenger").getString("id");
		urlTemp = "/passenger/" + passengerId;
		urlParameters = "";
		requestMethod = "GET";
		returnJSON = makeRequest(urlTemp, urlParameters, requestMethod);
		assertEquals(returnJSON.getJSONObject("passenger").getString("id"), passengerId);
		
		//testing get passenger for the a non existing passenger- should return 404
		passengerId = "0";
		urlTemp = "/passenger/" + passengerId;
		urlParameters = "";
		requestMethod = "GET";
		returnJSON = makeRequest(urlTemp, urlParameters, requestMethod);
		responseCode = returnJSON.getInt("responseCode");
		assertEquals(responseCode, 404);
		
		//again creating a passenger with same phone number - should give 400 error code
		urlTemp = "/passenger";
		urlParameters = "firstname=Siddharth&lastname=Patel&age=21&gender=male&phone=6692949502";
		requestMethod = "POST";
		returnJSON = makeRequest(urlTemp, urlParameters, requestMethod);
		responseCode = returnJSON.getInt("responseCode");
		
		assertEquals(responseCode, 400);
	}
	
	public JSONObject makeRequest(String urlTemp, String urlParameters, String requestMethod){
		StringBuffer response = new StringBuffer();
		JSONObject jsonObject = null;
		HttpURLConnection con = null;
		try{
			String url = baseURL + urlTemp;
			URL obj = new URL(url);
			
			
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(requestMethod);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setDoOutput(true);
			con.setUseCaches(false);
			
			if(!"GET".equalsIgnoreCase(con.getRequestMethod()) && con.getRequestMethod()!=null && !"".equalsIgnoreCase(urlParameters)){
				
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
				con.getOutputStream().close();
			}
			
			int responseCode = con.getResponseCode();
			
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
			inputStream.close();
			con.disconnect();
			jsonObject = new JSONObject(response.toString());
			jsonObject.put("responseCode", responseCode);
			
		}catch(Exception e){
			
			e.printStackTrace();
			fail();
		}finally{
			
		}
		return jsonObject;
	}
}