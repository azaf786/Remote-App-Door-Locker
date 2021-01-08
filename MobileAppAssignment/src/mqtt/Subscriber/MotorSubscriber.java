package mqtt.Subscriber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RCServo;

import mqtt.Data.MotorData;


public class MotorSubscriber 
{
	public static String MotorURL = "http://localhost:8080/MobileAppAssignmentServer/RfidServerDatabase";
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String userid = "17001340";
    String clientId = userid + "-sub" + "/motor" + "/"; //unique client id
    
    static RCServo rcs;
    static Gson gson = new Gson();
    static MotorData motorInfo = new MotorData(0, "unknown");
    static String oneMotorJSON;
    static String room;
    
    Connection conn = null;
    Statement stmt;
    ResultSet rs;
    
    private MqttClient mqttClient;
   
    public static void getid() throws PhidgetException 
    {
	  try
	  {
  		//code to read the serial number of the motor
        rcs = new RCServo();
  		rcs.open(5000);
  		int id = rcs.getDeviceSerialNumber();
  		System.out.println("MOTOR-Serial Number " + id);
  		rcs.close();
  		//rcs- connection closed.
  		
  		MotorData motorData = new MotorData(0, "unknown");
  		motorData.setMotor_id(id);
  		
  	    //converting data into JSON Format
  	    String oneMotorJSON = gson.toJson(motorData);
  	    String result = sendToServer(oneMotorJSON); //sends the json string to the server
  	    System.out.println("\nJSON Format. Result: " + result);
  	    
  	    MotorData resultObject = gson.fromJson(result, MotorData.class);
  	    //setting the motorId and RoomId to the values received as a response fromt the server
  	    motorInfo.setMotor_id(resultObject.getMotor_id());
  	    motorInfo.setRoom_name(resultObject.getRoom_name());
  	    room = resultObject.getRoom_name(); //makes the room dynamic
  	  
  	    System.out.println("Room Number Received: " + room);
  	    System.out.println("Result Object: " + resultObject.toString() + "\n");
	  }
	  	    
	  catch(PhidgetException PE)
	  {
	  	PE.getMessage();
	  }
	}

    //sending the json string to this method
    public static String sendToServer(String oneMotorJSON)
    {
    	try 
    	{
    		System.out.println("\nBefore Encoding: " + oneMotorJSON);
    		oneMotorJSON = URLEncoder.encode(oneMotorJSON, "UTF-8");
			System.out.println("\nAfter Encoding: " + oneMotorJSON + "\n");
		}
    	catch (UnsupportedEncodingException UEE) 
    	{
    		UEE.printStackTrace();
		}
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = MotorURL+"?motorSerialNumber="+oneMotorJSON;
        System.out.println("Sending data to: " + fullURL);  // DEBUG confirmation message
        String line;
        String result = "";
        try 
        {
           url = new URL(fullURL);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           while ((line = rd.readLine()) != null) 
           {
              result += line;
              System.out.println("The result = " + result);
           }
           rd.close();
        } 
        catch (Exception E) 
        {
           E.printStackTrace();
        }
        return result;  
    }

	public MotorSubscriber() 
	{
        try 
        {	
            mqttClient = new MqttClient(BROKER_URL, clientId);
        } 
        catch (MqttException ME) 
        {
        	ME.printStackTrace();
            System.exit(1);
        }
    }
    
    public void start() throws SQLException 
    {
        try 
        {
            mqttClient.setCallback(new MotorSubscribeCallback());
            mqttClient.connect();
            System.out.println("Subsriber's Status- CONNECTED");
            final String topic = clientId + room;
            mqttClient.subscribe(topic);
            System.out.println("Subscriber is now listening to " + topic);

        } 
        catch (MqttException ME) 
        {
        	ME.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String... args) throws PhidgetException, Exception 
    {
        final MotorSubscriber subscriber = new MotorSubscriber();
        getid();
        subscriber.start();
    }

}