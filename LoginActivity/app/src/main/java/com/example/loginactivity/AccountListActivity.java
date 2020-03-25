package com.example.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class AccountListActivity extends AppCompatActivity {

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";

    String[] bank_array = new String[30];
    String[] account_array = new String[30];
    EditText bankName, accountNumber;
    Button addAccount;
    TableRow Tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountlist);

        accountNumber = (EditText) findViewById(R.id.accountNumber);
        bankName = (EditText) findViewById(R.id.bankName);
        addAccount = (Button) findViewById(R.id.btnAddAcount);
        Tr = (TableRow)findViewById(R.id.tablerow1);

        Intent intent = getIntent();
        final String userId = intent.getExtras().getString("userId");

        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
        Call<BankAccount> m = RetrofitServiceImplFactory.serverPost().queryMyAccounts(userId);
        Call<BankAccount> m2 = RetrofitServiceImplFactory.serverPost().getBank(userId);


        m.enqueue(new Callback<BankAccount>() {
            @Override
            public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {
                if(response.isSuccessful()) {
                    Log.d("query my account", "success");
                    final BankAccount body = response.body();
                    accountNumber.setText(valueOf(body.getAccountNumber()));
                    Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BankAccount> call, Throwable t) {

            }
        });

        m2.enqueue(new Callback<BankAccount>() {
            @Override
            public void onResponse(Call<BankAccount> call, Response<BankAccount> response) {
                if(response.isSuccessful()) {
                    Log.d("query my bank", "success");
                    final BankAccount body = response.body();
                    bankName.setText(valueOf(body.getBank()));
                    Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BankAccount> call, Throwable t) {

            }
        });

        Tr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("click", "success");
            }

        });

    }

    public interface ServerPost {
        @FormUrlEncoded
        @POST("/assets/queryMyAccounts")
        Call<BankAccount> queryMyAccounts(@Field("userId") String userId);


        @FormUrlEncoded
        @POST("/assets/queryMyBank")
        Call<BankAccount> getBank(@Field("userId") String userId);

    }

    static class RetrofitServiceImplFactory {

        private static Retrofit getretrofit() {
            return new Retrofit.Builder()
                    .baseUrl(SERVER_ADRESS)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        public static AccountListActivity.ServerPost serverPost() {
            return getretrofit().create(AccountListActivity.ServerPost.class);
        }
    }

}
