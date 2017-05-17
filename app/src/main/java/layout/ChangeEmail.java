package layout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import groupass.amc.chatroom.R;

import android.widget.Toast;


public class ChangeEmail extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText newEmail;
    private FirebaseAuth auth;


    public static ChangeEmail newInstance(String param1, String param2) {
        ChangeEmail fragment = new ChangeEmail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_email, container, false);
        Button button = (Button) v.findViewById(R.id.changeEmail);
        newEmail = (EditText) v.findViewById(R.id.new_email);
        button.setOnClickListener(this);
        return v;
    }


    @Override
    public void onClick(View v) {
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (v.getId()) {
            case R.id.changeEmail:
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to update email!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                }
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }
}
