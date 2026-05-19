package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class GenerateSequenceRecu implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR recuSeq";

  @Override
  public String generateValue(Session session, Object o) {

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    String format = "%1$06d";
    String result = String.format(format, seqValue);
    return String.valueOf(result);
  }

}
