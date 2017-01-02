import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetBattles implements Runnable {
    private static final Logger log = LogManager.getLogger(GetBattles.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0";
    private static final String BASE_URL = "http://warthunder.com/en/tournament/replay/page/";
    private Map<String, String> cookies = new HashMap<>();
    private Connection get;
    private Document doc;
    private int pageNumber;
    private GameMode gameMode;
    private GameType gameType;
    private Vehicles vehicles;


    GetBattles(int pageNumber, GameMode gameMode, GameType gameType, Vehicles vehicles) {
        this.pageNumber = pageNumber;
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.vehicles = vehicles;
    }

    void setCookies() {
        cookies.put("identity_sid", "4t8fvafmgv5i71m8q9ah9arla0");
        cookies.put("identity_token", "mbnzumnarru51gxxnn32jkmw8fqrzb7afdsdewkqkxptb7osw8xqt5od7fkpw1g5");
        cookies.put("identity_id", "47044885");
        cookies.put("identity_name", "id733");
    }

    void open() throws IOException {

        StringBuilder url = new StringBuilder(BASE_URL);
        url.append(pageNumber);
        url.append("?Filter%5Bgame_mode%5D%5B0%5D=");

        switch (gameMode) {
            case Arcade:
                url.append("arcade");
                break;
            case Realistic:
                url.append("realistic");
                break;
         /*   case Simulation:
                url.append("simulation");
                break;*/
        }

        url.append("&Filter%5Bgame_type%5D%5B0%5D=");

        switch (gameType) {
            case Random:
                url.append("randomBattle");
                break;
        }

        url.append("&Filter%5Bstatistic_group%5D=");

        switch (vehicles) {
            case Aircraft:
                url.append("aircraft");
                break;
            case Tank:
                url.append("tank");
                break;
            case TanksAndPlanes:
                url.append("mixed");
                break;
        }

        url.append("&Filter%5Bkeyword%5D=&Filter%5Bnick%5D=&action=search");

        get = Jsoup.connect(url.toString())
                .cookies(cookies)
                .userAgent(USER_AGENT)
                .header("Accept-Encoding", "gzip, deflate")
                .timeout(5000)
                .method(Connection.Method.GET);
        doc = get.execute().parse();
        Elements replays = doc.select("a[class=replay__item]");
        for (Element replay : replays) {
            ReplayParser parser = new ReplayParser(replay);
            Thread thread = new Thread(parser);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("Thread interrupted " + e);
            }

        }
    }

    @Override
    public void run() {
        setCookies();
        try {
            open();
        } catch (IOException e) {
            log.error("IOException at page " + pageNumber + " " + e);
        }
    }
}
