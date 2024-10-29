package zerobase.sijak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Code {

    // Success
    OK(0, "success"),

    // Failed
    // Exception

    // Lecture Exception
    CRAWLING_FAILED(4000, "크롤링 작업 도중 실패했습니다."),
    LECTURE_ID_NOT_EXIST(4001, "강의 ID가 존재하지 않습니다."),

    // User Exception
    LOGIN_FAILED(5000, "로그인에 실패했습니다."),
    MEMBER_ID_NOT_EXIST(5001, "유저 ID가 존재하지 않습니다."),
    KAKAO_ID_NOT_EXIST(5002, "유저의 카카오 ID가 존재하지 않습니다."),
    ADDRESS_PROCESSING_FAILED(5003, "사용자 주소를 얻어오는 데 실패했습니다."),
    GEOLOCATION_NOT_EXIST(5004, "해당 위도, 경도의 행정구역을 알 수 없습니다."),
    INVALID_ACCESS_TOKEN(5005, "잘못된 ACCESS_JWT 서명입니다."),
    EXPIRED_ACCESS_TOKEN(5006, "만료된 ACCESS_JWT 토큰입니다."),
    INVALID_REFRESH_TOKEN(5007, "잘못된 REFRESH_JWT 서명입니다."),
    EXPIRED_REFRESH_TOKEN(5008, "만료된 REFRESH_JWT 토큰입니다."),
    ACCESS_TOKEN_UNAUTHORIZED(5009, "ACCESS_TOKEN: 유효한 자격증명을 제공하지 않습니다."),
    REFRESH_TOKEN_UNAUTHORIZED(5010, "REFRESH_TOKEN: 유효한 자격증명을 제공하지 않습니다."),
    ACCESS_TOKEN_UNMATCHED(5011, "ACCESS_TOKEN: 일치하지 않습니다."),
    REFRESH_TOKEN_UNMATCHED(5012, "REFRESH_TOKEN: 일치하지 않습니다."),
    UNSUPPORTED_ACCESS_TOKEN(5013, "지원되지 않는 JWT 토큰입니다."),
    WRONG_TYPE_ACCESS_TOKEN(5014, "JWT 토큰이 잘못되었습니다."),
    USER_UNAUTHORIZED(5015, "인증되지 않은 사용자입니다."),
    USER_FORBIDDEN(5016, "권한이 없는 사용자입니다."),

    // Nickname Exception
    INVALID_LENGTH_NICKNAME(6000, "띄어쓰기 없이 2자 ~ 12자까지 가능해요."),
    INVALID_CV_NICKNAME(6001, "자음, 모음은 닉네임 설정 불가합니다."),
    INVALID_CHARACTER_NICKNAME(6002, "한글, 영문, 숫자만 입력해주세요."),
    ALREADY_NICKNAME_EXIST(6003, "이미 사용중인 닉네임이에요."),

    // Heart Exception
    HEART_REMOVE_FAILED(7000, "찜클래스 삭제에 실패했습니다."),
    ALREADY_PUSH_HEART(7001, "이미 찜을 해놓았습니다.");

    private final Integer code;
    private final String message;
    private final String data = null;
}
