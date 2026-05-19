package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class CustomGenerator implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR hibernate_sequence";

  @Override
  public String generateValue(Session session, Object o) {
    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    return "nety" + String.format("%06d%s", 0, seqValue);
  }
}
