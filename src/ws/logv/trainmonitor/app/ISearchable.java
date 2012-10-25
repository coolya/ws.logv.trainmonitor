package ws.logv.trainmonitor.app;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 11.10.12
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchable {
    void query(String query);
    void searchClosed();
}
