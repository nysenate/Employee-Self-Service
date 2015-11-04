package gov.nysenate.ess.web.model.attendance;

import java.math.BigInteger;
import java.sql.Timestamp;

public class TempDataStore
{
    protected int id;
    protected char dataType;
    protected BigInteger dataId;
    protected Timestamp date;

    /**Basic getters and setters*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getDataType() {
        return dataType;
    }

    public void setDataType(char dataType) {
        this.dataType = dataType;
    }

    public BigInteger getDataId() {
        return dataId;
    }

    public void setDataId(BigInteger dataId) {
        this.dataId = dataId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
