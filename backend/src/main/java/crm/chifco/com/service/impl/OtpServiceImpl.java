package crm.chifco.com.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.OtpSms;
import crm.chifco.com.repository.OtpRepository;
import crm.chifco.com.service.OtpService;

@Service("OtpService")
public class OtpServiceImpl implements OtpService {

  @Autowired
  OtpRepository otpRepository;

  @Override
  public void saveNewOtpMessage(String number, String OtpCode, Number validity) {
    OtpSms newOtpSms = new OtpSms();
    newOtpSms.setPhoneNumber(number);
    newOtpSms.setOtpValidity(new Date((long) validity));
    newOtpSms.setCodeOtp(OtpCode);

    otpRepository.save(newOtpSms);
  }

  @Override
  public OtpSms findLastOtpSms(String number) {
    return otpRepository.findFirstOtpSmsByPhoneNumberDesc(number);
  }

}
