package com.example.loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LoginActivity.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {

   // public static String SERVER_ADRESS = "http://141.223.83.30:8080";
    EditText idText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        TextView registerButton = (TextView) findViewById(R.id.createDigitalCheck);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent menuIntetent = new Intent(LoginActivity.this, MenuActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);
                //LoginActivity.this.startActivity(mainIntetent);

//                final String userId = idText.getText().toString();
//                final String userPasswd = passwordText.getText().toString();
//
//                menuIntetent.putExtra("userId", userId);
                LoginActivity.this.startActivity(menuIntetent);
                setContentView(R.layout.activity_menu);


                //Call<ResponseBody> m = LoginActivity.RetrofitServiceImplFactory.serverPost().sendName(userId, userPasswd);
//                m.enqueue(new Callback<ResponseBody>() {
//
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                        if(response.isSuccessful()) {
//                            Log.d(userId, "Login Success");
//                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
//
//                            menuIntetent.putExtra("userId", userId);
//                            //mainIntetent.putExtra("userId", userId);
//                            LoginActivity.this.startActivity(menuIntetent);
//                            //LoginActivity.this.startActivity(mainIntetent);
//                            setContentView(R.layout.activity_menu);
//                            //setContentView(R.layout.activity_main);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntetent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(mainIntetent);
            }
        });
    }

//    public interface ServerPost {
//
//        @FormUrlEncoded
//        @POST("/assets/oauth/token")
//        Call<ResponseBody> sendName(@Field("userId") String userId, @Field("userPasswd") String userPasswd);
//    }
//
//    static class RetrofitServiceImplFactory {
//
//        private static Retrofit getretrofit() {
//            return new Retrofit.Builder()
//                    .baseUrl(SERVER_ADRESS)
//                    .addConverterFactory(GsonConverterFactory.create()).build();
//        }
//
//        public static LoginActivity.ServerPost serverPost() {
//            return getretrofit().create(LoginActivity.ServerPost.class);
//        }
//    }
}
