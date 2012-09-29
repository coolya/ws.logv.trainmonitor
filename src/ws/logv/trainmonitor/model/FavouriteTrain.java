package ws.logv.trainmonitor.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class FavouriteTrain {
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(canBeNull = false, unique = true, uniqueIndex = true)
    private String trainId;

    public void setId(Integer id)
    {
        this.id = id;
    }
    public void setTrainId(String trainId)
    {
        this.trainId = trainId;
    }

    public String getTrainId()
    {
        return this.trainId;
    }

    public Integer getId()
    {
        return id;
    }


}
