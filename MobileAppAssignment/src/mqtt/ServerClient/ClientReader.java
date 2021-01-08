package mqtt.ServerClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.*;

import mqtt.Data.RfidData;
import mqtt.Publisher.PhidgetPublisher;

public class ClientReader
{
	String oneRFIDJSON;
	
	RFID rfid = new RFID();
	Gson gson = new Gson();
	
	PhidgetPublisher publisher = new PhidgetPublisher();
	RfidData rfidD = new RfidData("unknown", "unknown", 0, false);
	
	public static String ServerURL = "http://localhost:8080/MobileAppAssignmentServer/RfidServerDatabase";
    public static void main(String[] args) throws PhidgetException 
    {
        new ClientReader();
    }
    
    public ClientReader() throws PhidgetException 
    {
        rfid.addTagListener(new RFIDTagListener() //listens for the tags and stores them in a variable
        {
			public void onTag(RFIDTagEvent e) 
			{
				String tagRead = e.getTag();
				System.out.println("Tag read: " + tagRead);
				try 
				{
					rfid.open();
					int readerid = rfid.getDeviceSerialNumber(); 
					rfidD.setReader_id(readerid); //sets the reader id from unknown to the correct reader id received using getDeviceSerialNumber
					rfidD.setTag_id(tagRead); //sets the tag id from unknown to the correct tag id received using addTagListener
					System.out.println("Reader Id: " + readerid);
				}
				catch (PhidgetException PE) 
				{
					PE.printStackTrace();
				}
				
				//converting the rfid data into JSON format
				oneRFIDJSON = gson.toJson(rfidD);
				String result = sendToServer(oneRFIDJSON); //sending json format to server
				System.out.println("\nJSON Format. Result: " + result); 
				
				//converting a json string into a standard object
				RfidData resultObject = gson.fromJson(result, RfidData.class);
				System.out.println("Result Object: " + resultObject + "\n");
	
				//sending verified data to publish notifications for android
				try 
				{
					publisher.publishNotifications(resultObject.getRoom_name(), resultObject.getTag_id(), resultObject.isValid());
				} 
				catch (Exception E) {
					E.printStackTrace();
				}

				//Makes the room name dynamic as it only sets the room value to the correct room name when data is received from the server
				rfidD.setRoom_name(resultObject.getRoom_name());
				if(resultObject.isValid())
				{
					try 
					{
						//sending info to the methods in phidget publisher
						publisher.publishMotor(resultObject.getRoom_name());
						publisher.publishRFID(resultObject.getTag_id());
					}
					catch (MqttException ME) 
					{
						ME.printStackTrace();
					}
					try
	    			{
	    				Thread.sleep(5000);
	    			}
	    			catch(InterruptedException ie)
	    			{
	    				ie.printStackTrace();
	    			}
					if(tagRead.contains(resultObject.getTag_id())) 
					{	
						System.out.println("\nTag read: " + tagRead + ", Interacting with the Lock, Opening the Room: " + resultObject.getRoom_name());
					}
					else 
					{
						System.out.println("Tag not found in the database.");
					}
				}
				else
				{
					System.out.println("Tag Read: " + tagRead + ", Operating with the Lock, Access Denied!");
				}
			}
        });

        //listens for the lost tags
        rfid.addTagLostListener(new RFIDTagLostListener() 
        { 
			public void onTagLost(RFIDTagLostEvent e) 
			{
				System.out.println("Tag lost: " + e.getTag());
			}
        });
        
        rfid.open(5000); 
        System.out.println("RFID-Serial Number " + rfid.getDeviceSerialNumber());
        try 
        {                   
            System.out.println("\nGathering data for 30 seconds\n");
            pause(30);
            rfid.close();
            System.out.println("\nRFID Reader - Status: Closed...");
        } 
        catch (PhidgetException PE) 
        {
            System.out.println(PE.getMessage());
        }
    }

    public String sendToServer(String oneRFIDJSON)
    {
    	try 
    	{
    		System.out.println("\nBefore Encoding: " + oneRFIDJSON);
			oneRFIDJSON = URLEncoder.encode(oneRFIDJSON, "UTF-8");
			System.out.println("\nAfter Encoding: " + oneRFIDJSON + "\n");
		}
    	catch (UnsupportedEncodingException UEE) 
    	{
    		UEE.printStackTrace();
		}
    	
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = ServerURL+"?rfidSerialNumber=" + oneRFIDJSON;
        System.out.println("Sending data to: "+ fullURL); 
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
    
	private void pause(int secs)
	{
        try 
        {
			Thread.sleep(secs*1000);
		} 
        catch (InterruptedException IE) 
        {
			IE.printStackTrace();
		}
	}

}