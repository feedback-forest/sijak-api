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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NowonScrapService {

    private final LectureRepository lectureRepository;
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;
    private final CareerRepository careerRepository;

    // @Scheduled(fixedRate = 10000000)
    public void scrapNowon() throws InterruptedException {

        String name = "", time = "", price = "", href = "";
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

        String NOWON_URL = "https://50plus.or.kr/nwc/education.do?page=%d&";
        int idx = 1;
        while (true) {
            String url = String.format(NOWON_URL, idx);
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
                if (!cols.get(cols.size() - 1).getText().equals("수강신청")) continue;

                System.out.println("2222");
                for (int j = 3; j < cols.size(); j++) {
                    String content = cols.get(j).getText();
                    switch (j) {
                        case 3:
                            name = cols.get(j).getText();
                            System.out.println("name = " + name);
                            break;
                        case 5:
                            time = cols.get(j).getText().replace("\n", "").replace("~", " ~ ");
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
                            LectureCreateRequest lectureCreateRequest = LectureCreateRequest.builder()
                                    .name(name)
                                    .time(time)
                                    .price(price)
                                    .capacity(capacity)
                                    .link(href)
                                    .status("P")
                                    .latitude(37.6561352)
                                    .longitude(127.0707057)
                                    .centerName("서울시 50+노원50플러스센터")
                                    .address("서울특별시 노원구 노원로30길 73")
                                    .build();
                            Lecture lecture = new Lecture(lectureCreateRequest);

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

        System.out.println("111");
        Thread.sleep(2000);

        Lecture lecture = lectureRepository.findById(lId).orElseThrow(RuntimeException::new);

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

        System.out.println("상세 읽기 finish!");
        driver.close();
        driver.quit();
        Thread.sleep(3000);
    }
}
