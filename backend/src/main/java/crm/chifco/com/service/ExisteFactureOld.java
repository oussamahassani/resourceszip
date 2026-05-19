package crm.chifco.com.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.Facture;

@Service
@Transactional
public class ExisteFactureOld {
  @Autowired

  @Value("${app.datasource.crm.url}")
  private String url;
  @Value("${app.datasource.crm.username}")
  private String login;
  @Value("${app.datasource.crm.password}")
  private String password;

  public List<Facture> getFilteredFactures(Long abonnementId, List<String> factures) {
    try {
      String min = Collections.min(factures);
      String max = Collections.max(factures);
      Connection con = DriverManager.getConnection(url, login, password);
      String sql =
          "SELECT f.facture_id,f.ref_facture, f.date_de_debut, f.date_de_fin, f.etat_facture, f.montant_payer, f.date_echeance "
              + "FROM factures f WHERE f.clientid = ? "
              + "AND ( f.facture_id > ALL (SELECT CAST(id_value AS INTEGER) FROM (VALUES  " + "(?)";

      sql += ") AS id_list(id_value)) ";

      sql += "   and f.facture_id < ALL (" + "    SELECT CAST(id_value AS INTEGER)" + "    FROM ("
          + "        VALUES (?)" + ")AS id_list(id_value)) or"
          + "  f.facture_id < ALL (SELECT CAST(id_value AS INTEGER) FROM (VALUES  (?)) AS id_list(id_value)) )"
          + "AND f.etat_facture = 0";

      PreparedStatement preparedStatement = con.prepareStatement(sql);
      preparedStatement.setLong(1, abonnementId);
      preparedStatement.setString(2, min);
      preparedStatement.setString(3, max);
      preparedStatement.setString(4, min);
      // Set the facture values dynamically
   

      ResultSet resultSet = preparedStatement.executeQuery();

      // Process the results and return a list of Facture objects
      List<Facture> factureList = new ArrayList<>();
      while (resultSet.next()) {
        Facture facture = new Facture();
        facture.setFactureId(resultSet.getLong("facture_id"));
        facture.setRef_facture(resultSet.getString("ref_facture"));
        facture.setDateDeDebut(resultSet.getDate("date_de_debut"));
        // Set other Facture properties as needed
        factureList.add(facture);
      }

      return factureList;
    } catch (SQLException e) {
      throw new RuntimeException("Error retrieving factures: " + e.getMessage(), e);
    }
  }

}
