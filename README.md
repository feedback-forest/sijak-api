# sijak-api

## Exception Code Number
|code|message|data|
|:--:|:-------:|:--:|
|OK|||
|0|success|Object|
|Lecture|||
|4000|크롤링 작업 도중 실패했습니다.|null|
|4001|강의 ID가 존재하지 않습니다.|null|
|User|||
|5000|로그인에 실패했습니다.|null|
|5001|유저 ID가 존재하지 않습니다.|null|
|5002|유저 email이 존재하지 않습니다.|null|
|5003|사용자 주소를 얻어오는 데 실패했습니다.|null|
|5004|해당 위도, 경도의 행정구역을 알 수 없습니다.|null|
|Nickname||
|6000|띄어쓰기 없이 2자 ~ 12자까지 가능해요.|null|
|6001|자음, 모음은 닉네임 설정 불가합니다.|null|
|6002|한글, 영문, 숫자만 입력해주세요.|null|
|6003|이미 사용중인 닉네임이에요.|null|
|Heart||
|7000|찜클래스 삭제에 실패했습니다.|null|
|7001|이미 찜을 해놓았습니다.|null|