 package ws.logv.trainmonitor.api;

public interface IApiCallback<T> {
	void onComplete(T data);
	void onError(Throwable tr);	
	void onNoConnection();
}
