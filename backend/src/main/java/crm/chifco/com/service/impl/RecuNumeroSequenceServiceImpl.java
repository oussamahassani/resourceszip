package crm.chifco.com.service.impl;

import java.text.DecimalFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.PayementRepository;
import crm.chifco.com.repository.RecuNumeroSequenceRepository;
import crm.chifco.com.service.RecuNumeroSequenceService;

@Service
public class RecuNumeroSequenceServiceImpl implements RecuNumeroSequenceService {

  @Autowired
  PayementRepository payementRepository;

  @Autowired
  RecuNumeroSequenceRepository recuNumeroSequenceRepository;

  @Override
  public String generateCode(User user) {

    double number = 1.0;
    DecimalFormat df = new DecimalFormat("000");
    Long num = recuNumeroSequenceRepository.countRecuByCodeUser(user.getUserid()) + 1;
    String numFormatted = df.format(num);

    Date d = new Date();
    int annee = d.getYear() + 1900;

    String codeRecuPayment = null;
    if (user.getCodeUser() != null) {
      codeRecuPayment = annee + "-" + user.getCodeUser() + "-" + numFormatted;
    } else {
      codeRecuPayment = annee + "-" + numFormatted;
    }

    return codeRecuPayment;
  }

  @Override
  public RecuNumeroSequence save(RecuNumeroSequence recuNumeroSequence) {
    RecuNumeroSequence recuCode = new RecuNumeroSequence();
    recuCode.setCodePayement(recuNumeroSequence.getCodePayement());
    recuCode.setMontantTotal(recuNumeroSequence.getMontantTotal());
    recuCode.setUser(recuNumeroSequence.getUser());
    return (recuNumeroSequenceRepository.save(recuCode));
  }

}
