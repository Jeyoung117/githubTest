package com.example.loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";

    EditText userIdText;
    EditText myCash;
    EditText myDigitalCheck;
    EditText myBankName;
    EditText bankName;
    EditText accountNumber;
    EditText createAmount;
    EditText sendAmount;
    EditText receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIdText = (EditText) findViewById(R.id.userId);
        myCash = (EditText) findViewById(R.id.myCash);
        myDigitalCheck = (EditText) findViewById(R.id.myDigitalCheck);
        myBankName = (EditText) findViewById(R.id.myBankName);
        bankName = (EditText) findViewById(R.id.bankName);
        accountNumber = (EditText) findViewById(R.id.accountNumber);
        createAmount = (EditText) findViewById(R.id.createAmount);
        //sendAmount = (EditText) findViewById(R.id.sendAmount);
        //receiver = (EditText) findViewById(R.id.receiver);

        Button getMyMoney = (Button) findViewById(R.id.getMyMoney);
        Button connectAccount = (Button) findViewById(R.id.connectAccount);
        Button createDigitalCheck = (Button) findViewById(R.id.createDigitalCheck);
        //Button sendDigitalCheck = (Button) findViewById(R.id.sendDigitalCheck);
        Button getMyDigitalCheck = (Button) findViewById(R.id.getMyDigitalCheck);

        Intent intent = getIntent();
        final String userId = intent.getExtras().getString("userId");
        userIdText.setText("Welcome, " + userId);

        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        getMyMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String BankName = myBankName.getText().toString();
                Call<BankAccount> m = MainActivity.RetrofitServiceImplFactory.serverPost().queryMyAmounts(userId, BankName);

                m.enqueue(new Callback<BankAccount>() {

                    @Override
                    public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {

                        if (response.isSuccessful()) {
                            Log.d("query my amounts", "success");
                            final BankAccount body = response.body();
                            myCash.setText(valueOf(body.getAmounts()));
                            //myDigitalCheck.setText(valueOf(body.getDigitalCheck()));
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BankAccount> call, Throwable t) {

                    }
                });
            }
        });

        connectAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String bank = bankName.getText().toString();
                final String account = accountNumber.getText().toString();

                Call<ResponseBody> m = MainActivity.RetrofitServiceImplFactory.serverPost().connectAccount(userId, bank, account);
                m.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()) {
                            Log.d("Connect Success", bank + " , " + accountNumber);
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();

                            bankName.setText("");
                            accountNumber.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        createDigitalCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String bank = bankName.getText().toString();
                final String account = accountNumber.getText().toString();
                final String amounts = createAmount.getText().toString();

                EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
                Log.d("Enrollment >>>>>>>> ", endorsementDB.getStrEndorsement() + " ################ " + bank);
                Call<ResponseBody> m = MainActivity.RetrofitServiceImplFactory.serverPost().createToken(userId, endorsementDB.getStrEndorsement(), bank, account, amounts);

                m.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }

                        bankName.setText("");
                        accountNumber.setText("");
                        createAmount.setText("");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

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

        getMyDigitalCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
                Call<BankAccount> m = MainActivity.RetrofitServiceImplFactory.serverPost().queryMyDigitalCheck(userId, endorsementDB.getStrEndorsement());

                m.enqueue(new Callback<BankAccount>() {

                    @Override
                    public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {

                        if (response.isSuccessful()) {
                            Log.d("query my digital checks", "success");
                            final BankAccount body = response.body();
                            myDigitalCheck.setText(valueOf(body.getDigitalCheck()));
                            //myDigitalCheck.setText(valueOf(body.getDigitalCheck()));
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BankAccount> call, Throwable t) {

                    }
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

        /*
        sendDigitalCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tokenReceiver = receiver.getText().toString();
                final String amounts = sendAmount.getText().toString();

                EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
                Log.d("Enrollment >>>>>>>> ", endorsementDB.getStrEndorsement());

                Call<BankAccount> m = MainActivity.RetrofitServiceImplFactory.serverPost().send(userId, tokenReceiver, amounts, endorsementDB.getStrEndorsement());

                m.enqueue(new Callback<BankAccount>() {

                    @Override
                    public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {

                        if (response.isSuccessful()) {
                            Log.d("send my amounts", "success");
                            final BankAccount body = response.body();
                            myDigitalCheck.setText(valueOf(body.getDigitalCheck()));
                            //myDigitalCheck.setText(valueOf(body.getDigitalCheck()));
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BankAccount> call, Throwable t) {

                    }
                });
            }
        });

         */
    }

    public interface ServerPost {

        @FormUrlEncoded
        @POST("/assets/queryMyAmounts")
        Call<BankAccount> queryMyAmounts(@Field("userId") String userId, @Field("myBankName") String myBankName);

        @FormUrlEncoded
        @POST("/assets/queryMyDigitalCheck")
        Call<BankAccount> queryMyDigitalCheck(@Field("userId") String userId, @Field("enrollment") String enrollment);

        @FormUrlEncoded
        @POST("/assets/connectAccount")
        Call<ResponseBody> connectAccount(@Field("userId") String userId, @Field("bank") String bank, @Field("accountNumber") String accountNumber);

        @FormUrlEncoded
        @POST("/assets/mint")
        Call<ResponseBody> createToken(@Field("userId") String userId, @Field("enrollment") String enrollment, @Field("bank") String bank, @Field("accountNumber") String accountNumber, @Field("amounts") String amounts);

        @FormUrlEncoded
        @POST("/assets/send")
        Call<BankAccount> send(@Field("userId") String userId, @Field("receiver") String receiver, @Field("amounts") String amounts, @Field("enrollment") String enrollment);

    }

    static class RetrofitServiceImplFactory {

        private static Retrofit getretrofit() {
            return new Retrofit.Builder()
                    .baseUrl(SERVER_ADRESS)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        public static MainActivity.ServerPost serverPost() {
            return getretrofit().create(MainActivity.ServerPost.class);
        }
    }
}
