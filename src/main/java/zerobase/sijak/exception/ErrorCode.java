package zerobase.sijak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    CRAWLING_FAILED(500, "크롤링 작업 도중 실패했습니다.", "CRAWLING FAILED"),
    ADDRESS_PROCESSING_FAILED(500, "사용자 주소를 얻어오는 데 실패했습니다.", "ADDRESS PROCESSING FAILED"),
    INTER_SERVER_ERROR(500, "서버 오류입니다.", "INTER SERVER ERROR"),

    NOT_FOUND(404, "페이지가 존재하지 않습니다.", "PAGE NOT FOUND"),
    LECTURE_ID_NOT_EXIST(404, "강의 ID가 존재하지 않습니다.", "LECTURE ID NOT EXIST"),
    MEMBER_ID_NOT_EXIST(404, "유저 ID가 존재하지 않습니다.", "MEMBER ID NOT EXIST"),
    ALREADY_PUSH_HEART(404, "이미 찜을 해놓았습니다.", "ALREADY PUSH HEART"),
    HEART_REMOVE_FAILED(404, "찜클래스 삭제에 실패했습니다.", " HEART REMOVE FAILED"),
    INVALID_NICKNAME(404, "띄어쓰기 없이 2자 ~ 12자까지 가능해요.", " INVALID NICKNAME"),
    ALREADY_NICKNAME_EXIST(404, "이미 사용중인 닉네임이에요. 다른 닉네임을 적어주세요.", " ALREADY NICKNAME EXIST"),
    GEOLOCATION_NOT_EXIST(404, "해당 위도, 경도의 행정구역을 알 수 없습니다.", " GEOLOCATION NOT EXIST"),

    LOGIN_FAILED(401, "로그인에 실패했습니다.", "LOGIN FAILED"),
    EMAIL_NOT_EXIST(401, "유저 email이 존재하지 않습니다.", "EMAIL NOT EXIST");

    private int status;
    private String message;
    private String data;
}
