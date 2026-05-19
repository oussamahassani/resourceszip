package crm.chifco.com.service;

import crm.chifco.com.model.OtpSms;

public interface OtpService {

  void saveNewOtpMessage(String number , String OtpCode , Number validity) ; 
  
  OtpSms findLastOtpSms(String number );
}
