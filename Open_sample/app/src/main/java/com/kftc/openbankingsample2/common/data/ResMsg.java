package com.kftc.openbankingsample2.common.data;

/**
 * 응답메시지
 */
public class ResMsg {

    String rsp_code;
    String rsp_message;
    String raw;

    public String getRsp_code() {
        return rsp_code == null ? "" : rsp_code;
    }

    public void setRsp_code(String rsp_code) {
        this.rsp_code = rsp_code;
    }

    public String getRsp_message() {
        return rsp_message;
    }

    public void setRsp_message(String rsp_message) {
        this.rsp_message = rsp_message;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public boolean isSuccess() {

        // 사용자인증의 경우 정상이면 rsp_code 값이 존재하지 않는다.
        // 사용자인증 외 API는 정상이면 A0000
        return "".equals(getRsp_code()) || "A0000".equals(getRsp_code());
    }
}
