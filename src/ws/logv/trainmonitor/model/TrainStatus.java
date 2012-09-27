package ws.logv.trainmonitor.model;

public enum TrainStatus {
    E("E") ,
    F("F") ,
    S("S"),
    X("X"),
    N("N");
    
    private String value;
    private TrainStatus(String value)
    {
    	this.value = value;
    }
    
    public static TrainStatus findByAbbr(String value)
    {
    	for (TrainStatus c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }
    }
