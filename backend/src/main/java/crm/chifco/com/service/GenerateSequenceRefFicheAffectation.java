package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

public class GenerateSequenceRefFicheAffectation implements ValueGenerator<String> {

  private static final String query = "SELECT NEXT VALUE FOR ref_fiche_affectation_seqs";
  private static final String PREFIX = "A";
  private static final int SEQUENCE_LENGTH = 6;
  private static final String YEAR_FORMAT = "yyyy";

  @Override
  public String generateValue(Session session, Object owner) {
    // TODO Auto-generated method stub
    Date date = new Date();
    SimpleDateFormat yearFormat = new SimpleDateFormat(YEAR_FORMAT);
    String year = yearFormat.format(date);

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    String sequence = String.format("%1$" + SEQUENCE_LENGTH + "s", seqValue).replace(' ', '0');
    return PREFIX + year + sequence;
  }

}
