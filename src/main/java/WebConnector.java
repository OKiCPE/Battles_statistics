import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class WebConnector {
    private static final Logger log = LogManager.getLogger(WebConnector.class);
    private static final String URL = "https://login.gaijin.net/en/sso/login";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0";
    private static final String email = "id733@mail.ru";
    private static final String password = "1Asassin";
    private static final String action = "/en/sso/login/procedure/";
    private String captchaUrl;
    private String captcha;
    private static String token;
    private Map<String, String> cookies;
    private Map<String, String> headers;
    private Connection.Response get;
    private Connection.Response post;


    void connect() throws IOException {


        try {
            get = Jsoup.connect(URL).
                    userAgent(USER_AGENT).
                    header("Accept-Encoding", "gzip, deflate").
                    timeout(5000).
                    method(Connection.Method.GET).execute();
            token = get.parse().select("input[name=refresh_token]").val();
            captchaUrl = "https://login.gaijin.net" + get.parse().select("img[class=captcha-img js-captcha-img]").attr("src");
            log.info(captchaUrl);

            cookies = get.cookies();

            Connection.Response resultImageResponse = Jsoup.connect(captchaUrl)
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            resultImageResponse = Jsoup.connect(captchaUrl)
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            FileOutputStream out = (new FileOutputStream(new java.io.File("captcha.gif")));
            out.write(resultImageResponse.bodyAsBytes());
            out.close();


        } catch (IOException e) {
            log.error("IOException " + e);
        }

        Scanner keyboard = new Scanner(System.in);
        captcha = keyboard.next();
        log.info(captcha);

        headers = get.headers();

        try {

            post = Jsoup.connect(URL)
                    .cookies(cookies)
                    .headers(headers)
                    .userAgent(USER_AGENT)
                    .data("captcha", captcha)
                    .data("action", action)
                    .data("login", email)
                    .data("password", password)
                    .data("refresh_token", token)
                    .referrer(URL)
                    .method(Connection.Method.POST)
                    .execute();

         /*   String test = Jsoup.connect(URL)
                    .cookies(cookies)
                    .headers(headers)
                    .userAgent(USER_AGENT)
                    .data("captcha", captcha)
                    .data("action", action)
                    .data("login", email)
                    .data("password", password)
                    .data("refresh_token", token)
                    .referrer(URL)
                    .method(Connection.Method.POST)
                    .execute().body();
            log.info(test);*/

            /*log.info(post.parse().text());*/
//            log.info(post.statusMessage());


            //          log.info(post.statusCode());
            //  log.info(apost.post().html());


            //  cookies.putAll(post.cookies());
        } catch (IOException e) {
            log.error("IOException " + e);
        }


        Iterator cookieIterator = cookies.entrySet().iterator();
        while (cookieIterator.hasNext()) {
            Map.Entry cookie = (Map.Entry) cookieIterator.next();
            log.info("cookie " + cookie.getKey() + " " + cookie.getValue());
        }


        Iterator headerIterator = headers.entrySet().iterator();
        while (headerIterator.hasNext()) {
            Map.Entry header = (Map.Entry) headerIterator.next();
            log.info("header " + header.getKey() + " " + header.getValue());
        }

    }


    void test() {
        cookies = new HashMap<String, String>();
        cookies.put("identity_sid", "1s513tbv5utgqplbv4l6dlidh0");
        cookies.put("identity_token", "owdnyt7b4st2e2emnhejzooetgq6h329cfyc6b169ak1es8cdot3k9pgdm9xk56u");
        cookies.put("identity_id", "47044885");
        cookies.put("identity_name", "id733");
        try {
            post = Jsoup.connect("http://warthunder.ru/ru/tournament/replay/")
                    .cookies(cookies)
                    .userAgent(USER_AGENT)
                    .referrer(URL)
                    .execute();
            log.info(post.statusCode());
            log.info(post.parse().text());
        } catch (IOException e) {
            log.error("IOException " + e);
        }


    }

    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", "gaijin.jks");
        WebConnector webConnector = new WebConnector();
        webConnector.connect();
        //  webConnector.test();
    }



   /* public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }*/


}
