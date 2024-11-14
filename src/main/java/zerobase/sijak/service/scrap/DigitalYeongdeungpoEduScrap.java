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
import zerobase.sijak.persist.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DigitalYeongdeungpoEduScrap {

    private final LectureRepository lectureRepository;
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;
    private final CareerRepository careerRepository;
    private final EducateRepository educateRepository;

    //@Scheduled(fixedRate = 10000000)
    public void scrapDongjak() throws CustomException {
        try {
            String name = "", time = "", price = "", href = "", startDate = "", endDate = "", tel = "";
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

            String URL = "https://didong.kr/reservation/date/0/1";

            driver.get(URL);

            List<WebElement> trs = driver
                    .findElements(By.cssSelector("#main > section.section8-2.section12-1 > div > div.calendar-board-wrap > div.inner-wrap > div.le-wrap.calendar-wrap > div.calendar-body > table > tbody > tr"));

            for (int i = 1; i < trs.size() - 1; i++) {
                List<WebElement> tds = trs.get(i).findElements(By.cssSelector("td"));
                for (WebElement td : tds) {
                    if (StringUtils.isEmpty(td.getText()) || td.getText().contains("예약불가"))
                        continue;
                    log.info(td.getText());
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
                        startDate = LocalDateTime.now().toString().substring(0, 10);
                        endDate = LocalDateTime.now().toString().substring(0, 10);
                        price = "0원";
                        deadline = LocalDateTime.parse(endDate + "T00:00:00", formatter);
                        log.info("deadline : {}", deadline);
                        boolean status = person < capacity;
                        Lecture lecture = Lecture.builder()
                                .name(name)
                                .time(time)
                                .price(price)
                                .total(-1)
                                .certification("")
                                .dayOfWeek("")
                                .target("")
                                .tel(tel)
                                .textBookName("")
                                .textBookPrice("")
                                .thumbnail("")
                                .capacity(capacity)
                                .deadline(deadline)
                                .link(href)
                                .startDate(startDate)
                                .endDate(endDate)
                                .division("정기 클래스")
                                .view(0)
                                .status(true)
                                .latitude(37.513305)
                                .longitude(126.941528)
                                .centerName("서울시 50+동작50플러스센터")
                                .address("서울특별시 동작구 노량진로 140 메가스터디타워 2층")
                                .build();


                        lectureRepository.save(lecture);
                    }
                }
            }


        } catch (Exception e) {
            throw new CustomException(Code.CRAWLING_FAILED);
        }
    }
}
