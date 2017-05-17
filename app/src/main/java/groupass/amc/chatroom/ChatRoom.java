package groupass.amc.chatroom;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Vibrator; // New Import for Vibration
import android.widget.Toast;


public class ChatRoom extends AppCompatActivity {

    private EditText input_msg;
    private TextView chat_conversation;
    private DatabaseReference root;
    private String temp_key;

    final MainActivity ad = new MainActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        Button btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);
        Button btn_attach = (Button) findViewById(R.id.button3);

        String room_name = getIntent().getExtras().get("room_name").toString();
        this.setTitle(" Room - " + room_name);
        root = FirebaseDatabase.getInstance().getReference().child(room_name);


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_msg.getText().length() < 1) {
                    Toast.makeText(ChatRoom.this, "Please add some Text", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", ad.showName());
                    map2.put("msg", input_msg.getText().toString());
                    message_root.updateChildren(map2);
                    input_msg.getText().clear();
                }
            }
        });
        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });


        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        Uri contactUri = data.getData();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor c = getContentResolver()
                .query(contactUri, projection, null, null, null);
        c.moveToFirst();
        int name = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int number = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        String number1 = c.getString(number);
        String name1 = c.getString(name);
        input_msg.setText("Number: " + number1 + " Name:" + name1);
        Toast.makeText(this, name1 + " has number " + number1, Toast.LENGTH_LONG).show();


    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Iterator i = dataSnapshot.getChildren().iterator();
        int myColor = Color.BLUE;
        while (i.hasNext()) {

            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            if (chat_user_name == ad.getName()) {
                chat_conversation.append(chat_user_name + " : " + chat_msg + " \n");
            } else {
                chat_conversation.append(chat_user_name + " : " + chat_msg + " \n");
                mVibrator.vibrate(300); // On click Vibrate
            }
        }


    }


}