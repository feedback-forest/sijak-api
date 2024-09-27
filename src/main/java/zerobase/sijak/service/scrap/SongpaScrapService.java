package zerobase.sijak.service.scrap;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.crawling.LectureCreateRequest;
import zerobase.sijak.persist.domain.Career;
import zerobase.sijak.persist.domain.Image;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Teacher;
import zerobase.sijak.persist.repository.CareerRepository;
import zerobase.sijak.persist.repository.ImageRepository;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.TeacherRepository;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SongpaScrapService {

    private final LectureRepository lectureRepository;
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;
    private final CareerRepository careerRepository;

    //@Scheduled(fixedRate = 10000000)
    public void scrapMapo() throws InterruptedException {

        String name = "", time = "", price = "", href = "", dayOfWeek = "", location = "";
        int capacity = 1, lId = -1, tId = -1, cId = -1;

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

        String SONGPA_URL = "https://www.songpawoman.org/2024/search.asp?catg_s_num=&dayofweek=&key=&keyf=&keyhour=&wk1=&wk2=&wk3=&wk4=&wk5=&wk6=&v=l&g=1&Page_Current=%d";

        int idx = 19;
        while (true) {
            String url = String.format(SONGPA_URL, idx);
            log.info("url : {} ", url);
            driver.get(url);

            WebElement specificTable = driver.findElement(By.xpath("//*[@id=\"mainsection\"]/div/div/div/div/div[3]/table"));
            WebElement specificTbody = specificTable.findElement(By.tagName("tbody"));
            // 읽을 Tbody가 없다면 -> 크롤링 종료
            if (specificTbody.getText().isEmpty()) {
                System.out.println("specificTbody is empty");
                break;
            }
            List<WebElement> rows = specificTbody.findElements(By.cssSelector("#mainsection > div > div > div > div > div:nth-child(4) > table > tbody > tr"));

            Thread.sleep(1000);

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);

                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (alreadySavedUserJudge(cols.get(1).findElement(By.tagName("a")).getAttribute("href")
                        , cols.get(cols.size() - 1).getText())) continue;
                if (cols.get(cols.size() - 1).getText().equals("마감")) continue;

                System.out.println("2222");
                for (int j = 0; j < cols.size(); j++) {
                    String content = cols.get(j).getText();
                    switch (j) {
                        case 1:
                            name = cols.get(j).getText().substring(5);
                            WebElement link = cols.get(j).findElement(By.tagName("a"));
                            href = link.getAttribute("href");
                            break;
                        case 2:
                            dayOfWeek = cols.get(j).getText();
                            break;
                        case 3:
                            time = cols.get(j).getText().replace("\n", "").replace("-", " ~ ");
                            break;
                        case 5:
                            price = cols.get(j).getText();
                            break;
                        case 6:
                            location = cols.get(j).getText();
                            break;
                        case 7:
                            int len = cols.get(j).getText().length();
                            capacity = Integer.parseInt(cols.get(j).getText().substring(0, len - 1));
                            break;
                        case 8:
                            log.info("name : {}", name);
                            log.info("href : {}", href);
                            log.info("dayOfWeek : {}", dayOfWeek);
                            log.info("time : {}", time);
                            log.info("price : {}", price);
                            log.info("location : {}", location);
                            log.info("capacity : {}", capacity);

                            Lecture lecture = Lecture.builder()
                                    .name(name)
                                    .link(href)
                                    .time(time)
                                    .dayOfWeek(dayOfWeek)
                                    .location(location)
                                    .price(price)
                                    .view(0)
                                    .latitude(37.556445)
                                    .longitude(126.946607)
                                    .address("서울특별시 마포구 동교로8길 58")
                                    .centerName("마포시니어클럽")
                                    .capacity(capacity)
                                    .status("P")
                                    .build();

                            Lecture lectureId = lectureRepository.save(lecture);
                            lId = lectureId.getId();
                            log.info("lId : {} ", lId);
                            getDetailInfo(lId, href);
                            Thread.sleep(5000);

                    }
                }
            }


            idx++;
        }
        driver.close();
        driver.quit();

    }

    public void getDetailInfo(int lId, String href) throws InterruptedException {

        System.out.println("상세 읽기 start!");
        Thread.sleep(5000);
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

        System.out.println("이동!");
        driver.get(href);

        String target = driver.findElement(By.xpath("//*[@id=\"tab001\"]/table[2]/tbody/tr[1]/td")).getText();
        String description = driver.findElement(By.xpath("//*[@id=\"tab001\"]/table[2]/tbody/tr[3]/td")).getText();

        log.info("target : {}", target);
        log.info("description : {}", description);

        Lecture lecture = lectureRepository.findById(lId).orElseThrow(RuntimeException::new);
        lecture.setTarget(target);
        lecture.setDescription(description);

        lecture = lectureRepository.save(lecture);
        WebElement img = driver.findElement(By.cssSelector("#mainsection > div > div > div > div > div.product.row > div.img-container.col-md-4.col-xs-12 > img"));
        Image image = new Image(lecture, img.getAttribute("src"));
        imageRepository.save(image);

        WebElement p = driver.findElement(By.cssSelector("#tab001 > table:nth-child(2) > tbody > tr:nth-child(5) > td > p"));
        String teacherName = p.getText();
        log.info("teacherName : {}", teacherName);

        Teacher teacher = Teacher.builder()
                .name(teacherName)
                .lecture(lecture)
                .build();

        Teacher teacherId = teacherRepository.save(teacher);
        int tid = teacherId.getId();
        WebElement innerUl = driver.findElement(By.xpath("//*[@id=\"tab001\"]/table[1]/tbody/tr[5]/td/div/ul"));
        List<WebElement> innerLis = innerUl.findElements(By.tagName("li"));

        for (WebElement innerLi : innerLis) {
            String content = innerLi.getText().trim();
            log.info("content : {}", content);
            Career career = Career.builder()
                    .teacher(teacherId)
                    .content(content).build();

            careerRepository.save(career);
        }

        log.info("상세 읽기 finish!");
        driver.close();
        driver.quit();
        Thread.sleep(3000);
    }

    private boolean alreadySavedUserJudge(String link, String lectureStatus) {
        Lecture lecture = lectureRepository.findByName(link);

        if (lecture == null) return false;
        else if (lecture.getStatus().equals("N")) return true;
        else if (lecture.getStatus().equals("P") && lectureStatus.equals("마감")) {
            lecture.setStatus("N");
            lectureRepository.save(lecture);
            return true;
        }
        return true;
    }
}
