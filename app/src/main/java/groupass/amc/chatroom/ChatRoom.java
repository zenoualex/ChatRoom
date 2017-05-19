package groupass.amc.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
    private String message_key;

    final MainActivity ad = new MainActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        //Declaring Variables etc.
        Button btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);
        Button btn_attach = (Button) findViewById(R.id.button3);
        //Asking permitions to enter Phones Contact information
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        String room_name = getIntent().getExtras().get("room_name").toString();
        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        //Works with Mapping the first input will be the name and second will be the message. Before all that it is pushing a uinique Key for each message and user.
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_msg.getText().length() < 1) {
                    Toast.makeText(ChatRoom.this, "Please add some Text", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    message_key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference message_root = root.child(message_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", ad.showName());
                    map2.put("msg", input_msg.getText().toString());
                    message_root.updateChildren(map2);
                    input_msg.getText().clear();
                }
            }
        });
        //Generating an action when the Attack button is pressed. The action is to open the contact.
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
    //While the contact menu is open, the person is able to pick one contact and then it pastes to the input field
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode != Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] rcontact = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor c = getContentResolver()
                        .query(contactUri, rcontact, null, null, null);
                c.moveToFirst();
                int contact_name = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int contact_number = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String contact_number1 = c.getString(contact_number);
                String contact_name1 = c.getString(contact_name);
                input_msg.setText("Number: " + contact_number1 + " Name:" + contact_name1);
                Toast.makeText(this, contact_name1 + " has number " + contact_number1, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, " This Device has no Contacs ", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    //It load all previus Conversation in each room. Also Updates the chat with all new messages using dataSnapshots
    //Firebase uses Snapshots to show its data.
    //And with a loop we display all messages based on the Unique Keys
    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            if (chat_user_name.equals( ad.getName())) {
                Spannable Chatme = new SpannableString(chat_user_name + " : " + chat_msg + " \n");
                Chatme.setSpan(new ForegroundColorSpan(Color.BLUE), 0, chat_user_name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                chat_conversation.append(Chatme);

            } else {
                chat_conversation.append( chat_user_name + " : " + chat_msg + " \n");
                mVibrator.vibrate(200);
            }
        }
    }

}