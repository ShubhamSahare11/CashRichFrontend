package com.cashrich.coinrich;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cashrich.coinrich.databinding.SignupScreenBinding;
import com.cashrich.coinrich.utils.OkHttpUtil;
import com.cashrich.coinrich.utils.Utility;
import com.cashrich.coinrich.vo.ResponseVo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private SignupScreenBinding binding;
    boolean inCorrectUserName = true;
    boolean inCorrectPassword = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SignupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextInputEditText userNameInput = (TextInputEditText) findViewById(R.id.signUpUsernameInputText);
        checkUserNameLength(userNameInput);
        TextInputEditText passwordInput = (TextInputEditText) findViewById(R.id.signUpPasswordInputText);
        checkPasswordLength(passwordInput);
        TextInputEditText firstNameInput = (TextInputEditText) findViewById(R.id.signUpFirstNameInputText);
        TextInputEditText lastNameInput = (TextInputEditText) findViewById(R.id.signUpLastNameInputText);


        TextView loginLink = (TextView) findViewById(R.id.loginLink);
        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            setContentView(binding.getRoot());
            finish();

        });

        Button signupButton = (Button) findViewById(R.id.signUpButton);
        signupButton.setOnClickListener(view -> {
            if (inCorrectUserName || inCorrectPassword) {
                Snackbar.make(view, "Errors in the Details", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    String userName = Objects.requireNonNull(userNameInput.getText()).toString();
                    String password = Utility.hashPassword(Objects.requireNonNull(passwordInput.getText()).toString());
                    String firstName = Objects.requireNonNull(firstNameInput.getText()).toString();
                    String lastName = Objects.requireNonNull(lastNameInput.getText()).toString();
                    JSONObject jsonObject = createSignUpJsonPayload(userName, password, firstName, lastName);
                    String url = getResources().getString(R.string.baseUrl).concat(getResources().getString(R.string.signup));
                    Map<String, String> headers = Utility.getOriginHeader();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        ResponseVo responseVo = OkHttpUtil.cashRichPostRequest(jsonObject, url, headers);
                        // Process the result or update UI if needed
                        runOnUiThread(() -> {
                            if (responseVo.getVal() == 1) {
                                Snackbar.make(view, responseVo.getResponse().concat(" Please login again."), Snackbar.LENGTH_LONG).show();
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                }, 3000);
                            } else {
                                Snackbar.make(view, responseVo.getResponse(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private JSONObject createSignUpJsonPayload(String userName, String password, String firstName, String lastName) {
        JSONObject object = new JSONObject();
        try {
            object.put(getResources().getString(R.string.userName), userName);
            object.put(getResources().getString(R.string.password), password);
            object.put(getResources().getString(R.string.firstName), firstName);
            object.put(getResources().getString(R.string.lastName), lastName);
            return object;
        } catch (JSONException e) {
            Log.e("StringToJson", "Error occurred while converting field values to Json");
            throw new RuntimeException("Error Occurred");
        }
    }

    @Override
    public void onBackPressed() {
        // Perform your desired action here

        // If you want to go back to the previous activity, you can call super.onBackPressed()
        finish();
    }


    private void checkUserNameLength(TextInputEditText textInput) {
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 4) {
                    inCorrectUserName = true;
                    textInput.setError("Minimun:".concat(String.valueOf(4)));
                    return;
                }
                inCorrectUserName = false;
                textInput.setError(null);
            }
        });
    }

    private void checkPasswordLength(TextInputEditText textInput) {
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 8) {
                    inCorrectPassword = true;
                    textInput.setError("Minimun:".concat(String.valueOf(8)));
                } else {
                    if (Utility.isValidPassword(editable.toString())) {
                        inCorrectPassword = false;
                        textInput.setError(null);
                        return;
                    }
                    textInput.setError("Must have 1 upper, 1 lower, 1 digit and 1 special character");
                    inCorrectPassword = true;

                }
            }
        });
    }

}