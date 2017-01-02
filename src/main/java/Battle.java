import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "battles")
public class Battle implements Serializable {

    private static final long serialVersionUID = 9106318808349991105L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @Column(name = "battle_id", unique = true, nullable = false)
    long battleId;

    @Column(columnDefinition = "TIME")
    Time duration;
    @Column

    String mission;
    @Column
    String map;
    @Column(columnDefinition = "DATETIME")
    Timestamp date;
    @Enumerated(EnumType.STRING)
    GameMode gameMode;
    @Enumerated(EnumType.STRING)
    GameType gameType;
    @Enumerated(EnumType.STRING)
    Vehicles vehicles;

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public Vehicles getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }


    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public int hashCode() {
        return (int) (battleId ^ (battleId >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Battle battle = (Battle) o;

        return battleId == battle.battleId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
