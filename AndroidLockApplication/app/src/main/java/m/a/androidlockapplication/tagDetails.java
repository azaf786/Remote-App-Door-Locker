package m.a.androidlockapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import static m.a.androidlockapplication.R.layout.tag_details;


public class tagDetails extends AppCompatActivity
{
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    private String userid = "17001340";
    private String clientId = userid + "-android";
    private String motorSensor = "/motor";
    private String room = "";
    private String TOPIC_MOTOR = "";
    private MqttClient mqttClient;
    private String notifications = "";

    Button openLock;

    TextView roomValue;

    androidNotifications notification = new androidNotifications(this);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(tag_details);
        TextView tagValue = findViewById(R.id.tagValue);
        roomValue = findViewById(R.id.roomValue);
        System.out.println("Currently on tagDetails");
        try
        {
            Bundle extras = getIntent().getExtras();
            final androidData theAndroidData = (androidData) extras.get("TagObject"); //gets the resultobject from the main activity
            System.out.println("Room received from the intent: " + theAndroidData.getRoom_name());
            tagValue.setText(theAndroidData.getTag_id());
            roomValue.setText(theAndroidData.getRoom_name());
            notifications = "The room " + theAndroidData.getRoom_name() +" is unlocked";
            System.out.println("Room value " +  theAndroidData.getRoom_name());
            room = theAndroidData.getRoom_name(); //setting the room value to the room received from the server response
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        openLock = findViewById(R.id.openLock);
        openLock.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showNotification();
                System.out.println("Publishing topic");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            mqttMessages();
                            message();
                            startSubscribing();

                        }
                        catch (MqttException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void mqttMessages()
    {
        System.out.println("Connecting to the Mqtt broker");
        try
        {
            mqttClient = new MqttClient(BROKER_URL, clientId, null);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(final String topic, MqttMessage message)
                {
                    System.out.println("DEBUG: MESSAGE ARRIVED... Topic: " + topic + "  Message: " + message);
                    final String messageString = message.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            room = roomValue.getText().toString();
                            String newTopic = userid + "/" + room + "/androidNotifications";
                            if(newTopic.equals(topic))
                            {
                                notification.createNotification("Lock Notifications",messageString);
                            }
                        }
                    });
                    if((TOPIC_MOTOR+"/LWT").equals(topic)){
                        System.out.println("DEAD SENSOR");
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Door Unlocked")
                .setContentText(notifications);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.notify(0, builder.build());




    }

    private void startSubscribing()
    {
        try
        {
            final String topMotor = TOPIC_MOTOR;
            room = roomValue.getText().toString();
            String newTopic = room+"try/notifications";
            mqttClient.subscribe(topMotor);
            mqttClient.subscribe(newTopic);
            System.out.println("Subscriber is listening to " +topMotor);
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void message() throws MqttException
    {
        mqttClient.connect();
        System.out.println("CLIENT has Connected");
        TOPIC_MOTOR = userid + "-sub" + motorSensor + "/" + room;
        final MqttTopic motTopic = mqttClient.getTopic(TOPIC_MOTOR);
        System.out.println(motTopic);
        System.out.println("SENDING THIS ROOM TO ECLIPSE" + room);
        motTopic.publish(new MqttMessage(room.getBytes()));
        System.out.println("Published Data. Topic: "+ motTopic.getName() + " Message: " + room);
    }
}

