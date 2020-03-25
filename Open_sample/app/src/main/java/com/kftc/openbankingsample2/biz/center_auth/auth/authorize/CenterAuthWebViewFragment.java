package com.kftc.openbankingsample2.biz.center_auth.auth.authorize;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.util.Utils;

import java.util.Map;


/**
 * 사용자인증 개선버전에서 공통적으로 사용하는 WebView Fragment
 */
public class CenterAuthWebViewFragment extends AbstractCenterAuthMainFragment {

    public static final String URL_TO_LOAD = "urlToLoad";

    // context
    private Context context;

    // view
    private View view;
    private WebView webView;

    // data
    private Bundle args;
    private String urlToLoad;
    private Map<String, String> headerMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        urlToLoad = args.getString(URL_TO_LOAD, "");
        headerMap = (Map<String, String>) args.getSerializable("headerMap");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_webview, container, false);
        initView();
        return view;
    }

    void initView() {
        EditText etUrl = view.findViewById(R.id.etUrl);
        etUrl.setText(urlToLoad);
        Button btnFold = view.findViewById(R.id.btnFold);
        btnFold.setOnClickListener(v -> {
            if("펼침".equals(btnFold.getText().toString())){
                etUrl.setSingleLine(false);
                btnFold.setText("접힘");
            }else{
                etUrl.setSingleLine(true);
                btnFold.setText("펼침");
            }
        });

        // 사용자이름 필드를 url encoding 한다 (G/W에서 디코딩 해 주는 설정 있음)
        if(headerMap != null){
            headerMap.put("Kftc-Bfop-UserName", Utils.urlEncode(headerMap.get("Kftc-Bfop-UserName")));
        }

        webView = view.findViewById(R.id.webView);
        loadUrlOnWebView();
    }

    void loadUrlOnWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // 로그인을 위해 필요
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("UTF-8");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                showAlert("확인", message, null, (dialog, which) -> result.confirm());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                /*
                 * AuthorizationCode 발급이 완료된 이후에, 해당 코드를 사용하여 토큰발급까지의 흐름을 UI상에 보여주기 위해서 추가한 코드 예시
                 * 이용기관에서는 redirect uri 에 해당하는 웹페이지에서 에러처리를 해야한다.
                 */
                String callbackUrl = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_REDIRECT_URI);
                if(url.startsWith(callbackUrl)){

                    // error 코드가 있을경우 에러처리
                    String error = Utils.getParamValFromUrlString(url, "error");
                    if (!error.isEmpty()) {
                        String errorDesc = Utils.getParamValFromUrlString(url, "error_description");
                        if (errorDesc.isEmpty()) {
                            errorDesc = "알 수 없는 오류가 발생하였습니다.";
                        }
                        showAlert(error, Utils.urlDecode(errorDesc), url, (dialog, which) ->
                                /*startFragment(CenterAuthFragment.class, null, R.string.fragment_id_center_auth)*/ onBackPressed());
                        return true;
                    }

                    // error 코드가 없으면 토큰발급으로 이동
                    String code = Utils.getParamValFromUrlString(url, "code");
                    String scope = Utils.getParamValFromUrlString(url, "scope");
                    String client_info = Utils.getParamValFromUrlString(url, "client_info");

                    // 요청시 이용기관이 세팅한 state 값을 그대로 전달받는 것으로, 이용기관은 CSRF 보안위협에 대응하기 위해 요청 시의 state 값과 응답 시의 state 값을 비교해야 함
                    String state = Utils.getParamValFromUrlString(url, "state");

                    args.putString("code", code);
                    args.putString("scope", scope);
                    args.putString("client_info", client_info);
                    args.putString("state", state);
                    goNext();
                    return true;
                }

                view.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideProgress();
            }
        });
        webView.loadUrl(urlToLoad, headerMap);

    }

    void goNext() {
        startFragment(CenterAuthTokenRequestFragment.class, args, R.string.fragment_id_token);
    }
}
