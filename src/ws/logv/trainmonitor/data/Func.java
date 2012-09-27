package ws.logv.trainmonitor.data;

public interface Func<T, Tin> {
	T exec(Tin param);
}
