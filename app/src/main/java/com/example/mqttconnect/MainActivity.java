package com.example.mqttconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
     /*
     to connect with password:
     static String MQTTHOST=
     static String USERNAME=
     static String PASSWORD=
     String topicStr=
      */
    private Button connection_button;
    private Button disconnect_button;
    private Button publish_button;
    MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connection_button=findViewById(R.id.connection_button);
        String clientId = MqttClient.generateClientId();
        //the server url must be replaced with the designated one
        client =
                new MqttAndroidClient(MainActivity.this, "tcp://broker.hivemq.com:1883",
                        clientId);
        //MqttConnectOptions options = new MqttConnectOptions();
        //options.setUserName("USERNAME");
        //options.setPassword("PASSWORD".toCharArray());


        connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

            }
        });
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
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void toPublish(View view)
    {
        publish_button=findViewById(R.id.publish_button);
        String topic = "foo/bar";
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
