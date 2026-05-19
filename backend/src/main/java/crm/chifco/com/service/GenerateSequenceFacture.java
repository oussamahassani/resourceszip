package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class GenerateSequenceFacture implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR factureSeq";

  @Override
  public String generateValue(Session session, Object o) {
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    String year = df.format(date);

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    String format = "%1$07d";
    String result = String.format(format, seqValue);
    return String.valueOf(result) + "-" + year;
  }
}
