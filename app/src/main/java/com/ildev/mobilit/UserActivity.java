package com.ildev.mobilit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.ildev.mobilit.utils.Validator.validateEmail;
import static com.ildev.mobilit.utils.Validator.validateName;
import static com.ildev.mobilit.utils.Validator.validatePassword;

public class UserActivity extends AppCompatActivity {

    // Declaration of Variables
    private Button mButtonSignIn, mButtonSignUp;
    private CallbackManager callbackManager;
    private ConstraintLayout mSignInCL, mSignUpCL;
    private CollectionReference mCollectionRef;
    private GoogleSignInClient mGoogleSignInClient; // Google's Sign In Client
    private ImageButton  mButtonGoogle, mButtonFacebook, mButtonTwitter;
    private LoginManager mFbLoginManager; // Facebook's Login Manager
    private TextView mTextViewSignUp, mTextViewSignIn, mTextViewForgotPassword;
    private TextInputLayout mEmailWrapper, mPasswordWrapper;   //Singin Variables
    private TextInputLayout mNameWrapper, mLastNameWrapper, mEmailRegisterWrapper, mPasswordRegisterWrapper; //SingUp Variables
    private TwitterAuthClient mTwitterAuthClient; // Twitter's Authentication Client
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private final static String TAG = "UserActivity";
    private final static String NAME_KEY = "name";
    private final static String LNAME_KEY = "lname";
    private  final static String EMAIL_KEY = "email";
    private static final int RC_SIGN_IN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Twitter.initialize(this); // Twitter's kit initialization

        // ConstraintLayout References
        mSignInCL = (ConstraintLayout) findViewById(R.id.signInConstraintLayout);
        mSignUpCL = (ConstraintLayout) findViewById(R.id.signUpConstraintLayout);
        // Button References
        mButtonSignIn = (Button) findViewById(R.id.ButtonSignIn);
        mButtonSignUp = (Button) findViewById(R.id.ButtonSignUp);
        // ImageButtons References
        mButtonGoogle = (ImageButton) findViewById(R.id.imageButtonGoogle);
        mButtonFacebook = (ImageButton) findViewById(R.id.imageButtonFacebook);
        mButtonTwitter = (ImageButton) findViewById(R.id.imageButtonTwitter);
        //SingIn references
        mEmailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        mPasswordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        // SingUp references
        mNameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        mLastNameWrapper = (TextInputLayout) findViewById(R.id.lastNameWrapper);
        mEmailRegisterWrapper = (TextInputLayout) findViewById(R.id.emailRegisterWrapperSignUp);
        mPasswordRegisterWrapper = (TextInputLayout) findViewById(R.id.passwordWrapperSignUp);
        // TextView References
        mTextViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        mTextViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        mTextViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        // Database references
        mCollectionRef = FirebaseFirestore.getInstance().collection("users");
        // Google's stuff
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build(); // Google Sign In Options requesting Email
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); // Google Sign In Client
        // Setting hints
        mEmailWrapper.setHint(getString(R.string.email));
        mPasswordWrapper.setHint(getString(R.string.password));
        // Facebook's Stuff
        LoginManager fbLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        mFbLoginManager = LoginManager.getInstance(); // TODO: Verify the existence of a better approach, if exists
        // Twitter's Stuff
        mTwitterAuthClient = new TwitterAuthClient();


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
                                        Log.e("UserActivity", "Error in authenticating the email " + email);
                                        // Cleaning up the Password EditTextField
                                        mPasswordWrapper.getEditText().setText(null);
                                        // Showing message to the user
                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Error in the email/password combination", Snackbar.LENGTH_SHORT);
                                        snack.show();
                                    } else{
                                        // Succesfully done the Login
                                        Log.v("UserActivity", "Success in authenticating the email " + email);
                                        // Provisory Message
                                        // TODO: Clean up the Snackbar message when the change betweens activities are completed
                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"User " +email+ " successfully authenticated", Snackbar.LENGTH_SHORT);
                                        snack.show();
                                    }
                                }
                            });
                }
            }
        });
        // Setting SingUp Button
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
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
                                        // Task failed in creating the new user, so we clean up the Password
                                        mPasswordRegisterWrapper.getEditText().setText(null);
                                        // And show a message to the user
                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Email already registered", Snackbar.LENGTH_LONG);
                                        snack.show();
                                    }else{
                                        // Task succeed in creating the new user
                                        FirebaseUser user = auth.getCurrentUser();
                                        String Uid = user.getUid(); // User ID
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
                                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Succesfully registered, thanks :D", Snackbar.LENGTH_LONG);
                                                        snack.show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {

                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Error writing the document", e);
                                                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Unable to register the user: " + e.getMessage(), Snackbar.LENGTH_LONG);
                                                        snack.show();
                                                    }
                                        });
                                    }
                                }
                            });
                }

            }
        });

        // Setting Touch Listeners
        mButtonSignIn.setOnTouchListener(onTouchListener);
        mButtonSignUp.setOnTouchListener(onTouchListener);

        /**
         * Google's Button OnCLickListener
         */
        mButtonGoogle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Getting SignIn Intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        /**
         * Facebook's Login
         */
        // Login Manager
        mFbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // Some code here
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Loged in with Facebook", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onCancel() {
                // Canceled in Login with Facebook
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Login with Facebook was canceled", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onError(FacebookException error) {
                // Error in performing the Login with Facebook
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error in performing the login " +error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        // Facebook Button
        mButtonFacebook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mFbLoginManager.logInWithReadPermissions(UserActivity.this, Arrays.asList("user_photos", "email", "user_birthday"));
            }
        });

        /**
         * Twitter's Button OnClickListener
         */
        mButtonTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mTwitterAuthClient.authorize(UserActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        // Success
                        // TODO: Uns bagulhos aqui que to com preguiça, mas ok, ja deve estar funfando
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Unable to login with Twitter: " + exception.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });

            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        // Checks if user is signed in (non-null) and update UI accordiingly
        FirebaseUser currentUser = auth.getCurrentUser();
        // Check for GoogleSignInAccount, if the user is already signed in, the result will be not-null
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Check if user is signed in with Facebook (non-null Access Token)
        AccessToken tokenFB = AccessToken.getCurrentAccessToken();
        // Check if user is signed in with Twitter (non-null User Token)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN){
            /**
             * The Task returned from this call is always completed, no need to attach a listener.
             */
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
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

    // Touch Listener to the view
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            /**
             * We override the onTouch method to the view that has this
             * TouchListener. Since we're gonna apply it to change the colors of buttons,
             * then we're gonna use ACTION_DOWN and ACTION_DOWN identifiers
             */
            Button btn = (Button) view; // We cast the view to a button
            if(motionEvent.getAction() == motionEvent.ACTION_DOWN){
                btn.setBackgroundResource(R.drawable.btn_rectangle_dark);
            }else if(motionEvent.getAction() == motionEvent.ACTION_UP){
                btn.setBackgroundResource(R.drawable.btn_rectangle_light);
            }
            return false;
        }
    };
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

    // Function that handles with the task of the GoogleSignInAccount
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            // updateUI(account);
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Succesfully loged in with Google", Snackbar.LENGTH_LONG);
            snack.show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Unable to login with Google", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }
}
