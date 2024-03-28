package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stella.utils.loadSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class pantallaCrearCuenta extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email, password, user, repeatPassword;
    TextView emailErr, userErr, passwordErr, repeatPasswordErr;

    String regexPatternEmail = "^(?=.{1,32}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    String regexPatternUserName = "^[a-zA-Z0-9]{3,30}$";
    String regexPatternPassword = "^.{6,30}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantallacrearcuenta);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEditText);
        user = findViewById(R.id.userNameEditText);
        password = findViewById(R.id.passwordEditText);
        repeatPassword = findViewById(R.id.passwordRepeatEditText);
        emailErr = findViewById(R.id.emailErrText);
        userErr = findViewById(R.id.userErrText);
        passwordErr = findViewById(R.id.passwordErrText);
        repeatPasswordErr = findViewById(R.id.repeatPasswordErrText);
        auxControlEditTexts();



    }

    /**
     * El método "register" tiene la utilidad de registrar un nuevo usuario en FireBase.
     * Para ello, hace uso del email, usuario y contaseña.
     * @param view
     */

    public void register(View view){

        // Se obtiene el email, contraseña y nombre de usuario escritos por el usuario
        String emailText, passwordText, userText;
        emailText = String.valueOf(email.getText());
        passwordText = String.valueOf(password.getText());
        userText = String.valueOf(user.getText());


        // A continuación, se hace las comprobaciones pertinentes de email, usuario y contraseña

        if(TextUtils.isEmpty(emailText) || !Pattern.compile(regexPatternEmail).matcher(emailText).matches()){
            Toast.makeText(this, R.string.emailEmpty, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userText)){
            Toast.makeText(this, R.string.userEmpty, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(this, R.string.passwordEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Una vez pasadas las comprobaciones, se procede a crear el usuario en FireBase

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Se introduce el nombre del usuario por separado mediante el objeto UserProfileChangeRequest
                        UserProfileChangeRequest UPCR = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userText)
                                .build();
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        firebaseUser.updateProfile(UPCR);
                        firebaseUser.sendEmailVerification();
                        alertDialog();
                    }
                });

    }

    private void alertDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.newaccountdialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        Button button = dialog.findViewById(R.id.btn_closeNewAccDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                finish();
            }
        });
    }

    private void auxControlEditTexts(){

        email.setBackgroundResource(R.drawable.edtnormal);
        user.setBackgroundResource(R.drawable.edtnormal);
        password.setBackgroundResource(R.drawable.edtnormal);
        repeatPassword.setBackgroundResource(R.drawable.edtnormal);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Pattern.compile(regexPatternEmail).matcher(email.getText()).matches()){
                    email.setBackgroundResource(R.drawable.edtsuccess);
                    emailErr.setVisibility(View.INVISIBLE);
                } else if (TextUtils.isEmpty(String.valueOf(email.getText()))){
                    email.setBackgroundResource(R.drawable.edtnormal);
                    emailErr.setVisibility(View.INVISIBLE);
                }
                else{
                    email.setBackgroundResource(R.drawable.edterr);
                    emailErr.setVisibility(View.VISIBLE);
                }
            }
        });
        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Pattern.compile(regexPatternUserName).matcher(user.getText()).matches()){
                    user.setBackgroundResource(R.drawable.edtsuccess);
                    userErr.setVisibility(View.INVISIBLE);
                } else if (TextUtils.isEmpty(String.valueOf(user.getText()))){
                    user.setBackgroundResource(R.drawable.edtnormal);
                    userErr.setVisibility(View.INVISIBLE);
                } else{
                    user.setBackgroundResource(R.drawable.edterr);
                    userErr.setVisibility(View.VISIBLE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Pattern.compile(regexPatternPassword).matcher(password.getText()).matches()){
                    password.setBackgroundResource(R.drawable.edtsuccess);
                    passwordErr.setVisibility(View.INVISIBLE);
                } else if (TextUtils.isEmpty(String.valueOf(password.getText()))){
                    password.setBackgroundResource(R.drawable.edtnormal);
                    passwordErr.setVisibility(View.INVISIBLE);
                } else{
                    password.setBackgroundResource(R.drawable.edterr);
                    passwordErr.setVisibility(View.VISIBLE);
                }
            }
        });
        repeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String repPass = String.valueOf(repeatPassword.getText());
                String pass = String.valueOf(password.getText());

                if(repPass.equals(pass)){
                    repeatPassword.setBackgroundResource(R.drawable.edtsuccess);
                    repeatPasswordErr.setVisibility(View.INVISIBLE);
                } else if (TextUtils.isEmpty(String.valueOf(repeatPassword.getText()))){
                    repeatPassword.setBackgroundResource(R.drawable.edtnormal);
                    repeatPasswordErr.setVisibility(View.INVISIBLE);
                } else{
                    repeatPassword.setBackgroundResource(R.drawable.edterr);
                    repeatPasswordErr.setVisibility(View.VISIBLE);
                }

            }
        });
    }
}