package mqtt.Publisher;

import org.eclipse.paho.client.mqttv3.*;


public class PhidgetPublisher
{
	public static final String BROKER_URL    = "tcp://broker.mqttdashboard.com:1883";
	public static final String userid        = "17001340";
	public static final String TOPIC_RFID    = userid + "/rfid";
	public static String TOPIC_MOTOR         = userid + "-sub" + "/motor" +"/";
	private MqttClient client;
	
	public PhidgetPublisher() 
	{
		try 
		{
			client = new MqttClient(BROKER_URL, userid); // creates a Mqtt Session
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(false); //both the client and server maintain their state across restarts of the client, the server and the connection.
			options.setWill(client.getTopic(userid + "/LWT"), "Conection Lost: ".getBytes(), 0, false);// if a client experience an unexpected connection loss to the server, the server will publish a message to itself using the supplied details.
			client.connect(options);
		}
		catch(MqttException me) 
		{ 
			//checks for any errors
			me.printStackTrace(); 
			//prints the details of the errors related to the mqtt exception
			System.exit(1);
			System.out.println("System terminated.");
		}
	}
	
	//handles rfid messages
	public void publishRFID(String rfidTag) throws MqttException 
	{
		final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
		final String rfid = rfidTag + "";
		rfidTopic.publish(new MqttMessage(rfid.getBytes()));
		System.out.println("Published Data from Publish RFID. Topic: " + rfidTopic.getName() + ". Message: " + rfid);
	}
	
	//publishes the correct room name to the motor subscriber
	public void publishMotor(String doorNumber) throws MqttException 
	{
		TOPIC_MOTOR = TOPIC_MOTOR + doorNumber;
		System.out.println("This is Motor Topic: " +  TOPIC_MOTOR);
        final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
        System.out.println("Publishing Door Name : "+ doorNumber + " to topic: "+ motorTopic.getName());
        final String motorMessage = doorNumber;
        motorTopic.publish(new MqttMessage(motorMessage.getBytes()));
        System.out.println("Published data from Publish MOTOR. Topic: " + motorTopic.getName() + ". Message: " + motorMessage);
    }
	
	//used for android notifications
	public void publishNotifications(String door, String tagid, boolean valid) throws MqttPersistenceException, MqttException 
	{
		String TOPIC_ANDROID_NOTIFICATIONS = userid + "/" + door + "/androidNotifications";
		final MqttTopic topic = client.getTopic(TOPIC_ANDROID_NOTIFICATIONS);
		String notifications;
		System.out.println("This is  Notification Topic " +  TOPIC_ANDROID_NOTIFICATIONS);
		if(valid) 
		{
			notifications = "Room: " + door + "Status: Valid and Tag Id: " + tagid;		
		}
		else 
		{
			notifications = "Invalid Tag.";
		}
		topic.publish(new MqttMessage(notifications.getBytes()));
		System.out.println("Published data from Publish NOTIFICATIONS. Topic: " + topic.getName() + ". Message: " + notifications + "\n");
    }
}
