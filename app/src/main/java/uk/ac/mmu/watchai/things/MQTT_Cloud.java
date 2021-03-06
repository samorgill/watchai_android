package uk.ac.mmu.watchai.things;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import uk.ac.mmu.babywatch.R;


/**
 * @author Samuel Orgill 15118305
 * NW5 Smartwatch Control of Environment
 * September 2016
 *
 * Cloud MQTT class for sending MQTT messages over the internet.
 */
public class MQTT_Cloud extends AppCompatActivity {

    TextView tv, tv2;
    Button btn;
    String topic;
    String content;
    int qos;
    String broker;
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv = (TextView) findViewById(R.id.textView);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

    }



    public void msgClick(Context mContext){


        TextView tv;
        Button btn;

        Context con = mContext;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);
        String ipAddy = settings.getString("ipadd", "");
        String usrName = settings.getString("usrName", "");

            String mes = GetSet.getMqttMsg();
            String top = GetSet.getMqttTopic();

        topic        = top;
        content      = mes;
        qos          = 1;
        broker       = "tcp://m21.cloudmqtt.com:17781";
        clientId     = "Watchai_Android";

        Log.i("User: ", topic);
        Log.i("Broker: ", broker);

        MemoryPersistence persistence = new MemoryPersistence();
        final MqttAndroidClient mqttClient = new MqttAndroidClient(con.getApplicationContext(),broker, clientId, persistence);
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage msg)
                    throws Exception {
                System.out.println("Recieved:" + topic);
                System.out.println("Recieved now:" + new String(msg.getPayload()));
                Log.i("Recieved now:", new String(msg.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken arg0) {
                System.out.println("Delivary complete");
            }

            public void connectionLost(Throwable arg0) {
                // TODO Auto-generated method stub
            }
        });

        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        System.out.println("Publish message: " + message);
                        mqttClient.subscribe(topic, qos);
                        mqttClient.publish(topic, message);
                        mqttClient.disconnect();

                    } catch (MqttException ex) {

                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Connection Failure!");
                }
            });
        } catch (MqttException ex) {

        }

    }
}
