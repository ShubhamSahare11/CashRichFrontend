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

import com.cashrich.coinrich.databinding.LoginScreenBinding;
import com.cashrich.coinrich.utils.OkHttpUtil;
import com.cashrich.coinrich.utils.Utility;
import com.cashrich.coinrich.vo.ResponseVo;
import com.cashrich.coinrich.vo.SessionVo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private LoginScreenBinding binding;
    boolean inCorrectUserName = true;
    boolean inCorrectPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String sessionId = ((SessionVo) getApplicationContext()).getSessionId();
        Long validTill = ((SessionVo) getApplicationContext()).getValidTill();
        if (null != sessionId && new Date().getTime() < validTill) {
            startActivity(new Intent(LoginActivity.this, Welcome.class));
            finish();
        }

        TextInputEditText userNameInput = (TextInputEditText) findViewById(R.id.loginUsernameInputText);
        checkUserNameLength(userNameInput);
        TextInputEditText passwordInput = (TextInputEditText) findViewById(R.id.loginPasswordInputText);
        checkPasswordLength(passwordInput);

        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(view -> {
            if (inCorrectUserName || inCorrectPassword) {
                Snackbar.make(view, "Errors in the Details", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    String userName = userNameInput.getText().toString();
                    String password = Utility.hashPassword(passwordInput.getText().toString());
//                        String userName = "asdf";
//                        String password = "asdf";
                    JSONObject jsonObject = createLoginJsonPayload(userName, password);
                    String url = getResources().getString(R.string.baseUrl).concat(getResources().getString(R.string.login));
                    Map<String, String> headers = Utility.getOriginHeader();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        ResponseVo responseVo = OkHttpUtil.cashRichPostRequest(jsonObject, url, headers);
                        // Process the result or update UI if needed
                        runOnUiThread(() -> {

                            if (responseVo.getVal() == 1) {
                                JSONObject jsonResponse = getJsonFromResponse(responseVo);
                                if (null == jsonResponse) {
                                    Snackbar.make(view, "Internal Error Occurred", Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                                createSession(jsonResponse);
                                Snackbar.make(view, "SUCCESS!", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(LoginActivity.this, Welcome.class));
                                    finish();
                                }, 2000);
                            } else {
                                Snackbar.make(view, responseVo.getResponse(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


        TextView signUpLink = (TextView) findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            setContentView(binding.getRoot());
            finish();

        });
    }

    private JSONObject getJsonFromResponse(ResponseVo responseVo) {
        try {
            return new JSONObject(responseVo.getResponse());
        } catch (JSONException e) {
            Log.e("StringToJson", "Error occurred while converting response to Json object");
            responseVo.setResponse("Internal Error");
        }
        return null;
    }

    private void createSession(JSONObject object) {
        String sessionId = object.optString("session_id");
        Long validTill = object.optLong("valid_till");
        ((SessionVo) getApplicationContext()).setSessionId(sessionId);
        ((SessionVo) getApplicationContext()).setValidTill(validTill);
        ((SessionVo) getApplicationContext()).setFullName(object.optString("fullName"));
    }

    private JSONObject createLoginJsonPayload(String userName, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put(getResources().getString(R.string.userName), userName);
            object.put(getResources().getString(R.string.password), password);
            return object;
        } catch (JSONException e) {
            Log.e("StringToJson", "Error occurred while converting field values to Json");
            throw new RuntimeException("Error Occurred");
        }
    }

    @Override
    public void onBackPressed() {
        // Call finish() to exit the application
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
                } else {
                    inCorrectUserName = false;
                    textInput.setError(null);
                }
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