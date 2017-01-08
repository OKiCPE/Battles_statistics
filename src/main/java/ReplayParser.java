import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Element;

import javax.persistence.Query;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReplayParser implements Runnable {
    private static final Logger log = LogManager.getLogger(ReplayParser.class);
    Element element;
    private long id;
    Battle battle;
    GameType gameType;
    GameMode gameMode;
    Vehicles vehicles;
    long battleId;
    String a;
    String type;
    String gamesMode;
    String map;
    Date date;
    String vehicless;
    Time duration;
    Date parced;
    boolean isExist;

    ReplayParser(Element element) {
        this.element = element;
    }

    @Override
    public void run() {


        battleId = Long.parseLong(element.attr("data-replay"));

        Session session = HibernateFactory.getSession();

        Query query;
        List list;
        query = session.createQuery("from Battle where battleId = :battleId");
        query.setParameter("battleId", battleId);
        list = query.getResultList();
        if (list.isEmpty()) {

        } else {
            isExist = true;
        }

        session.close();

        if (!isExist) {
            battle = new Battle();
            battle.setBattleId(battleId);
            String raw = element.select("div[class=col-9 replay__title]").text();

            int index = raw.indexOf(']');
            battle.setMission(raw.substring(1, index));


            battle.setMap(raw.substring(index + 2, raw.length()));
            String dateString = element.select("span[class=date__text]").text();
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);

            try {
                parced = formatter.parse(dateString);
                java.sql.Timestamp sql = new java.sql.Timestamp(parced.getTime());
                battle.setDate(sql);
            } catch (ParseException e) {
                log.error("Parse error " + e);
                e.printStackTrace();
            }


            String gametypes = element.select("div[class=col-6]").select("span[class=stat__value]").first().text();
            switch (gametypes) {
                case "Random battle":
                    battle.setGameType(GameType.Random);
                    break;
                case "Squadrons":
                    battle.setGameType(GameType.Squadrons);
                    break;
                case "Firing field":
                    battle.setGameType(GameType.FiringField);
                    break;
                case "Tournament":
                    battle.setGameType(GameType.Tournament);
                    break;
                case "Squadron tournament":
                    battle.setGameType(GameType.SquadronTournament);
                    break;
                default:
                    battle.setGameType(GameType.N);
                    log.error("Error GameType " + gametypes);
                    break;
            }


            type = element.select("div[class=row]").get(2).select("span[class=stat__value]").first().text();
            switch (type) {
                case "Arcade battles":
                    battle.setGameMode(GameMode.Arcade);
                    break;
                case "Realistic battles":
                    battle.setGameMode(GameMode.Realistic);
                    break;
                case "Simulation battles":
                    battle.setGameMode(GameMode.Simulation);
                    break;
               /* default:
                    battle.setGameMode(GameMode.N);
                    log.error("Error set GameMode " + type);
                    break;*/
            }


            vehicless = element.select("div[class=col-6]").select("span[class=stat__value]").get(2).text();

            switch (vehicless) {
                case "Aircraft":
                    battle.setVehicles(Vehicles.Aircraft);
                    break;
                case "Tank":
                    battle.setVehicles(Vehicles.Tank);
                    break;
                case "Tank and Planes":
                    battle.setVehicles(Vehicles.TanksAndPlanes);
                    break;
                default:
                    battle.setVehicles(Vehicles.N);
                    log.error("Vehicles " + vehicless);
                    break;

            }

            battle.setDuration(Time.valueOf(element.select("div[class=col-2 pr0 stat-column]")
                    .select("span[class=inlined text-left]")
                    .text()));


            session = HibernateFactory.getSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(battle);
            transaction.commit();
            session.close();
        }
    }
}
