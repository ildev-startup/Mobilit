package com.ildev.mobilit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.ildev.mobilit.utils.Validator.validateEmail;
import static com.ildev.mobilit.utils.Validator.validateName;
import static com.ildev.mobilit.utils.Validator.validatePassword;

public class UserActivity extends AppCompatActivity {

    // Declaration of Variables
    private Button mButtonSignIn, mButtonSingUp;
    private ConstraintLayout mSignInCL, mSignUpCL;
    private CollectionReference mCollectionRef;
    private TextView mTextViewSignUp, mTextViewSignIn, mTextViewForgotPassword;
    private TextInputLayout mEmailWrapper, mPasswordWrapper;   //Singin Variables
    private TextInputLayout mNameWrapper, mLastNameWrapper, mEmailRegisterWrapper, mPasswordRegisterWrapper; //SingUp Variables
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private final static String TAG = "UserActivity";
    private final static String NAME_KEY = "name";
    private final static String LNAME_KEY = "lname";
    private  final static String EMAIL_KEY = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Getting the references to the UI Elements
        mButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        mButtonSingUp = (Button) findViewById(R.id.ButtonSignUp);
        mSignInCL = (ConstraintLayout) findViewById(R.id.signInConstraintLayout);
        mSignUpCL = (ConstraintLayout) findViewById(R.id.signUpConstraintLayout);
        mTextViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        mTextViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        mTextViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        //SingIn references
        mEmailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        mPasswordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        // SingUp references
        mNameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        mLastNameWrapper = (TextInputLayout) findViewById(R.id.lastNameWrapper);
        mEmailRegisterWrapper = (TextInputLayout) findViewById(R.id.emailRegisterWrapperSignUp);
        mPasswordRegisterWrapper = (TextInputLayout) findViewById(R.id.passwordWrapperSignUp);
        // Database references
        mCollectionRef = FirebaseFirestore.getInstance().collection("users");

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
                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Error in doing the login", Snackbar.LENGTH_SHORT);
                                        snack.show();
                                    } else{
                                        // Succesfully done the Login
                                        // TODO: Task succesfully done logic
                                        Log.v("UserActivity", "Success in authenticating the email " + email);
                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"User " +email+ " successfully authenticated", Snackbar.LENGTH_SHORT);
                                        snack.show();
                                    }
                                }
                            });
                }
            }
        });
        // Setting SingUp Button
        mButtonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //Hiding the Keyboard

                //Getting name, last name, email and password
                final String name  = mNameWrapper.getEditText().getText().toString();
                final String last_name = mLastNameWrapper.getEditText().getText().toString();
                final String email = mEmailRegisterWrapper.getEditText().getText().toString();
                String password = mPasswordRegisterWrapper.getEditText().getText().toString();

                //Setting Errors to null
                mNameWrapper.setError(null);
                mLastNameWrapper.setError(null);
                mEmailRegisterWrapper.setError(null);
                mPasswordRegisterWrapper.setError(null);

                if (!validateName(name) || !validateName(last_name) || !validateEmail(email) || !validatePassword(password)) {
                    if (!validateName(name)) {
                        mNameWrapper.setError("Invalid Name");
                    }
                    if (!validateName(last_name)) {
                        mLastNameWrapper.setError("Invalid Last Name");
                    }
                    if (!validateEmail(email)) {
                        mEmailRegisterWrapper.setError("Invalid Email");
                    }
                    if (!validatePassword(password)) {
                        mPasswordRegisterWrapper.setError("Invalid Password");
                    }
                }
                else{
                    // Valid Name, Last Name, Email, Password
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    /**
                                     * If success in creating the new User, we register it into the Database.
                                     * Otherwise, it failed and we must deal with it
                                     */
                                    if(!task.isSuccessful()){
                                        // Task failed in creating the new user

                                    }else{
                                        // Task succeed in creating the new user
                                        FirebaseUser user = auth.getCurrentUser();
                                        String Uid = user.getUid();
                                        // Data to save
                                        Map<String, Object> dataToSave = new HashMap<String, Object>();
                                        dataToSave.put(NAME_KEY, name);
                                        dataToSave.put(LNAME_KEY, last_name);
                                        dataToSave.put(EMAIL_KEY, email);
                                        mCollectionRef.document(Uid)
                                                .set(dataToSave)
                                                .addOnSuccessListener(new OnSuccessListener<Void>(){

                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "User document has been saved");
                                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Succesfully registered, thanks :D", Snackbar.LENGTH_SHORT);
                                                        snack.show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {

                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Error writing the document", e);
                                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Unable to register the user: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                                                        snack.show();
                                                    }
                                        });
                                    }
                                }
                            });
                }

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        // Checks if user is signed in (non-null) and update UI accordiingly
        FirebaseUser currentUser = auth.getCurrentUser();

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
