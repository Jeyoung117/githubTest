package com.example.loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static java.lang.String.valueOf;

public class RegisterActivity extends AppCompatActivity {

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";
    EditText idText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        TextView registerButton = (TextView) findViewById(R.id.createDigitalCheck);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent mainIntetent = new Intent(RegisterActivity.this, LoginActivity.class);

                final String userId = idText.getText().toString();
                final String userPasswd = passwordText.getText().toString();

                Call<Data> m = RegisterActivity.RetrofitServiceImplFactory.serverPost().sendName(userId, userPasswd);

                m.enqueue(new Callback<Data>() {

                    @Override
                    public void onResponse(Call<Data> call, Response<Data> response) {

                        if(response.isSuccessful()) {
                            final Data body = response.body();
                            //EndorsementApplication endorsementApplication = new EndorsementApplication();
                            //endorsementApplication.onCreate();
                            //endorsementApplication.setEndorsement(userId, body);

                            //EndorsementDB endorsementDB = endorsementApplication.getEndorsement(userId);
                            System.out.println(body.getStrEnrollment());

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    EndorsementDB endorsementDB = new EndorsementDB(userId, body.getStrEnrollment());
//                                    EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
//                                    endorsementDB.setStrEndorsement(body.getStrEnrollment());
                                    realm.copyToRealm(endorsementDB);
                                }
                            });

                            EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
                            System.out.println(" >> " + endorsementDB.getStrEndorsement());
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }

                        RegisterActivity.this.startActivity(mainIntetent);
                        setContentView(R.layout.activity_login);
                    }

                    @Override
                    public void onFailure(Call<Data> call, Throwable t) {

                    }

                    /*
                    @Override
                    public void onResponse(Response<Message> response, Retrofit retrofit) {
                        final Message message = response.body();
                        Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                     */
                });

            }
        });

        /*

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://141.223.121.141:8080/assets/signup");

                ArrayList<NameValuePair> nameValues =
                        new ArrayList<NameValuePair>();

                try {
                    nameValues.add(new BasicNameValuePair(
                            "userId", URLDecoder.decode(idText.getText().toString(), "UTF-8")));
                    nameValues.add(new BasicNameValuePair(
                            "userPasswd", URLDecoder.decode(passwordText.getText().toString(), "UTF-8")));

                    post.setEntity(
                            new UrlEncodedFormEntity(
                                    nameValues, "UTF-8"));

                    Log.d("MyTag", "######################################");
                } catch (UnsupportedEncodingException ex) {
                    Log.e("Insert Log", ex.toString());
                }

                try {
                    HttpResponse response = client.execute(post);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

         */
    }

    public interface ServerPost {

        @FormUrlEncoded
        @POST("/assets/signUp")
        Call<Data> sendName(@Field("userId") String userId, @Field("userPasswd") String userPasswd);
    }

    static class RetrofitServiceImplFactory {

        private static Retrofit getretrofit() {
            return new Retrofit.Builder()
                    .baseUrl(SERVER_ADRESS)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        public static ServerPost serverPost() {
            return getretrofit().create(ServerPost.class);
        }
    }

}
