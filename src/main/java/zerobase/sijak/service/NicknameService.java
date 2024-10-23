package zerobase.sijak.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {

    // 형용사 배열
    private final String[] ADJECTIVES = {
            // 성격/태도/특징 관련
            "친절한", "동정심많은", "관대한", "낙천적인", "친근한", "정직한", "겸손한", "용감한", "인내심있는", "예의바른", "희망찬",
            "활기찬", "기쁜", "화난", "열정적인", "명량한", "빈둥거리는", "재잘거리는", "멋있는", "귀여운", "섬한", "뽐내는",
            "깜찍한", "날쌘", "듬직한", "노래부르는", "춤추는",

            // 능력 관련
            "지적인", "창의적인", "지혜로운", "분석적인", "부지런한", "재능있는", "집중력있는", "능숙한", "야망있는", "통찰력있는",

            // 감정/에너지 관련
            "즐거운", "신이난", "희망적인", "감사하는", "자신감있는", "만족한", "평온한", "행복한", "웃고있는", "날렵한", "열정적인", "이쁜",

            // 질감/형태 관련
            "물렁한", "폭신폭신한", "뾰족뾰족한", "부드러운", "부들부들한", "보들보들한", "따뜻한", "쫄깃한", "윤기나는",
            "촉촉한", "신선한", "부드러운", "매끄러운", "세련된", "독특한", "화려한", "세련된", "넉넉한", "달콤한",

            // 기타 긍정적인 특징
            "단정한", "유쾌한", "상냥한", "활달한", "꾸준한", "정돈된", "단호한", "설득력있는", "유연한", "반짝이는", "푸른", "복스러운"
    };

    // 명사 배열
    private final String[] NOUNS = {
            "황새", "해파리", "표범", "부엉이", "뱁새", "나무늘보", "카멜레온", "코알라", "타조", "펠리컨", "원앙", "참새", "고래",
            "두더지", "까치", "다람쥐", "오리", "악어", "하마", "담비", "래서팬더", "아기곰",

            "사자", "호랑이", "독수리", "상어", "판다", "여우", "늑대", "용", "곰", "매", "강아지", "고양이", "토끼", "햄스터",
            "앵무새", "거북이", "고슴도치", "물고기", "말", "돌고래", "펭귄", "코알라", "기린", "수달", "코끼리"
    };

    private final Random RANDOM = new Random();


    public String generate() {
        // 랜덤 형용사
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];

        // 랜덤 명사
        String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];

        // 랜덤 숫자
        int randomNumber = RANDOM.nextInt(10000);
        String formattedRandomNumber = String.format("%04d", randomNumber);

        return MessageFormat.format("{0}{1}#{2}", adjective, noun, formattedRandomNumber);
    }

}
