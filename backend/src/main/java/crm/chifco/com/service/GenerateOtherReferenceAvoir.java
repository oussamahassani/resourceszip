package crm.chifco.com.service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GenerateOtherReferenceAvoir {
  @Autowired

  @Value("${app.datasource.crm.url}")
  private String url;
  @Value("${app.datasource.crm.username}")
  private String login;
  @Value("${app.datasource.crm.password}")
  private String password;

  public String generateWithPrefix() {

    try {

      Date date = new Date();
      SimpleDateFormat df = new SimpleDateFormat("yyyy");
      String year = df.format(date);


      Connection con = DriverManager.getConnection(url, login, password);

      String query = "SELECT NEXT VALUE FOR avoirreglementSeq";

      PreparedStatement preparedStatement = con.prepareStatement(query);



      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) { // Move the cursor to the first row
        int seqValue = resultSet.getInt(1); // Assuming avoirSeq is in the first column
        String format = "%1$07d";
        String result = String.format(format, seqValue);
        return "AR" + "-" + String.valueOf(result) + "-" + year;
      } else {
        throw new RuntimeException("No value retrieved from sequence.");
      }


      // Process the results and return a list of Facture objects



    } catch (SQLException e) {
      throw new RuntimeException("Error retrieving factures: " + e.getMessage(), e);
    }
  }

}
