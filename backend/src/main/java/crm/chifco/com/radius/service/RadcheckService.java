/*
 * created by hatem ghozzi on 31 10 2022
 */

package crm.chifco.com.radius.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.model.Radusergroup;
import crm.chifco.com.radius.repository.RadacctRepository;
import crm.chifco.com.radius.repository.RadcheckRepository;
import crm.chifco.com.radius.repository.RadusergroupRepository;

@Service
public class RadcheckService {
  private static final Logger logger = LogManager.getLogger(RadcheckService.class);
  @Autowired
  private RadcheckRepository radcheckRepository;

  @Autowired
  private RadusergroupRepository radusergroupRepository;

  @Autowired
  private RadacctRepository radacctRepository;

  public List<Radcheck> findAll() {
    return radcheckRepository.findAll();
  }

  public Radcheck getRadchecksByUsernameAndAttribute(String username, String attribute) {
    // logger.info(radcheckRepository.Listetabel());
    return radcheckRepository.findUsernameAndAttribute(username, attribute);
  }


  public List<Radcheck> getRadchecksByUsernamee(String username) {
    // logger.info(radcheckRepository.Listetabel());
    return radcheckRepository.findUsername(username);
  }

  public Radcheck updateRedcheck(Radcheck radcheckDetails, String username) {
    Radcheck recheckold =
        getRadchecksByUsernameAndAttribute(username, radcheckDetails.getAttribute());
    if (recheckold != null) {
      recheckold.setUsername(radcheckDetails.getUsername());
      recheckold.setAttribute(radcheckDetails.getAttribute());
      recheckold.setOp(radcheckDetails.getOp());
      recheckold.setValue(radcheckDetails.getValue());
      final Radcheck updatedRadcheck = radcheckRepository.save(recheckold);
      return updatedRadcheck;
    }
    return recheckold;

  }

  public void updateDateExpiration(String expirationDate, String username) {
    logger.info("update expirationDate: " + expirationDate + " " + username);
    Radcheck modificationDateExpiration =
        radcheckRepository.updateDateExpiration(expirationDate, username);
    logger.info(" modificationDateExpiration: " + modificationDateExpiration);
  }

  public void addNewRow(String name, String Atrribute, String value) {

    try {
      // Expiration
      Radcheck Radcheck = new Radcheck();
      Radcheck.setAttribute(Atrribute);
      Radcheck.setOp(":=");
      Radcheck.setUsername(name);
      Radcheck.setValue(value);
      radcheckRepository.save(Radcheck);
    } catch (Exception e) {
      logger.error("Hibernate Exception  addNewRow Radcheck: " + e.getMessage());
    }
    // radcheckRepository.addNewRow(name, Atrribute, ":=", value);

  }

  public void AddNewradusergroup(String username, String typeadsl) {
    try {
      String groupName = null;
      groupName = generateRadusGroupGroupName(typeadsl);
      Radusergroup Usergroup = new Radusergroup();
      Usergroup.setUsername(username);
      Usergroup.setPriority("0");
      Usergroup.setGroupname(groupName);
      radusergroupRepository.save(Usergroup);
    } catch (Exception e) {
      logger.error("Hibernate Exception AddNew usergroup " + e.getMessage());
    }

  }

  public String generateRadusGroupGroupName(String typeadsl) {
    String groupName;
    if ("ADSL".equals(typeadsl))
      groupName = "ADSLPPP";
    else if ("VDSL".equals(typeadsl))
      groupName = "VDSLPPP";
    else if ("GPON".equals(typeadsl))
      groupName = "FASTPPP";
    else
      groupName = "NETYPPP";
    return groupName;
  }

  public List<Radacct> getRadacctConnection(String username) {

    return radacctRepository.findUsernameAndAttribute(username);
  }

  public Radacct getRadacctConnectionToClaculateFraisTT(String username) {

    return radacctRepository.findFirstSessionByUsername(username);
  }

  public List<Radacct> findSessionByUsername(Date createdDate, String username) {
    Date now = new Date();
    SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    return radacctRepository.findSessionByUsername(username, dmyFormat.format(createdDate),
    		fullFormat.format(now));
  }

  public Radusergroup getRaduserGroup(String username) {
    if (username.isEmpty()) {
      return null;
    }
    return radusergroupRepository.findAllByUsername(username);
  }

  public Radacct getRadacctConnectionToClaculateFraisTTAndDateDeConnection(String loginModem,
      String formattedCurrentYearMonth) {
    // TODO Auto-generated method stub
    return radacctRepository.getRadacctConnectionToClaculateFraisTTAndDateDeConnection(loginModem,
        formattedCurrentYearMonth);
  }

  public List<String> getListeConnectionToClaculateFraisTTAndDateDeConnection(
      String formattedCurrentDate) {
    // TODO Auto-generated method stub
    return radacctRepository
        .getListeConnectionToClaculateFraisTTAndDateDeConnection(formattedCurrentDate);
  }

  public Radacct getLastConnectionToClaculateFraisServiceTTIfIsResilation(
      String formattedCurrentDate, String userName) {
    // TODO Auto-generated method stub
    return radacctRepository
        .getLastConnectionToClaculateFraisServiceTTIfIsResilation(formattedCurrentDate, userName);
  }

  public List<String> findExpirationByDate(String formattedCurrentDate) {
    // TODO Auto-generated method stub
    return radcheckRepository.findExpirationByDate(formattedCurrentDate);
  }
}
