package ws.logv.trainmonitor.data;

public interface Func2<T, Tin, Tin2> {
	T exec(Tin param, Tin2 param2);
}
