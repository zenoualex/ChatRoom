package layout;

import android.net.Uri;
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

import groupass.amc.chatroom.R;
import android.widget.Toast;

public class ResetPassword extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText oldEmail;

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
        //get firebase auth instance
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button button = (Button) v.findViewById(R.id.send);
        oldEmail = (EditText) v.findViewById(R.id.old_email);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        switch (v.getId()) {
            case R.id.send:
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                }

                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
