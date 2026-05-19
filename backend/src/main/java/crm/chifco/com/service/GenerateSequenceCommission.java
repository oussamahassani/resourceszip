package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class GenerateSequenceCommission implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR ref_commission";

  @Override
  public String generateValue(Session session, Object o) {
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    String year = df.format(date);

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();

    DecimalFormat decimalFormat = new DecimalFormat("000000");
    String numeroFormate = decimalFormat.format(seqValue);
    return "C-" + year + "-" + numeroFormate;
  }
}
