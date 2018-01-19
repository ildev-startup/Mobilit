package com.ildev.mobilit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    // Declaration of Variables
    TextView mAppTextView, mTextViewSignUp;
    EditText mEditTextPassword, mEditTextEmail;
    Button mButtonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Getting the references to the UI Elements
        mEditTextEmail = (EditText) findViewById(R.id.EditTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.EditTextPassword);
        mButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        mTextViewSignUp = (TextView) findViewById(R.id.TextViewSignUp);

        // Setting the widths properly
        setWidths();
    }

    private void setWidths(){
        int sizeTextEmail = mEditTextEmail.getWidth(); // Size of the TextEmail
        // TODO: Very which of the solutions is more correct
//        mButtonSignUp.getLayoutParams().width = sizeTextEmail;
//        mButtonSignUp.requestLayout();
        mButtonSignIn.setWidth(sizeTextEmail);
    }

}
