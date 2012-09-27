package ws.logv.trainmonitor.data;

public abstract class Action<T> implements Runnable {
	public abstract void exec(T param);
	
	public void run()
	{
		this.exec(null);
	}
}
