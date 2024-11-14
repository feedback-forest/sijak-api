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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.persist.domain.*;
import zerobase.sijak.persist.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DongjakScrapService {

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
            options.addArguments("--headless");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

            String DONGJAK_URL = "https://50plus.or.kr/djc/education.do?page=%d&";
            int idx = 1;
            while (true) {
                String url = String.format(DONGJAK_URL, idx);
                System.out.println("url :" + url);
                driver.get(url);
                WebElement specificTable = driver.findElement(By.xpath("/html/body/div[3]/div[3]/table"));
                WebElement specificTbody = specificTable.findElement(By.tagName("tbody"));
                // 읽을 Tbody가 없다면 -> 크롤링 종료
                if (specificTbody.getText().isEmpty()) {
                    System.out.println("specificTbody is empty");
                    break;
                }
                List<WebElement> rows = specificTbody
                        .findElements(By.cssSelector("body > div.container > div.campus-course-list-table > table > tbody > tr"));

                Thread.sleep(1000);
                for (int i = 0; i < rows.size(); i++) {
                    WebElement row = rows.get(i);

                    List<WebElement> cols = row.findElements(By.tagName("td"));

                    log.info("수강신청 여부: {}", cols.get(cols.size() - 1).getText());
                    log.info("수강신청 여부 false/true: {}", !cols.get(cols.size() - 1).getText().trim().equals("수강신청"));
                    log.info("이미 저장 여부: {}", alreadySavedUserJudge(cols.get(9).findElement(By.tagName("a")).getAttribute("href")
                            , cols.get(cols.size() - 1).getText()));
                    log.info("href = {}", cols.get(9).findElement(By.tagName("a")).getAttribute("href"));

                    if (alreadySavedUserJudge(cols.get(9).findElement(By.tagName("a")).getAttribute("href")
                            , cols.get(cols.size() - 1).getText())) continue;
                    log.info("저장 여부는 통과");
                    if (!cols.get(cols.size() - 1).getText().trim().equals("수강신청")) continue;
                    log.info("수강신청 여부 통과");

                    WebElement telInfo = driver.findElement(By.xpath("/html/body/footer/div[2]/div[2]/address/a[1]"));
                    tel = telInfo.getText();
                    log.info("tel = {}", tel);

                    System.out.println("2222");
                    for (int j = 3; j < cols.size(); j++) {
                        String content = cols.get(j).getText();
                        switch (j) {
                            case 3:
                                name = cols.get(j).getText();
                                System.out.println("name = " + name);
                                break;
                            case 4:
                                String[] date = cols.get(j).getText().split("~");
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH:mm:ss");
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
                                startDate = time.split("~")[0].trim();
                                endDate = time.split("~")[1].trim();
                                System.out.println("time = " + time);
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
                                System.out.println("수강신청 링크: " + href);

                                System.out.println("name : " + name);
                                System.out.println("time : " + time);
                                System.out.println("price : " + price);
                                System.out.println("capacity : " + capacity);
                                System.out.println("link : " + href);

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

                                Lecture lectureId = lectureRepository.save(lecture);
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

        } catch (Exception e) {
            throw new CustomException(Code.CRAWLING_FAILED);
        }
    }

    public void getDetailInfo(int lId, String href) throws CustomException {
        try {
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

            Lecture lecture = lectureRepository.findById(lId).orElseThrow(RuntimeException::new);

            WebElement we = driver.findElement(By.cssSelector("body > div.container > div.course-content.clearfix > div.course-left > div.course-schedule-table > div > table > tbody > tr:nth-child(1)"));
            List<WebElement> tds = we.findElements(By.tagName("td"));

            String time = "", location = "", description = "", need = "";
            for (int i = 0; i < tds.size(); i++) {
                switch (i) {
                    case 0:
                        time = tds.get(i).getText().split("\n")[1].replace("(", "").replace(")", "");
                        break;
                    case 1:
                        location = tds.get(i).getText();
                        break;
                    case 3:
                        description = tds.get(i).getText();
                        break;
                    case 4:
                        need = tds.get(i).getText();

                }
            }
            lecture.setTime(time);
            lecture.setLocation(location);
            lecture.setDescription(description);
            lecture.setNeed(need);

            log.info("time : {}", time);

            lecture = lectureRepository.save(lecture);


            List<String> images = new ArrayList<>();
            WebElement specificDiv = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]"));
            List<WebElement> paragraphs = specificDiv
                    .findElements(By.cssSelector("body > div.container > div.course-content.clearfix > div.course-left > div.course-info > p"));

            System.out.println("333");

            for (WebElement paragraph : paragraphs) {
                String innerText = paragraph.getAttribute("innerHTML");
                if (innerText.contains("img")) {

                    List<WebElement> imgs = paragraph.findElements(By.tagName("img"));
                    System.out.println("imgs.size: " + imgs.size());

                    for (WebElement img : imgs) {
                        Image image = new Image(lecture, img.getAttribute("src"));
                        Thread.sleep(1000);
                        imageRepository.save(image);
                        images.add(img.getAttribute("src"));
                    }

                    System.out.println("images : " + images);

                }
            }
            System.out.println("images: " + images);
            WebElement instructorUl = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/ul"));
            List<WebElement> lis = instructorUl.findElements(By.cssSelector("body > div.container > div.course-content.clearfix > div.course-right > ul > li"));

            Map<String, List<String>> teachers = new HashMap<>();
            for (WebElement li : lis) {

                WebElement h5 = li.findElement(By.tagName("h5"));
                String teacherName = h5.getText();

                Teacher teacher = Teacher.builder()
                        .name(teacherName)
                        .lecture(lecture)
                        .build();

                Teacher teacherId = teacherRepository.save(teacher);
                int tid = teacherId.getId();
                WebElement innerUl = li.findElement(By.tagName("ul"));
                log.info("innerUI : {}", innerUl.getAttribute("innerHTML"));
                if (innerUl.getAttribute("innerHTML").contains("li")) {
                    List<WebElement> innerLis = innerUl.findElements(By.tagName("li"));
                    List<String> histories = new ArrayList<>();
                    for (WebElement innerLi : innerLis) {
                        String content = innerLi.getText().substring(1).trim();
                        Career career = Career.builder()
                                .teacher(teacherId)
                                .content(content).build();
                        careerRepository.save(career);
                        histories.add(content);
                    }
                    teachers.put(teacherName, histories);
                    System.out.println("teachers = " + teachers);
                }
            }

            // educatePlan
            log.info("educate start");
            List<WebElement> trs = driver.findElements(By.cssSelector("body > div.container > div.course-content.clearfix > div.course-left > div.course-schedule-table > div > table > tbody > tr"));
            for (WebElement tr : trs) {
                List<WebElement> eduTds = tr.findElements(By.tagName("td"));
                for (int i = 0; i < eduTds.size(); i++) {
                    if (i == 3) {
                        String content = eduTds.get(i).getText();
                        log.info("content : {}" + content);
                        Educate educate = new Educate(lecture, content);
                        educateRepository.save(educate);
                    }
                }
            }

            System.out.println("상세 읽기 finish!");
            driver.close();
            driver.quit();
            Thread.sleep(3000);
        } catch (Exception e) {
            throw new CustomException(Code.CRAWLING_FAILED);
        }
    }

    private boolean alreadySavedUserJudge(String link, String lectureStatus) {
        Lecture lecture = lectureRepository.findByLink(link);

        if (lecture == null) return false;
        else if (LocalDateTime.now().isAfter(lecture.getDeadline())) {
            log.info("now: {} / deadline: {}", LocalDateTime.now(), lecture.getDeadline());
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        } else if (!lecture.isStatus() && lectureStatus.trim().equals("수강신청")) {
            lecture.setStatus(true);
            lectureRepository.save(lecture);
            return true;
        } else if (lecture.isStatus() && !lectureStatus.trim().equals("수강신청")) {
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        }
        return true;
    }

}
