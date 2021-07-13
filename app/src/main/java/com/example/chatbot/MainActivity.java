package com.example.chatbot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    recieveMessages broadcastReceiver;
    SmsManager smsManager;
    IntentFilter intentFilter;
    Handler handler = new Handler();;
    String sendMessage, message, destNumber;
    TextView currentState, phoneNumber,recievedMessage;
    String wrongResponse = "I'm not sure what you mean, can you please clarify?";
    String [] feelingsInput = {"Alright!", "Sounds good!", "Okay got it!", "Awesome!"};
    String [] byeInput = {"See you soon!", "bye", "goodbye", "bye bye"};
    String [] farewell = {"Have a nice day!", "Thank you, bye!", "Thank you, have a nice day!"};
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    int state = 1;
    int rand = (int)(Math.random()*3)+1;

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new recieveMessages();
        intentFilter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        broadcastReceiver = new recieveMessages();
        intentFilter = new IntentFilter(SMS_RECEIVED);
        currentState = findViewById(R.id.id_text);
        phoneNumber = findViewById(R.id.id_number);
        recievedMessage = findViewById(R.id.id_messageRecieved);


            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, 1);
            }
            registerReceiver(broadcastReceiver, intentFilter);

        } //end of onCreate

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        public class recieveMessages extends BroadcastReceiver
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("TAG_", "message recieved");

                Bundle extras = intent.getExtras();
                Object[] pdusArray = (Object[]) extras.get("pdus");
                SmsMessage [] msgArray = new SmsMessage [pdusArray.length];
                String format = intent.getStringExtra("format");

                for(int i = 0; i < msgArray.length; i++)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        msgArray[i] = SmsMessage.createFromPdu((byte []) pdusArray[i], format);
                    }


                message = msgArray[0].getMessageBody();
                destNumber = msgArray[0].getOriginatingAddress();
                recievedMessage.setText("Message recieved: "+msgArray[0].getMessageBody());
                phoneNumber.setText("Number: "+msgArray[0].getOriginatingAddress());
                handler.postDelayed(getRunnable(), 5000);

                Log.d("TAG_", "msgBody: "+msgArray[0].getMessageBody());
                Log.d("TAG_", "msgAddress: "+msgArray[0].getOriginatingAddress());
            }
        } //end of recieveMessages

        public Runnable getRunnable() {
            Runnable runnable = new Runnable() {
                @Override
                public void run()
                {
                    smsManager = SmsManager.getDefault();
                    currentState.setText("Greeting State");

                    if (state == 1)
                    {
                        if (checkInput(message, "hi", "hey", "hello"))
                        {
                            sendMessage = "Hi welcome, I am IcecreamBot!";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        }

                        else if(checkInput(message, "is", "where", "who"))
                        {
                            sendMessage = "Yes, I am IcecreamBot";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        }

                        else if(checkInput(message, "how", "you", "doing"))
                        {
                            sendMessage = "I am good. How are you?";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        }

                        else if(checkInput(message, "want", "would", "need"))
                            state = 2;

                        else
                        {
                            sendMessage = wrongResponse;
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                    }

                    else if (state == 2)
                    {
                        currentState.setText("Ordering State");

                        if(checkInput(message, "want", "do", "will"))
                        {
                            sendMessage = "Would you like strawberry and sprinkles?";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        }

                        else if(checkInput(message, "would", "could", "might"))
                        {
                            sendMessage = "Would you like chocolate and oreos?";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "need", "must", "asap"))
                        {
                            sendMessage = "How about vanilla and fudge?";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "can i", "how", "get"))
                        {
                            state = 3;
                        }

                        else
                        {
                            sendMessage = wrongResponse;
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        /*sendMessage = messageGenerator(feelingsInput);
                        smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        sendMessage = "We have strawberry, vanilla, chocolate, cookie dough, and cookies n cream";
                        smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        sendMessage = "Which one would you like?";
                        smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);

                        state = 3;*/
                    }

                    else if (state == 3)
                    {
                        currentState.setText("Confirmation State");

                        if(checkInput(message, "vanilla", "chocolate", "strawberry"))
                        {
                            sendMessage = messageGenerator(feelingsInput)+ " Your order has been placed!";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "else", "something", "anything"))
                        {
                            sendMessage = "No, I only serve icecream";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "cookie", "cake", "mint"))
                        {
                            sendMessage = "I'm sorry I do not have that flavor right now";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "ok", "alright", "k"))
                        {
                            state = 4;
                        }

                        else
                        {
                            sendMessage = wrongResponse;
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }
                    }

                    else if (state == 4)
                    {
                        currentState.setText("Farewell State");

                        if(checkInput(message, "thanks", "cool", "great"))
                        {
                            sendMessage = messageGenerator(farewell);
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "that's", "fine", "got"))
                        {
                            sendMessage = "Sorry for the inconvenience! "+messageGenerator(byeInput);
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else if(checkInput(message, "understand", "sense", "okay"))
                        {
                            sendMessage = "Okay! See you later!";
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }

                        else
                        {
                            sendMessage = wrongResponse;
                            smsManager.sendTextMessage(destNumber, null, sendMessage, null, null);
                        }
                    }
                }

            };
            return runnable;
        } //end of runnable

        public boolean checkInput (String message, String a, String b, String c)
        {
            boolean res = false;
            if (message.toLowerCase().contains(a) || message.toLowerCase().contains(b) || message.toLowerCase().contains(c))
                res = true;

            return res;
        }

        public String messageGenerator(String [] array)
        {
            String message = "";
            int rand = (int)(Math.random()*3)+0;
            message = array[rand];
            return message;
        }
} //end of activity