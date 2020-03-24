package com.example.loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.LoginActivity.R;

import static java.lang.String.valueOf;
public class MenuActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button BalancePage = (Button) findViewById(R.id.btnBalancePage);
        Button IssuePage = (Button) findViewById(R.id.btnIssuePage);
        Button AccountListPage = (Button) findViewById(R.id.btnAccountList);
        //Button AccountCancelPage = (Button) findViewById(R.id.btnAccountCancel);

        final String userId = getIntent().getStringExtra("userId");


        BalancePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent accountbalanceIntetent = new Intent(MenuActivity.this, AccountBalanceActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);

                accountbalanceIntetent.putExtra("userId", userId);
                MenuActivity.this.startActivity(accountbalanceIntetent);
                setContentView(R.layout.activity_account_balance);
            }
        });

        IssuePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent issueuIntetent = new Intent(MenuActivity.this, IssueActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);

                issueuIntetent.putExtra("userId", userId);
                MenuActivity.this.startActivity(issueuIntetent);
                setContentView(R.layout.activity_issue);
            }
        });

        AccountListPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent accountlistIntetent = new Intent(MenuActivity.this, AccountBalanceActivity.class);
                //final Intent mainIntetent = new Intent(LoginActivity.this, MainActivity.class);

                accountlistIntetent.putExtra("userId", userId);
                MenuActivity.this.startActivity(accountlistIntetent);
                setContentView(R.layout.activity_accountlist);
            }
        });



    }
}
