package com.example.mqttconnect;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
     /*
     to connect with password:

     static String USERNAME=
     static String PASSWORD=

      */

     //Subscription Topic
     String topicStr="test1";
     //server
     static String MQTTHOST="tcp://broker.hivemq.com:1883";

    private Button disconnect_button;
    private TextView sub_topic;
    MqttAndroidClient client;
    private Button sub_button;

    //MqttConnectOptions options
    Vibrator vibrator;
    Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sub_topic=findViewById(R.id.sub_topic);
        sub_button=findViewById(R.id.sub_button);

        //vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);

        //Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //ringtone=RingtoneManager.getRingtone(getApplicationContext(),uri);


        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(MainActivity.this, MQTTHOST,
                        clientId);
        //options = new MqttConnectOptions();
        //options.setUserName("USERNAME");
        //options.setPassword("PASSWORD".toCharArray());



                try {
                    //IMqttToken token = client.connect(options);
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Toast.makeText(MainActivity.this,"Connection Failed",Toast.LENGTH_SHORT).show();


                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }





        disconnect_button=findViewById(R.id.disconnect_button);
        disconnect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IMqttToken disconToken = client.disconnect();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // we are now successfully disconnected
                            Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // something went wrong, but probably we are disconnected anyway
                            Toast.makeText(MainActivity.this,"could not disconnected",Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        sub_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String topic =topicStr;
                int qos = 1;
                try {
                    IMqttToken subToken = client.subscribe(topic, qos);
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // The subscription could not be performed, maybe the user was not
                            // authorized to subscribe on the specified topic e.g. using wildcards

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception
                    {
                        sub_topic.setText(new String(message.getPayload()));
                        //vibrator.vibrate(500);
                        //ringtone.play();
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

            }
        });

    }
    public void toPublish(View view)
    {

        String topic ="test1";
        String message = "Hello World";
        //byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = message.getBytes("UTF-8");
            //MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
