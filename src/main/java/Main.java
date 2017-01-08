import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);

        GodObject magic = new GodObject();
        for (int i = 0; i < 91; i++) {
            service.submit(new GetBattles(magic, i, GameMode.Arcade, GameType.Random, Vehicles.Tank));
            service.submit(new GetBattles(magic, i, GameMode.Arcade, GameType.Random, Vehicles.Aircraft));
            service.submit(new GetBattles(magic, i, GameMode.Arcade, GameType.Random, Vehicles.TanksAndPlanes));
            service.submit(new GetBattles(magic, i, GameMode.Realistic, GameType.Random, Vehicles.Tank));
            service.submit(new GetBattles(magic, i, GameMode.Realistic, GameType.Random, Vehicles.Aircraft));
            service.submit(new GetBattles(magic, i, GameMode.Realistic, GameType.Random, Vehicles.TanksAndPlanes));
//            service.submit(new GetBattles(magic, i, GameMode.Simulation, GameType.Random, Vehicles.Tank));
//            service.submit(new GetBattles(magic, i, GameMode.Simulation, GameType.Random, Vehicles.Aircraft));
//            service.submit(new GetBattles(magic, i, GameMode.Simulation, GameType.Random, Vehicles.TanksAndPlanes));
        }


        service.shutdown();
        try {
            while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
                log.info("Awaiting completion of threads.");
            }
        } catch (
                InterruptedException e) {
            log.error("Thread problem");
        }
        HibernateFactory.close();
    }
}
