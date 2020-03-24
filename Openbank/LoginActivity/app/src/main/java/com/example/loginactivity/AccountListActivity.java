package com.example.loginactivity;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.LoginActivity.R;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AccountListActivity extends AppCompatActivity {

    String[] bank_array = new String[30];
    String[] account_array = new String[30];
    EditText bankname, account;

    Intent intent = getIntent();
    final String userId = intent.getExtras().getString("userId");

    Realm.init(this);
    final Realm realm = Realm.getDefaultInstance();

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountlist);


        EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
        Call<ResponseBody> m = RetrofitServiceImplFactory.serverPost().queryMyAccounts(userId);
    }

    public interface ServerPost {

        @FormUrlEncoded
        @POST("/assets/queryMyAmounts")
        Call<BankAccount> queryMyAmounts(@Field("userId") String userId, @Field("myBankName") String myBankName);

        @FormUrlEncoded
        @POST("/assets/queryMyAmccounts")
        Call<BankAccount> queryMyAccounts(@Field("userId") String userId);

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
