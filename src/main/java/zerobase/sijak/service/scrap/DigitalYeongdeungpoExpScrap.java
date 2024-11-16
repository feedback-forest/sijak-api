package zerobase.sijak.service.scrap;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.repository.LectureRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DigitalYeongdeungpoExpScrap {

    private final LectureRepository lectureRepository;

    //@Scheduled(fixedRate = 10000000)
    public void scrapDigitalYeongdeungpoExp() throws CustomException {
        try {
            String name = "", time = "", price = "", href = "", startDate = "", endDate = "", tel = "1566-2892";
            int capacity = 1, lId = -1, tId = -1, cId = -1;
            LocalDateTime deadline = LocalDateTime.now();
            log.info("deadline : {}", deadline);
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--remote-allow-origins=*");
            //options.addArguments("--headless");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
            String year = String.valueOf(LocalDateTime.now().getYear());
            String month = String.valueOf(LocalDateTime.now().getMonthValue());
            String URL = "https://didong.kr/reservation/date/0/2?year=" + year + "&month=" + month;

            driver.get(URL);
            List<WebElement> trs = driver
                    .findElements(By.cssSelector("#main > section.section8-2.section12-1 > div > div.calendar-board-wrap > div.inner-wrap > div.le-wrap.calendar-wrap > div.calendar-body > table > tbody > tr"));

            for (int i = 1; i < trs.size() - 1; i++) {
                List<WebElement> tds = trs.get(i).findElements(By.cssSelector("td"));
                for (WebElement td : tds) {
                    if (StringUtils.isEmpty(td.getText()) || td.getText().contains("예약불가"))
                        continue;
                    log.info(td.getText());
                    int day = Integer.parseInt(td.getText().split("\n")[0]);
                    WebElement div = td.findElement(By.tagName("div"));
                    ((ChromeDriver) driver).executeScript("arguments[0].click()", div);
                    List<WebElement> lis = driver.findElements(By.cssSelector("#resultWrap > div.scroll-wrap > ul > li"));
                    List<String> itemList = new ArrayList<>();
                    for (WebElement li : lis) {
                        log.info(li.getText());
                        itemList.add(li.getText());
                    }
                    log.info("size: {}", itemList.size());
                    for (String item : itemList) {
                        if (item.contains("휴관"))
                            continue;
                        log.info("item : {}", (Object) item.split("\n"));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        String[] items = item.split("\n");
                        name = items[0];
                        time = items[1];
                        int person = Integer.parseInt(items[2]);
                        capacity = Integer.parseInt(items[4]);
                        startDate = LocalDateTime
                                .of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), day, 0, 0, 0)
                                .toString().substring(0, 10);
                        endDate = startDate;
                        price = "0원";
                        deadline = LocalDateTime.parse(endDate + "T00:00:00", formatter);
                        String englishDay = LocalDateTime.now().getDayOfWeek().toString();
                        String dayOfWeek = DayOfWeek.valueOf(englishDay).getKoreanDay();
                        href = URL;
                        log.info("week: {}", DayOfWeek.valueOf(englishDay).getKoreanDay());
                        log.info("deadline : {}", deadline);
                        boolean status = person < capacity;

                        expiredClassStatusToFalse();
                        if (alreadySavedUserJudge(name, startDate, time, person))
                            continue;

                        log.info("없는 클래스 저장");
                        log.info("status: {}", status);
                        Lecture lecture = Lecture.builder()
                                .name(name)
                                .time(time)
                                .price(price)
                                .total(-1)
                                .certification("")
                                .dayOfWeek(dayOfWeek)
                                .target("")
                                .tel(tel)
                                .description("")
                                .location("")
                                .need("")
                                .textBookName("")
                                .textBookPrice("")
                                .thumbnail("")
                                .capacity(capacity)
                                .deadline(deadline)
                                .link(href)
                                .startDate(startDate)
                                .endDate(endDate)
                                .division("원데이 클래스")
                                .view(0)
                                .status(status)
                                .latitude(37.491580)
                                .longitude(126.899705)
                                .centerName("서울디지털동행플라자 서남센터(영등포)")
                                .address("서울특별시 영등포구 디지털로37나길 21")
                                .build();

                        lectureRepository.save(lecture);
                        log.info("==========");
                    }
                }
            }
            log.info("Crawling Success");
        } catch (Exception e) {
            throw new CustomException(Code.CRAWLING_FAILED);
        }
    }

    private void expiredClassStatusToFalse() {
        String centerName = "서울디지털동행플라자 서남센터(영등포)";
        String date = LocalDateTime.now().toString().substring(0, 10);
        lectureRepository.updateStatusToFalseForExpiredClasses(date, centerName);
    }

    private boolean alreadySavedUserJudge(String name, String startDate, String time, int person) {
        String centerName = "서울디지털동행플라자 서남센터(영등포)";
        Optional<Lecture> lecture = lectureRepository.findByNameAndCenterNameAndStartDateAndTime(name, centerName, startDate, time);
        if (lecture.isPresent()) {
            log.info("예약이 가능했다가, 인원수가 꽉 차서 예약이 불가능해진 경우 처리 / 또는 불가능 했다가 다시 예약이 가능한 경우 처리");
            log.info("person({}) : capacity({})", person, lecture.get().getCapacity());
            boolean status = person < lecture.get().getCapacity();
            lecture.get().setStatus(status);
            lectureRepository.save(lecture.get());
            return true;
        }
        log.info("처음 저장하는 클래스");
        return false;
    }
}
