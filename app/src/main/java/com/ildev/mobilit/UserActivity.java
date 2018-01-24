package com.ildev.mobilit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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
    private ConstraintLayout mSignInCL, mSignUpCL;
    private TextView mTextViewSignUp, mTextViewSignIn, mTextViewForgotPassword;
    private TextInputLayout mEmailWrapper, mPasswordWrapper;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Getting the references to the UI Elements
        mButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        mSignInCL = (ConstraintLayout) findViewById(R.id.signInConstraintLayout);
        mSignUpCL = (ConstraintLayout) findViewById(R.id.signUpConstraintLayout);
        mTextViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        mTextViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        mTextViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
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
                final String email = mEmailWrapper.getEditText().getText().toString();
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
                                        Log.e("UserActivity", "Error in authenticating the email " + email);
                                    } else{
                                        // Succesfully done the Login
                                        // TODO: Task succesfully done logic
                                        Log.v("UserActivity", "Success in authenticating the email " + email);
                                    }
                                }
                            });
                }
            }
        });

    }

    /**
     * OnClick Method to either SignIn and SignUp textViews
     */
    public void onClickSigns(View view) {
        switch (view.getId()){
            case R.id.textViewSignUp:
                Log.i("Succes", "Setting SignUp view");
                setSignUp();
                break;
            case R.id.textViewSignIn:
                Log.i("Succes", "Setting SignIn view");
                setSignIn();
                break;
            default:
                break;
        }
    }

    // Method that set visible the SignUp UI
    private void setSignUp(){
        // SignIn ConstraintLayout Gone
        mSignInCL.setVisibility(View.GONE);
        //Setting top margin
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTextViewForgotPassword.getLayoutParams();
        params.setMargins(0, convert_dp_to_px(488), 0, 0);
        mTextViewForgotPassword.requestLayout();
        // SignUp ConstraintLayout Visible
        mSignUpCL.setVisibility(View.VISIBLE);
    }

    // Method that set visible the SignIn UI
    private void setSignIn(){
        // SignUp ConstraintLayout Gone
        mSignUpCL.setVisibility(View.GONE);
        // Setting top margin
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTextViewForgotPassword.getLayoutParams();
        params.setMargins(0, convert_dp_to_px(316), 0, 0);
        mTextViewForgotPassword.setLayoutParams(params);
        mTextViewForgotPassword.requestLayout();
        // SignIn ConstraintLayout Visible
        mSignInCL.setVisibility(View.VISIBLE);
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

    // Converter of dp to pixels
    private int convert_dp_to_px(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
                        .getDisplayMetrics());
    }
}
