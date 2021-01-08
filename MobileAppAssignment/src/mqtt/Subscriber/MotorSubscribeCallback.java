package mqtt.Subscriber;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import Utils.Utils;
import mqtt.Publisher.MotorMover;

public class MotorSubscribeCallback implements MqttCallback {

	public static final String userid = "17001340";
	
	@Override
	public void connectionLost(Throwable cause) 
	{
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("Message Arrived. Topic: " + topic + "\nMessage:  " + message.toString() + "\n");
		
		///moves the motor
		double messageMotor = 180.0;
		MotorMover.moveServoTo(Double.valueOf(messageMotor));
	    System.out.println("Waiting until motor at position: " + message.toString());
	    Utils.waitFor(5);
	    MotorMover.moveServoTo(0.0);
	    Utils.waitFor(2);
	     
	    if ((userid+"/LWT").equals(topic)) 
	    {
	        System.err.println("Sensor gone!");
	    }
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) 
	{

	}

}


