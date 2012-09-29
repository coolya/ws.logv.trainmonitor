package ws.logv.trainmonitor;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class WindowMediator {
    private static OnRefreshRequestStateHandler sHandler;

    public static void setOnRefreshStateRequestHandler(OnRefreshRequestStateHandler handler)
    {
        sHandler = handler;
    }
    public  static void RequestRefreshState()
    {
          if(sHandler != null)
              sHandler.onRefreshStart();
    }
    public  static void EndRefreshState()
    {
        if(sHandler != null)
            sHandler.onRefreshEnd();
    }
}
