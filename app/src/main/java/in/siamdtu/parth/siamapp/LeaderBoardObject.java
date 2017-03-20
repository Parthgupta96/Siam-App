package in.siamdtu.parth.siamapp;

/**
 * Created by Parth on 09-02-2017.
 */

public class LeaderBoardObject {
    private String name;
    private long score;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getScore() {
        return score;
    }

    public String getEmail() {
        return email;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public LeaderBoardObject() {

    }

    public LeaderBoardObject(String name, long score, String email) {
        this.name = name;
        this.score = score;
        this.email = email;
    }

    public LeaderBoardObject(String name, long score) {
        this.name = name;
        this.score = score;
    }
}
