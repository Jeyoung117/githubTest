package com.kftc.openbankingsample2.biz.center_auth.auth.authorize;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.util.Utils;

import java.util.HashMap;

/**
 * 사용지인증 or 계좌등록확인 개선버전(CASE 1)
 */
public class CenterAuthAuthorizeCase1ChildFragment extends AbstractCenterAuthAuthorizeCaseFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private String uri;
    private String authorizeType;       // 사용자인증 or 계좌등록확인

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();
        uri = args.getString(CenterAuthConst.BUNDLE_KEY_URI, "");
        authorizeType = args.getString(CenterAuthConst.BUNDLE_KEY_OAUTH_TYPE, CenterAuthConst.BUNDLE_DATA_OAUTH_TYPE_AUTHORIZE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_authorize_case1, container, false);
        initView();
        return view;
    }

    void initView() {

        // 반환형태(고정)
        ((TextView) view.findViewById(R.id.tvResponseType)).setText("code");

        // 설정에서 저장한값 채워넣기
        ((TextView) view.findViewById(R.id.tvClientId)).setText(CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ID));
        ((TextView) view.findViewById(R.id.tvRedirectUri)).setText(CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_REDIRECT_URI));

        // 저장되어 있던 테이블 데이터를 화면에 채워넣기
        CenterAuthUtils.setEditTextFromSavedValue(view, R.id.auth2Case1FormTable);

        // scope 선택버튼 등
        super.initButton(view);

        // 사용자인증타입 구분
        ((TextView) view.findViewById(R.id.tvAuthType)).setText("0");

        // 색상선택
        super.initColor(view);

        // CASE1 실행
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // 화면에 있는 테이블 데이터를 앱에 저장하기
            CenterAuthUtils.saveFormData(view, R.id.auth2Case1FormTable);

            // parameter
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("response_type", ((TextView) view.findViewById(R.id.tvResponseType)).getText().toString().trim());
            paramMap.put("client_id", ((TextView) view.findViewById(R.id.tvClientId)).getText().toString().trim());
            paramMap.put("redirect_uri", ((TextView) view.findViewById(R.id.tvRedirectUri)).getText().toString().trim());
            paramMap.put("scope", ((EditText) view.findViewById(R.id.et_ANW_SCOPE)).getText().toString().trim());
            paramMap.put("client_info", ((EditText) view.findViewById(R.id.et_ANW_CLIENT_INFO)).getText().toString().trim());
            paramMap.put("state", ((EditText) view.findViewById(R.id.et_ANW_STATE)).getText().toString().trim());
            paramMap.put("auth_type", ((TextView) view.findViewById(R.id.tvAuthType)).getText().toString().trim());
            paramMap.put("lang", ((EditText) view.findViewById(R.id.et_ANW_LANG)).getText().toString().trim());
            paramMap.put("edit_option", (( EditText) view.findViewById(R.id.et_ANW_EDIT_OPTION)).getText().toString().trim());
            paramMap.put("bg_color", ((EditText) view.findViewById(R.id.et_ANW_BG_COLOR)).getText().toString().trim());
            paramMap.put("txt_color", ((EditText) view.findViewById(R.id.et_ANW_TXT_COLOR)).getText().toString().trim());
            paramMap.put("btn1_color", ((EditText) view.findViewById(R.id.et_ANW_BTN1_COLOR)).getText().toString().trim());
            paramMap.put("btn2_color", ((EditText) view.findViewById(R.id.et_ANW_BTN2_COLOR)).getText().toString().trim());
            String parameters = "?" + Utils.convertMapToQuerystring(paramMap);
            Log.d("#####", uri + "?" + Utils.convertMapToString(paramMap));

            // API 호출 웹뷰
            args.putString(CenterAuthWebViewFragment.URL_TO_LOAD, uri + parameters); // 호출 URL 전달
            goNext();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // 상단 타이틀/서브타이틀
        if (authorizeType.equals(CenterAuthConst.BUNDLE_DATA_OAUTH_TYPE_AUTHORIZE)) {
            setTitle(getString(R.string.fragment_id_auth_authorize));
            view.findViewById(R.id.tvSubTitle).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvSubTitle2).setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.fragment_id_auth_authorize_account));
            view.findViewById(R.id.tvSubTitle).setVisibility(View.GONE);
            view.findViewById(R.id.tvSubTitle2).setVisibility(View.VISIBLE);
        }
    }

    private void goNext() {
        startFragment(CenterAuthWebViewFragment.class, args, R.string.fragment_id_auth_authorize);
    }
}
