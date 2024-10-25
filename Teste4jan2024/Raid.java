import java.util.List;

public interface Raid {
    List<String> players();
    void waitStart() throws InterruptedException;
    void leave();
}
