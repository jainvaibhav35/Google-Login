    package com.example.user.gplus_sample;

    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.Toast;

    import com.google.android.gms.auth.GoogleAuthException;
    import com.google.android.gms.auth.GoogleAuthUtil;
    import com.google.android.gms.auth.UserRecoverableAuthException;
    import com.google.android.gms.auth.api.Auth;
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
    import com.google.android.gms.auth.api.signin.GoogleSignInResult;
    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.api.GoogleApiClient;

    import java.io.IOException;


    /**
     * This class is used to get the login from
     * Google Plus .
     *
     * Donot forget to include google-services.json file after
     * registering Project on Google Developer Console.
     */


    public class GPlus_Login extends AppCompatActivity {

        GoogleApiClient  mGoogleApiClient;
        Button gplus_button;
        private static String TAG = GPlus_Login.class.getSimpleName();
        private static int REQUESTCODE = 100;


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();


            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , cfl )
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

             gplus_button = (Button) findViewById(R.id.gplus_button);

            gplus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, REQUESTCODE);
                }
            });

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == REQUESTCODE && resultCode == GPlus_Login.RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                new RetrieveTokenTask().execute(result.getSignInAccount().getEmail());
            }
        }

        GoogleApiClient.OnConnectionFailedListener cfl = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        };

        private void handleSignInResult(GoogleSignInResult result) {
            Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                gplus_button.setText(acct.getDisplayName() + " " + acct.getId());

            } else {
            }
        }


        /**
         *
         * This is used to get access token from
         * the account.
         *
         */


        private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String accountName = params[0];
                String scopes = "oauth2:profile email";
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), 100);
                } catch (GoogleAuthException e) {
                    Log.e(TAG, e.getMessage());
                }
                return token;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                gplus_button.setText("Token Value: " + s);
                Toast.makeText(GPlus_Login.this,s,Toast.LENGTH_LONG).show();
            }
        }

    }
