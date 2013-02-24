package ws.logv.trainmonitor.data;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 07.12.12
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public enum TrainType {
    NONE, All, CNL, EC, IC, ICE, RJ, TGV, THA;

    public static String getString(TrainType type) {
        switch (type) {
            case IC:
                return "IC";
            case ICE:
                return "ICE";
            case EC:
                return "EC";
            case CNL:
                return "CNL";
            case RJ:
                return "RJ";
            case TGV:
                return "TGV";
            case THA:
                return "THA";
        }
        return "";
    }
}
