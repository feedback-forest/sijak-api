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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.persist.domain.*;
import zerobase.sijak.persist.repository.*;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SongpaScrapService {

    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final CareerRepository careerRepository;
    private final EducateRepository educateRepository;

    //@Scheduled(fixedRate = 10000000)
    public void scrapSongpa() throws CustomException {
        try {
            String name = "", time = "", href = "", price = "", dayOfWeek = "", location = "";
            int capacity = 1, lId = -1, tId = -1, cId = -1;

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

            String SONGPA_URL = "https://www.songpawoman.org/2024/search.asp?catg_s_num=&dayofweek=&key=&keyf=&keyhour=&wk1=&wk2=&wk3=&wk4=&wk5=&wk6=&v=l&g=1&Page_Current=%d";

            int idx = 1;
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
                    if (!isTrueKeyword(cols.get(1).getText().substring(5))) continue;
                    if (alreadySavedUserJudge(cols.get(1).findElement(By.tagName("a")).getAttribute("href")
                            , cols.get(cols.size() - 1).getText())) continue;
                    log.info("status = {}", cols.get(cols.size() - 1).getText());
                    if (cols.get(cols.size() - 1).getText().trim().equals("마감") || cols.get(cols.size() - 1).getText().trim().equals("접수마감"))
                        continue;

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
                                        .latitude(37.505900)
                                        .longitude(127.109778)
                                        .address("서울특별시 송파구 백제고분로 42길 5")
                                        .centerName("송파여성문화회관")
                                        .capacity(capacity)
                                        .division("정기 클래스")
                                        .status(true)
                                        .endDate("")
                                        .total(12)
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


            String target = driver.findElement(By.xpath("//*[@id=\"tab001\"]/table[2]/tbody/tr[1]/td")).getText();
            String description = driver.findElement(By.xpath("//*[@id=\"tab001\"]/table[2]/tbody/tr[3]/td")).getText();

            Lecture lecture = lectureRepository.findById(lId)
                    .orElseThrow(() -> new CustomException(Code.LECTURE_ID_NOT_EXIST));

            WebElement thumbnail = driver.findElement(By.cssSelector("#mainsection > div > div > div > div > div.product.row > div.img-container.col-md-4.col-xs-12 > img"));
            log.info("1");
            WebElement startDate = driver.findElement(By.xpath("//*[@id=\"mainsection\"]/div/div/div/div/div[2]/div[2]/div/ul/li[3]/p"));
            log.info("2");

            List<WebElement> trs = driver.findElements(By.cssSelector("#tab001 > table:nth-child(3) > tbody > tr"));
            String certification = "", textBookName = "", textBookPrice = "", need = "";

            log.info("3");
            for (WebElement tr : trs) {
                if (tr.getText().contains("자격증")) {
                    WebElement c = tr.findElement(By.tagName("td"));
                    certification = c.getText();
                } else if (tr.getText().contains("교재명")) {
                    List<WebElement> tds = tr.findElements(By.tagName("td"));
                    textBookName = tds.get(0).getText();
                    textBookPrice = tds.get(1).getText();
                } else if (tr.getText().contains("비고")) {
                    WebElement c = tr.findElement(By.tagName("td"));
                    need = c.getText();
                }
            }
            log.info("4");
            String bookPrice = "0";
            DecimalFormat df = new DecimalFormat("#,###");
            if (textBookPrice.isEmpty()) {
                bookPrice = "0";
            } else {
                bookPrice = df.format(Integer.parseInt(textBookPrice));
            }

            String date = driver.findElement(By.cssSelector("#mainsection > div > div > div > div > div.product.row > div.product-info.col-md-8.col-xs-12 > div > ul > li:nth-child(3)"))
                    .getText().split(":")[1].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            log.info("end = {}", date.trim());
            LocalDateTime deadline = LocalDateTime.parse(date + "T00:00:00", formatter);
            log.info("bookPrice : {}", bookPrice);

            lecture.setThumbnail(thumbnail.getAttribute("src"));
            lecture.setTarget(target);
            lecture.setDescription(description);
            lecture.setStartDate(startDate.getText().substring(6));
            lecture.setTextBookName(textBookName);
            lecture.setDeadline(deadline);
            lecture.setTextBookPrice(bookPrice + "원");
            lecture.setNeed(need);
            lecture.setCertification(certification);


            lecture = lectureRepository.save(lecture);

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
            log.info("read educate content");

            WebElement tab = driver.findElement(By.cssSelector("a[href='#tab002']"));
            tab.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tab002")));

            List<WebElement> trGroup = driver.findElements(By.cssSelector("#tab002 > table > tbody > tr"));
            Thread.sleep(1000);
            for (WebElement tr : trGroup) {
                List<WebElement> tds = tr.findElements(By.tagName("td"));
                String content = tds.get(1).getText();
                log.info("content : {}", content);

                Educate educate = new Educate(lecture, content);
                educateRepository.save(educate);
            }


            log.info("상세 읽기 finish!");
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
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        } else if (!lecture.isStatus() && !(lectureStatus.trim().equals("마감") || lectureStatus.trim().equals("접수마감"))) {
            lecture.setStatus(true);
            lectureRepository.save(lecture);
            return true;
        } else if (lecture.isStatus() && (lectureStatus.trim().equals("마감") || lectureStatus.trim().equals("접수마감"))) {
            lecture.setStatus(false);
            lectureRepository.save(lecture);
            return true;
        }
        return true;
    }

    private boolean isTrueKeyword(String name) {
        return name.contains("자격증") || name.contains("공예") || name.contains("영상") || name.contains("꽃") ||
                name.contains("스마트폰") || name.contains("댄스") || name.contains("요가") || name.contains("시니어모델");
    }
}
