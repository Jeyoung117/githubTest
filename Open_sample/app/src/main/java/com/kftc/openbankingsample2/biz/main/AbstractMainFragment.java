package com.kftc.openbankingsample2.biz.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.self_auth.SelfAuthConst;
import com.kftc.openbankingsample2.common.data.AccessToken;
import com.kftc.openbankingsample2.common.data.BankAccount;
import com.kftc.openbankingsample2.common.data.ResMsg;
import com.kftc.openbankingsample2.common.http.KmProgressBar;
import com.kftc.openbankingsample2.common.util.Utils;
import com.kftc.openbankingsample2.common.util.view.KmDialogDefault;
import com.kftc.openbankingsample2.common.util.view.onKeyBackPressedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 모든 프래그먼트의 추상 클래스
 */
public abstract class AbstractMainFragment extends Fragment implements onKeyBackPressedListener {

    // context
    private Context context;
    protected MainActivity activity;

    // progress
    private KmProgressBar progressBar;

    // listener
    protected interface OnClickListener {
        void onClick(String selectedItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();

        // 네이게이션 메뉴버튼 안보여주기
        lockNavi(true);

        // 툴바 보여주기
        setToolbarVisible(true);

        // 툴바 타이틀 설정
        setTitle(getTag());

        // 세팅 아이콘 보여주기
        showSetting(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 메인화면에서는 뒤로가기 버튼 제거
        if (getFragmentManager() != null) {
            Fragment f = getFragmentManager().findFragmentById(R.id.flContent);
            if (f instanceof HomeFragment) {
                setArrowVisible(false);
            } else {
                setArrowVisible(true);
            }
        }
    }

    // 네비게이션 메뉴버튼 보여주기/감추기
    public void lockNavi(boolean enableLock) {
        activity.lockNavi(enableLock);
    }

    // 툴바 보여주기/감추기
    public void setToolbarVisible(boolean isVisible) {
        activity.setToolbarVisible(isVisible);
    }

    // 타이틀 설정
    public void setTitle(String title) {
        activity.setTitle(title);
    }

    // 세팅 아이콘 보여주기/감추기
    public void showSetting(boolean enable) {
        activity.showSetting(enable);
    }

    // 백버튼 보여주기/감추기
    public void setArrowVisible(boolean bool) {
        activity.setArrowVisible(bool);
    }

    // fragment 시작
    public void startFragment(@NonNull Class fragmentClass, Bundle args, @StringRes int tagResId) {
        activity.startFragment(fragmentClass, args, tagResId);
    }

    // fragment 시작
    public void startFragment(@NonNull Class fragmentClass, Bundle args, String TAG_FRAGMENT, boolean replace, boolean keep) {
        activity.startFragment(fragmentClass, args, TAG_FRAGMENT, replace, keep);
    }

    // child fragment 시작
    public void startChildFragment(@NonNull Class fragmentClass, Bundle args) {
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Timber.e(e);
            return;
        }

        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(args);
        fragmentManager.beginTransaction().commitAllowingStateLoss();
    }

    // 통신처리(성공후 실행할 리스너)
    public interface NetListener {
        void onSuccess(String responseJson);
    }

    // 통신처리
    protected Callback<Map> handleResponse() {
        return handleResponse(null, null, true, null);
    }

    // 통신처리
    protected Callback<Map> handleResponse(NetListener listener) {
        return handleResponse(null, null, true, listener);
    }

    // 통신처리
    protected Callback<Map> handleResponse(String key, String desc) {
        return handleResponse(key, desc, true, null);
    }

    // 통신처리
    protected Callback<Map> handleResponse(String key, String desc, NetListener listener) {
        return handleResponse(key, desc, true, listener);
    }

    // 통신처리(key: 응답에서 추출하여 경고창에 보여줄 key, desc: 경고창에 보여줄 설명, enableAlert: 경고창을 띄울지 여부, listener: 성공시 실행할 로직)
    protected Callback<Map> handleResponse(String key, String desc, boolean enableAlert, NetListener listener) {
        return new Callback<Map>() {
            @Override
            public void onResponse(@NonNull Call<Map> call, @NonNull Response<Map> response) {
                hideProgress();

                // http ok(200) 아닐경우
                if (!response.isSuccessful()) {
                    handleNotSuccessful(response);
                    return;
                }

                // http ok(200) 일경우
                handleSuccessful(response, key, desc, enableAlert, listener);
            }

            @Override
            public void onFailure(@NonNull Call<Map> call, @NonNull Throwable t) {
                hideProgress();
                showAlert("통신실패", "서버 접속에 실패하였습니다.", t.getMessage());
            }
        };
    }

    // 통신처리
    protected void handleNotSuccessful(Response<Map> response) {
        ResponseBody errorBody = response.errorBody();
        if (errorBody == null) {
            showAlert("HTTP 에러코드 : " + response.code(), response.message());
            return;
        }

        try {
            String errorJson = errorBody.string();
            ResMsg resMsg = new Gson().fromJson(errorJson, ResMsg.class);
            showAlert("통신 에러코드 : " + resMsg.getRsp_code(), resMsg.getRsp_message(), errorJson);
        } catch (IOException e) {
            showAlert("오류", "알 수 없는 오류가 발생하였습니다.");
        }
    }

    // 통신처리
    protected void handleSuccessful(Response<Map> response, String key, String desc, boolean enableAlert, NetListener listener) {
        String responseJson = new Gson().toJson(response.body());

        // TODO: 2019-09-09 토큰유효시간이 지난 오류일경우, 개발자는 refresh token 을 사용해서 신규 access token을 발급받는 절차를 수행햐야함.

        // json 안에 있는 응답코드를 확인
        ResMsg resMsg = new Gson().fromJson(responseJson, ResMsg.class);
        if (!resMsg.isSuccess()) {

            // 오류인 경우에도 user_seq_no 가 있으면 저장해둔다.
            String userSeqNo = Utils.getValueFromJson(responseJson, "user_seq_no");
            Utils.saveData(CenterAuthConst.CENTER_AUTH_USER_SEQ_NO, userSeqNo);
            Utils.saveData(SelfAuthConst.SELF_AUTH_USER_SEQ_NO, userSeqNo);

            showAlert("API 에러코드 : " + resMsg.getRsp_code(), resMsg.getRsp_message(), responseJson);
            return;
        }

        // 사용자인증의 경우 다른 메뉴에서 access_token 을 사용할 수 있도록 저장.
        saveAuthData(responseJson);

        if (!enableAlert && listener != null) {
            listener.onSuccess(responseJson);
        } else {

            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(desc)) {

                // 추출하여 보여주고자 하는 key 값이 있을경우에는 경고창으로 보여준다.
                JsonObject json = new JsonParser().parse(responseJson).getAsJsonObject();
                JsonElement jsonElement = json.get(key);
                String value = "-";
                if (jsonElement != null) {
                    value = jsonElement.getAsString();
                }

                // 금액이면 쉼표와 '원', 건수이면 '건'을 붙여준다.
                if (key.contains("_amt")) {
                    value = Utils.moneyForm(value) + "원";
                } else if (key.contains("_cnt")) {
                    value = Utils.moneyForm(value) + "건";
                }

                if (listener == null) {
                    showAlert("정상", desc + ": " + value, responseJson);
                } else {
                    showAlert("정상", desc + ": " + value, responseJson,
                            (dialog, which) -> listener.onSuccess(responseJson));
                }
            } else if (!TextUtils.isEmpty(desc)) {
                if (listener == null) {
                    showAlert("정상", desc, responseJson);
                } else {
                    showAlert("정상", desc, responseJson,
                            (dialog, which) -> listener.onSuccess(responseJson));
                }
            } else {
                if (listener == null) {
                    showAlert("정상", responseJson);
                } else {
                    showAlert("정상", responseJson, null,
                            (dialog, which) -> listener.onSuccess(responseJson));
                }
            }
        }
    }

    // 토큰저장은 센터인증, 자체인증을 나눠서 하위 클래스에서 저장
    abstract protected void saveAuthData(String responseJson);

    // 경고창
    protected void showAlert(String title, String msg) {
        showAlert(title, msg, "");
    }

    // 경고창
    protected void showAlert(String title, String msg, String detailMsg) {
        showAlert(title, msg, detailMsg, null);
    }

    // 경고창
    protected void showAlert(String title, String msg, String detailMsg, DialogInterface.OnClickListener listener) {
        showAlert(title, msg, detailMsg, "확인", listener, null, null);
    }

    // 경고창
    protected void showAlert(String title, String msg, String detailMsg,
                             String positiveBtnName, DialogInterface.OnClickListener positiveListener,
                             String negativeBtnName, DialogInterface.OnClickListener negativeListener) {
        activity.closeSoftKeypad();

        KmDialogDefault dialog = new KmDialogDefault(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setDetailMessage(detailMsg);
        if (!TextUtils.isEmpty(positiveBtnName)) dialog.setPositiveButton("확인", positiveListener);
        if (!TextUtils.isEmpty(negativeBtnName)) dialog.setNegativeButton("취소", negativeListener);
        if (TextUtils.isEmpty(positiveBtnName) && TextUtils.isEmpty(negativeBtnName)) dialog.setPositiveButton("확인",  null);
        dialog.setCancelable(false);
        dialog.show();
    }

    // 경고창(토큰선택)
    protected void showAlertToken(ArrayAdapter<AccessToken> adapter, AdapterView.OnItemClickListener listener) {
        activity.closeSoftKeypad();

        if (adapter == null || adapter.getCount() <= 0) {
            showAlert("알림", "저장된 access token 이 없습니다.");
            return;
        }

        KmDialogDefault dialog = new KmDialogDefault(context);
        dialog.setTitle("사용하려는 access token을 선택하세요.");
        dialog.setAdapter(adapter, listener);
        dialog.setNegativeButton("취소", null);
        dialog.setCancelable(false);
        dialog.show();
    }

    // scope 멀티 선택창
    protected void showScopeDialog(EditText etScope, String[] scopeList) {
        ArrayList<String> selectedItemList = new ArrayList<String>() {
            @Override
            public String toString() {
                int size = size();
                if (size == 0) {
                    return "";
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    builder.append(get(i));
                    builder.append(" ");
                }
                return builder.toString().trim();
            }
        };

        showAlertScope(scopeList, (parent, view, position, id) -> {

            // 선택했을때
            boolean isChecked = ((CheckedTextView) view).isChecked();
            String scope = scopeList[position];
            if (isChecked) {
                selectedItemList.add(scope);
            } else {
                selectedItemList.remove(scope);
            }
        },(dialog, which) -> {

            // 확인버튼 눌렀을때
            if (etScope != null) {
                etScope.setText(selectedItemList.toString());
            }
        });
    }

    // 경고창(scope 선택) - 멀티선택
    protected void showAlertScope(String[] scopeList, AdapterView.OnItemClickListener multiChoiceListener,
                                  DialogInterface.OnClickListener listener) {
        activity.closeSoftKeypad();

        KmDialogDefault dialog = new KmDialogDefault(context);
        dialog.setTitle("서비스 구분을 선택하세요.");
        dialog.setMultiChoiceItems(scopeList, null, multiChoiceListener);
        dialog.setPositiveButton("확인", listener);
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }

    // scope 싱글 선택창
    protected void showScopeSingleDialog(EditText etScope, String[] scopeList, AbstractMainFragment.OnClickListener listener) {

        final String[] selectedItem = Arrays.copyOf(scopeList, scopeList.length);

        showAlertSingleScope(scopeList, (parent, view, position, id) -> {

            // 선택했을때
            boolean isChecked = ((CheckedTextView) view).isChecked();
            if (isChecked) {
                selectedItem[0] = scopeList[position];
            }

        },(dialog, which) -> {

            // 확인버튼 눌렀을때
            if (etScope != null) {
                etScope.setText(selectedItem[0]);
            }

            if (listener != null) {
                listener.onClick(selectedItem[0]);
            }
        });
    }

    // 경고창(scope 선택) - 싱글선택
    protected void showAlertSingleScope(String[] scopeList, AdapterView.OnItemClickListener singleChoiceListener,
                                  DialogInterface.OnClickListener listener) {
        activity.closeSoftKeypad();

        KmDialogDefault dialog = new KmDialogDefault(context);
        dialog.setTitle("서비스 구분을 선택하세요.");
        dialog.setSingleChoiceItems(scopeList, 0, singleChoiceListener);
        dialog.setPositiveButton("확인", listener);
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }

    // 경고창(계좌선택)
    protected void showAlertAccount(ArrayAdapter<BankAccount> adapter, AdapterView.OnItemClickListener listener) {
        activity.closeSoftKeypad();

        if (adapter == null || adapter.getCount() <= 0) {
            showAlert("알림", "저장된 계좌정보가 없습니다.");
            return;
        }

        KmDialogDefault dialog = new KmDialogDefault(context);
        dialog.setTitle("사용하려는 계좌를 선택하세요.");
        dialog.setAdapter(adapter, listener);
        dialog.setNegativeButton("취소", null);
        dialog.setCancelable(false);
        dialog.show();
    }

    // 프로그래스바
    protected void showProgress() {
        if (progressBar == null) {
            progressBar = new KmProgressBar(context);
        }
        progressBar.show();
    }

    protected void hideProgress() {
        if (progressBar != null) {
            progressBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        activity.onDefaultBackPressed();
    }
}
