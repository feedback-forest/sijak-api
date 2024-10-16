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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.crawling.LectureCreateRequest;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.IdNotExistException;
import zerobase.sijak.persist.domain.Image;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Teacher;
import zerobase.sijak.persist.repository.CareerRepository;
import zerobase.sijak.persist.repository.ImageRepository;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.TeacherRepository;
import zerobase.sijak.service.LectureService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MapoScrapService {

    private final LectureRepository lectureRepository;
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;
    private final CareerRepository careerRepository;
    private final LectureService lectureService;

    //@Scheduled(fixedRate = 10000000)
    public void scrapMapo() throws InterruptedException {

        String name = "", time = "", price = "", href = "", teacherName = "", startDate = "", endDate = "";
        int capacity = 1, lId = -1, tId = -1, cId = -1;
        LocalDateTime deadline = LocalDateTime.now();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

        String MAPO_URL = "https://mapo50.com/shop/list.php?ca_id=10&sort=&sortodr=&page=%d";
        int idx = 1;

        while (true) {

            String url = String.format(MAPO_URL, idx);
            System.out.println("url :" + url);
            driver.get(url);
            WebElement specificTable = driver.findElement(By.xpath("//*[@id=\"bo_list\"]/div/div[1]/table"));
            WebElement specificTbody = specificTable.findElement(By.tagName("tbody"));
            List<WebElement> rows = specificTbody.findElements(By.tagName("tr"));
            // 읽을 row 가 없다면 -> 크롤링 종료
            if (rows.size() == 1) {
                System.out.println("specificTbody is empty");
                break;
            }

            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (alreadySavedUserJudge(cols.get(9).findElement(By.tagName("a")).getAttribute("href")
                        , cols.get(cols.size() - 1).getText())) continue;
                if (cols.get(cols.size() - 1).getText().trim().equals("신청마감")) continue;

                System.out.println("222");
                for (int j = 2; j < cols.size(); j++) {
                    String content = cols.get(j).getText();
                    switch (j) {
                        case 3:
                            name = cols.get(j).getText();
                            System.out.println("name = " + name);
                            break;
                        case 4:
                            String[] date = cols.get(j).getText().split("~");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                            if (date.length == 1) {
                                log.info("end = {}", date[0].trim());
                                LocalDateTime end = LocalDateTime.parse(date[0].trim() + "T00:00:00", formatter);
                                deadline = end;
                            } else if (date.length == 2) {
                                log.info("end = {}", date[1].trim());
                                LocalDateTime end = LocalDateTime.parse(date[1].trim() + "T00:00:00", formatter);
                                deadline = end;
                            }
                            System.out.println("deadline = " + deadline);
                            break;
                        case 5:
                            time = cols.get(j).getText().replace("\n", "").replace("~", " ~ ");
                            if (time.contains("~")) {
                                startDate = time.split("~")[0].trim();
                                endDate = time.split("~")[1].trim();
                            } else {
                                startDate = time.trim();
                            }
                            System.out.println("time = " + time);
                            break;
                        case 6:
                            teacherName = cols.get(j).getText();
                            System.out.println("teacherName = " + teacherName);
                            break;
                        case 7:
                            price = cols.get(j).getText();
                            System.out.println("price = " + price);
                            break;
                        case 8:
                            capacity = Integer.parseInt(cols.get(j).getText());
                            System.out.println("capacity = " + capacity);
                            break;
                        case 9:
                            WebElement link = cols.get(j).findElement(By.tagName("a"));
                            href = link.getAttribute("href");
                            System.out.println("href = " + href);

                            Lecture lecture = Lecture.builder()
                                    .name(name)
                                    .time(time)
                                    .price(price)
                                    .capacity(capacity)
                                    .status(true)
                                    .deadline(deadline)
                                    .link(href)
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .view(0)
                                    .division("정기 클래스")
                                    .latitude(37.556445)
                                    .need("")
                                    .textBookName("")
                                    .textBookPrice("")
                                    .thumbnail("")
                                    .longitude(126.946607)
                                    .centerName("마포시니어클럽")
                                    .total(-1)
                                    .certification("")
                                    .description("")
                                    .dayOfWeek("")
                                    .address("서울특별시 마포구 동교로8길 58")
                                    .build();

                            Lecture lectureId = lectureRepository.save(lecture);
                            Teacher teahcer = Teacher.builder()
                                    .name(teacherName)
                                    .lecture(lecture)
                                    .build();

                            teacherRepository.save(teahcer);
                            lId = lectureId.getId();

                            System.out.println("lId = " + lId);
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
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

        System.out.println("이동!");
        driver.get(href);

        System.out.println("111");
        Thread.sleep(2000);

        Lecture lecture = lectureRepository.findById(lId)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        log.info("222");
        WebElement div = driver.findElement(By.cssSelector("#sit_inf_explan"));
        List<WebElement> paragraphs = div.findElements(By.tagName("p"));
        log.info("333");


        String target = "", location = "", description = "";
        for (WebElement paragraph : paragraphs) {
            String innerText = paragraph.getAttribute("innerHTML");
            if (innerText.contains("대상")) {
                log.info("대상");
                target = paragraph.getText().split(":")[1].trim();
                lecture.setTarget(target);
                lecture = lectureRepository.save(lecture);
            }
            if (innerText.contains("장소")) {
                log.info("장소");
                location = paragraph.getText().split(":")[1].trim();
                lecture.setLocation(location);
                lecture = lectureRepository.save(lecture);
            }
            if (innerText.contains("내용")) {
                log.info("내용");
                description = paragraph.getText().split(":")[1].trim();
                lecture.setDescription(description);
                lecture = lectureRepository.save(lecture);
            }
            if (innerText.contains("img")) {
                List<WebElement> imgs = paragraph.findElements(By.tagName("img"));
                System.out.println("imgs.size: " + imgs.size());

                for (WebElement img : imgs) {
                    Image image = new Image(lecture, img.getAttribute("src"));
                    Thread.sleep(1000);
                    imageRepository.save(image);
                }
            }
        }

        System.out.println("상세 읽기 finish");
        driver.close();
        driver.quit();
        Thread.sleep(3000);

    }

    private boolean alreadySavedUserJudge(String link, String lectureStatus) {
        Lecture lecture = lectureRepository.findByLink(link);

        if (lecture == null) return false;
        else if (LocalDateTime.now().isAfter(lecture.getDeadline())) {
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        } else if (!lecture.isStatus() && !lectureStatus.trim().equals("신청마감")) {
            lecture.setStatus(true);
            lectureRepository.save(lecture);
            return true;
        } else if (lecture.isStatus() && lectureStatus.trim().equals("신청마감")) {
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        }
        return true;
    }
}
