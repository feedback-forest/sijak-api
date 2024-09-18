package zerobase.sijak.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ScrapService {

    private String LIST_URL = "https://www.songpawoman.org/2024/search.asp?catg_s_num=&dayofweek=&key=&keyf=&keyhour=&wk1=&wk2=&wk3=&wk4=&wk5=&wk6=&v=l&g=1&Page_Current=3";
    private String CONTENT_URL = "https://www.songpawoman.org/2024/epit_contents.asp?epit_num=10501042&om=202410&ucode=&period=3";

    public Map<Object, Object> readClass(Long id) {

        Map<Object, Object> classInfo = new HashMap<>();
        try {
            Connection connection = getConnection();
            Document document = connection.ignoreHttpErrors(true).get();


            String name = document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(4)").text();
            String description = document.select("#tab001 > table:nth-child(3) > tbody > tr:nth-child(2) > td").text();
            String price = document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(4) > td:nth-child(4)").text();
            String dayOfWeek = document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)").text();
            String time = document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(4)").text();
            String capacity = document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(4) > td:nth-child(2)").text();
            String link = CONTENT_URL;
            String latitude = null;
            String longitude = null;
            String target = document.select("#tab001 > table:nth-child(3) > tbody > tr:nth-child(1) > td").text();
            String status = "접수중";
            String thumbnail = document.select("#mainsection > div > div > div > div > div.product.row > div.img-container.col-md-4.col-xs-12 > img").text();
            Boolean like = false;
            String hostedBy = "송파문화여성회관";
            String locationDetail = hostedBy + document.select("#tab001 > table:nth-child(2) > tbody > tr:nth-child(3) > td:nth-child(4)").text();

            classInfo.put("name", name);
            classInfo.put("description", description);
            classInfo.put("price", price);
            classInfo.put("dayOfWeek", dayOfWeek);
            classInfo.put("time", time);
            classInfo.put("capacity", capacity);
            classInfo.put("link", link);
            classInfo.put("latitude", latitude);
            classInfo.put("longitude", longitude);
            classInfo.put("target", target);
            classInfo.put("status", status);
            classInfo.put("thumbnail", thumbnail);
            classInfo.put("like", like);
            classInfo.put("hostedBy", hostedBy);
            classInfo.put("locationDetail", locationDetail);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return classInfo;
    }

    private Connection getConnection() {
        return Jsoup.connect(CONTENT_URL).sslSocketFactory(ScrapService.socketFactory());
    }

    private static SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }

    }
}
