package com.ildev.mobilit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.ildev.mobilit.utils.Validator.validateEmail;
import static com.ildev.mobilit.utils.Validator.validatePassword;

public class UserActivity extends AppCompatActivity {

    // Declaration of Variables
    private Button mButtonSignIn;
    private TextView mTextViewSignUp;
    private TextInputLayout mEmailWrapper, mPasswordWrapper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Getting the references to the UI Elements
        mButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        mTextViewSignUp = (TextView) findViewById(R.id.TextViewSignUp);
        mEmailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        mPasswordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        // Setting hints
        mEmailWrapper.setHint(getString(R.string.email));
        mPasswordWrapper.setHint(getString(R.string.password));

        // Setting SignIn Button ActionListener
        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); // Hiding the Keyboard

                // Getting Email and Password
                String email = mEmailWrapper.getEditText().getText().toString();
                String password = mPasswordWrapper.getEditText().getText().toString();

                // Setting Errors to null
                mEmailWrapper.setError(null);
                mPasswordWrapper.setError(null);

                // Validating the email and password
                if(!validateEmail(email) && !validatePassword(password) ){
                    // Invalid email and password
                    mEmailWrapper.setError("Invalid email");
                    mPasswordWrapper.setError("Invalid password");
                }else if(!validateEmail(email)){
                    // Invalid email
                    mEmailWrapper.setError("Invalid email");
                }else if(!validatePassword(password)){
                    // Invalid password
                    mPasswordWrapper.setError("Invalid password");
                }else{
                    // Valid email and password
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserActivity.this, new OnCompleteListener<AuthResult>(){

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    /**
                                     * If authentication fails, we display a message to the user.
                                     * Otherwise, if authentication succeds, the auth state listener will
                                     * be notified, and we do the logic to handle it
                                     */
                                    if(!task.isSuccessful()){
                                        // Failing in doing the Login
                                        // TODO: Task failing logic
                                    } else{
                                        // Succesfully done the Login
                                        // TODO: Task succesfully done logic
                                    }
                                }
                            });
                }
            }
        });
    }


    // Method that hides the Keyboard
    private void hideKeyboard(){
        // Check if no view has focus
        View view = getCurrentFocus(); // Get the view with focus or return null
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
