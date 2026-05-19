package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "otpSms")
@EntityListeners(AuditingEntityListener.class)
public class OtpSms implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "otpId")
  private Long otpId;


  @Column(length = 180)
  private String codeOtp;

  @Column(length = 180)
  private String phoneNumber;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;


  @Temporal(TemporalType.TIMESTAMP)
  private Date otpValidity;

  public OtpSms() {
    super();
    // TODO Auto-generated constructor stub
  }

  public OtpSms(Long otpId, String codeOtp, String phoneNumber, Date createdDate,
      Date otpValidity) {
    super();
    this.otpId = otpId;
    this.codeOtp = codeOtp;
    this.phoneNumber = phoneNumber;
    this.createdDate = createdDate;
    this.otpValidity = otpValidity;
  }

  public Long getOtpId() {
    return otpId;
  }

  public void setOtpId(Long otpId) {
    this.otpId = otpId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getOtpValidity() {
    return otpValidity;
  }

  public void setOtpValidity(Date otpValidity) {
    this.otpValidity = otpValidity;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getCodeOtp() {
    return codeOtp;
  }

  public void setCodeOtp(String codeOtp) {
    this.codeOtp = codeOtp;
  }

  @Override
  public String toString() {
    return "OtpSms [otpId=" + otpId + ", codeOtp=" + codeOtp + ", phoneNumber=" + phoneNumber
        + ", createdDate=" + createdDate + ", otpValidity=" + otpValidity + "]";
  }



}
