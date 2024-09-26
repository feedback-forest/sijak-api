package zerobase.sijak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND("404", "페이지가 존재하지 않습니다.", "PAGE NOT FOUND"),
    LECTURE_ID_NOT_EXIST("404", "강의 ID가 존재하지 않습니다.", "LECTURE ID NOT EXIST"),
    INTER_SERVER_ERROR("500", "서버 오류입니다.", "INTER SERVER ERROR"),
    LOGIN_FAILED("500", "로그인에 실패했습니다.", "LOGIN FAILED"),
    EMAIL_NOT_EXIST("404", "유저 email이 존재하지 않습니다.", "EMAIL NOT EXIST");

    private String status;
    private String message;
    private String data;
}
