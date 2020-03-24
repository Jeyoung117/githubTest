package com.example.loginactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class IssueActivity extends AppCompatActivity {

    public static String SERVER_ADRESS = "http://141.223.83.30:8080";

    String bank_tran_id = "djiflsfe"; //은행거래고유번호
    int cntr_account_num = 1234; //약정 계좌 번호(이용기관 소유의 계좌번호를 의미)
    int fintech_use_num = 1234; //출금계좌핀테크이용번호
    int tran_amt = 1234; //거래 금액
   // int tran_dtime = 1234; //거래일시
   // String req_client_name = "홍길동"; //요청고객성명
    final String userId = getIntent().getStringExtra("userId");
    //핀테크이용번호, Access token은 어떻게 전달?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);


        final EditText chargeText = (EditText) findViewById(R.id.chargeText);
        Button btnCharge = (Button) findViewById(R.id.btnCharge);
        final Spinner spinner = (Spinner)findViewById(R.id.spinner1);

        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Bank, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); spinner.setAdapter(adapter);



        btnCharge.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String BankName = spinner.getSelectedItem().toString();
                //final String cgha = chargeText.getText().toString();
                final String amounts = chargeText.getText().toString();


                //final String account = accountNumber.getText().toString();


                EndorsementDB endorsementDB = realm.where(EndorsementDB.class).equalTo("userId", userId).findFirst();
                Log.d("Enrollment >>>>>>>> ", endorsementDB.getStrEndorsement() + " ################ " + BankName);
                Call<ResponseBody> m = IssueActivity.RetrofitServiceImplFactory.serverPost().createToken(userId, endorsementDB.getStrEndorsement(), BankName, fintech_use_num, tran_amt);
                m.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                        }
                        //BankName.setText("");
//                        fintech_use_num.setText("" + 100);
//                        amounts.setText("");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
            });
        };
    });

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
