package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class AdslLoginGenerator implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR codeClientSeq";

  @Override
  public String generateValue(Session session, Object o) {

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    if (String.valueOf(seqValue).length() < 6) {
      String newString = String.format("%2d%s", 0, seqValue);

      return String.format("%6d", Integer.parseInt("1" + newString.trim()));
    } else
      return String.valueOf(seqValue);
  }
}
