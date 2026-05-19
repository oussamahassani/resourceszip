package crm.chifco.com.service;

import static javax.persistence.FlushModeType.COMMIT;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.beans.factory.annotation.Value;

public class GenerateSequenceAvoirClient implements ValueGenerator<String> {
  private static final String query = "SELECT NEXT VALUE FOR avoirSeq";
  @Value("${app.datasource.crm.url}")
  private String url;
  @Value("${app.datasource.crm.username}")
  private String login;
  @Value("${app.datasource.crm.password}")
  private String password;
  // Assurez-vous que la SessionFactory est correctement configurée
  private static SessionFactory sessionFactory;

  // Méthode pour configurer la SessionFactory (à appeler pendant l'initialisation)
  public static void setSessionFactory(SessionFactory sessionFactory) {
    GenerateSequenceAvoirClient.sessionFactory = sessionFactory;
  }

  @Override
  public String generateValue(Session session, Object o) {
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    String year = df.format(date);

    NativeQuery nativeQuery = session.createSQLQuery(query);
    long seqValue = ((Number) nativeQuery.setFlushMode(COMMIT).uniqueResult()).longValue();
    String format = "%1$07d";
    String result = String.format(format, seqValue);
    return "AVR-" + String.valueOf(result) + "-" + year;
  }



}
