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
    EMAIL_NOT_EXIST(5002, "유저 email이 존재하지 않습니다."),
    ADDRESS_PROCESSING_FAILED(5003, "사용자 주소를 얻어오는 데 실패했습니다."),
    GEOLOCATION_NOT_EXIST(5004, "해당 위도, 경도의 행정구역을 알 수 없습니다."),

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
