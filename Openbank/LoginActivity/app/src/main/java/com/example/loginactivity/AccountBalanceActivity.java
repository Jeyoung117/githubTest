package com.example.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.LoginActivity.R;

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

public class AccountBalanceActivity extends AppCompatActivity {

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        final Realm realm = Realm.getDefaultInstance();
        final String userId = getIntent().getStringExtra("userId");

        EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
        Call<BankAccount> m = MainActivity.RetrofitServiceImplFactory.serverPost().queryMyDigitalCheck(userId, endorsementDB.getStrEndorsement());


        Button btnSendToken = (Button) findViewById(R.id.btnSendToken);
        Button btnCharge = (Button) findViewById(R.id.btnCharge);
        final EditText totalBalance = (EditText) findViewById(R.id.totalBalance);

        m.enqueue(new Callback<BankAccount>() {

            @Override
            public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {

                if (response.isSuccessful()) {
                    Log.d("query my digital checks", "success");
                    final BankAccount body = response.body();
                    totalBalance.setText(valueOf(body.getDigitalCheck()));
                }
            }

            @Override
            public void onFailure(Call<BankAccount> call, Throwable t) {

            }
        });

        btnSendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent issueuIntetent = new Intent(AccountBalanceActivity.this, IssueActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);

                 issueuIntetent.putExtra("userId", userId);
                AccountBalanceActivity.this.startActivity(issueuIntetent);
                setContentView(R.layout.activity_issue);
            }
        });


        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent issueuIntetent = new Intent(AccountBalanceActivity.this, IssueActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);

                // issueuIntetent.putExtra("userId", userId);
                AccountBalanceActivity.this.startActivity(issueuIntetent);
                setContentView(R.layout.activity_issue);
            }
        });


    };

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
        Call<ResponseBody> createToken(@Field("userId") String userId, @Field("enrollment") String enrollment, @Field("bank") String bank, @Field("accountNumber") int accountNumber, @Field("amounts") int amounts);

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

        public static IssueActivity.ServerPost serverPost() {
            return getretrofit().create(IssueActivity.ServerPost.class);
        }
    }
}
