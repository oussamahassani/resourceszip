package crm.chifco.com.service.impl;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.User;
import crm.chifco.com.model.UserHistory;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.UserHistoryRepository;
import crm.chifco.com.service.UserHistoryService;

@Service
public class UserHistoryServiceImpl implements UserHistoryService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  UserHistoryRepository userHistoryRepository;

  @Autowired
  CodePostaleRepository codePostaleRepository;

  @Override
  public void addHistoryEntry(Long userEdit, String action, User editBy) {
    // TODO Auto-generated method stub
    UserHistory userHistory = new UserHistory();
    userHistory.setAction(action);
    userHistory.setUserEditId(userEdit);
    userHistory.setEditBy(editBy);

    userHistoryRepository.save(userHistory);

  }

  @Override
  public List<UserHistory> getHistoryByUser(Long userEdit) {
    // TODO Auto-generated method stub
    List<UserHistory> listHistory = userHistoryRepository.findAllByUserEditId(userEdit);
    return listHistory;
  }

  @Override
  public void checkAndSaveHistory(User oldUser, User newUser, User editBy) {
    // TODO Auto-generated method stub
    compareAndSave("Prénom", oldUser.getFirstName(), newUser.getFirstName(), editBy,
        oldUser.getUserid());
    compareAndSave("Nom", oldUser.getLastName(), newUser.getLastName(), editBy,
        oldUser.getUserid());
    compareAndSave("Nom commercial", oldUser.getNomCommercial(), newUser.getNomCommercial(), editBy,
        oldUser.getUserid());
    compareAndSave("Interlocuteur", oldUser.getInterlocuteur(), newUser.getInterlocuteur(), editBy,
        oldUser.getUserid());
    compareAndSave("Email", oldUser.getEmail(), newUser.getEmail(), editBy, oldUser.getUserid());
    compareAndSave("Adresse", oldUser.getAdresse(), newUser.getAdresse(), editBy,
        oldUser.getUserid());
    compareAndSave("Forme juridique", oldUser.getFormeJuridique(), newUser.getFormeJuridique(),
        editBy, oldUser.getUserid());
    compareAndSave("Identification fiscale", oldUser.getIdentificationFiscale(),
        newUser.getIdentificationFiscale(), editBy, oldUser.getUserid());
    compareAndSave("Gouvernorat", oldUser.getGouvernorat().getAbreviation(),
        newUser.getGouvernorat().getAbreviation(), editBy, oldUser.getUserid());
    compareAndSave("ville", oldUser.getVille().getAbreviation(),
        newUser.getVille().getAbreviation(), editBy, oldUser.getUserid());
    compareAndSave("Code postale", oldUser.getCodePostale(), newUser.getCodePostale(), editBy,
        oldUser.getUserid());
    compareAndSave("Type d'utilisateur", oldUser.getTypeUser(), newUser.getTypeUser(), editBy,
        oldUser.getUserid());
    compareAndSave("stock", oldUser.getWithStock(), newUser.getWithStock(), editBy,
        oldUser.getUserid());
    compareAndSave("Plafond ", oldUser.getPlafonRevendeur(), newUser.getPlafonRevendeur(), editBy,
        oldUser.getUserid());
    compareAndSave("coordonnees Bancaires", oldUser.getCoordonneesBancaires(),
        newUser.getCoordonneesBancaires(), editBy, oldUser.getUserid());
    compareAndSave("Activité Principale", oldUser.getActivitePrincipale(),
        newUser.getActivitePrincipale(), editBy, oldUser.getUserid());
    compareAndSave("Régime fiscal", oldUser.getRegimeFiscal(), newUser.getRegimeFiscal(), editBy,
        oldUser.getUserid());
    compareAndSave("Numéro de téléphone", oldUser.getTelephone(), newUser.getTelephone(), editBy,
        oldUser.getUserid());
    compareAndSave("Classe Revendeur", oldUser.getClassUser(), newUser.getClassUser(), editBy,
        oldUser.getUserid());
    compareAndSave("Mot de passe", oldUser.getPassword(), newUser.getPassword(), editBy,
        oldUser.getUserid());
    compareAndSave("Photo de profil", oldUser.getPhoto(), newUser.getPhoto(), editBy,
        oldUser.getUserid());
    compareAndSave("RNE", oldUser.getRNE(), newUser.getRNE(), editBy, oldUser.getUserid());
    compareAndSave("Carte Fiscale", oldUser.getCarteFiscale(), newUser.getCarteFiscale(), editBy,
        oldUser.getUserid());
    compareAndSave("Contrat", oldUser.getContrat(), newUser.getContrat(), editBy,
        oldUser.getUserid());
    compareAndSave("Cin", oldUser.getCin(), newUser.getCin(), editBy, oldUser.getUserid());
    compareAndSave("Role", oldUser.getRole().getRoleName(), newUser.getRole().getRoleName(), editBy,
        oldUser.getUserid());
    compareAndSave("Activation", oldUser.isEnabled() ? "Activé" : "Désactivé",
        newUser.isEnabled() ? "Activé" : "Désactivé", editBy, oldUser.getUserid());

    compareAndSave("Refus Commision", oldUser.getPcRefusCommision(), newUser.getPcRefusCommision(),
        editBy, oldUser.getUserid());
    compareAndSave("Activation Commision", oldUser.getPcActivationCommision(),
        newUser.getPcActivationCommision(), editBy, oldUser.getUserid());
  }

  private void compareAndSave(String fieldName, Object oldValue, Object newValue, User editBy,
      Long userEdit) {

    String historyMessage = null;

    if (!Objects.equals(oldValue, newValue)) {
      if (fieldName.equals("Mot de passe")) {
        historyMessage = "Champ " + fieldName + " modifié";
      }
      if (fieldName.equals("Code postale")) {
        PostalCode oldPostalCode =
            codePostaleRepository.findById(Long.parseLong(oldValue.toString())).get();
        PostalCode newPostalCode =
            codePostaleRepository.findById(Long.parseLong(newValue.toString())).get();
        historyMessage = "Champ " + fieldName + " modifié de '" + oldPostalCode.getCode() + "-"
            + oldPostalCode.getName() + "' à '" + newPostalCode.getCode() + "-"
            + newPostalCode.getName() + "'";
      } else {
        historyMessage = "Champ " + fieldName
            + (oldValue != null ? " modifié de '" + oldValue + "' à '" + newValue + "'"
                : " modifié à '" + newValue + "'");
      }

      addHistoryEntry(userEdit, historyMessage, editBy);
    }
  }

}
