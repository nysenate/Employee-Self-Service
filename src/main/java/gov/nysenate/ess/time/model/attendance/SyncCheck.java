package gov.nysenate.ess.time.model.attendance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Created by riken on 4/8/14.
 */
public class SyncCheck
{
    protected BigInteger checkId;
    protected String dataType;
    protected BigDecimal dataId;
    protected Timestamp date;
    protected String dataSide;

    public BigInteger getCheckId() {
        return checkId;
    }

    public void setCheckId(BigInteger checkId) {
        this.checkId = checkId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public BigDecimal getDataId() {
        return dataId;
    }

    public void setDataId(BigDecimal dataId) {
        this.dataId = dataId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDataSide() {
        return dataSide;
    }

    public void setDataSide(String dataSide) {
        this.dataSide = dataSide;
    }
}
