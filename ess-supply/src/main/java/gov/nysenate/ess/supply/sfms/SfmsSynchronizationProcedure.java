package gov.nysenate.ess.supply.sfms;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SfmsSynchronizationProcedure extends StoredProcedure {
    // TODO: CAN EXTEND Multiple classes

    private static final String procedureName = "SupplySychronization.synchronize";

    @Autowired
    public SfmsSynchronizationProcedure(ComboPooledDataSource remoteDataSource) {
        super(remoteDataSource, procedureName);
        declareParameter(new SqlOutParameter("response", Types.NUMERIC));
        declareParameter(new SqlParameter("requisitionXml", Types.VARCHAR));
        setFunction(true);
        compile();
    }

    public int synchronizeRequisition(String requisitionXml) {
        return 0;
    }


//    public MyStoredProcedure(DataSource ds){
//        super(ds,SQL);
//        declareParameter(new SqlOutParameter("param_out", Types.NUMERIC));
//        declareParameter(new SqlParameter("param_in", Types.NUMERIC));
//        setFunction(true);//you must set this as it distinguishes it from a sproc
//        compile();
//    }
//
//    public String execute(Long rdsId){
//        Map in = new HashMap();
//        in.put("param_in",rdsId);
//        Map out = execute(in);
//        if(!out.isEmpty())
//            return out.get("param_out").toString();
//        else
//            return null;
//    }
}
