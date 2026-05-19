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
@Table(name = "ClicToPay")
@EntityListeners(AuditingEntityListener.class)
public class ClicToPay implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "clicToPayId")
  private Long clicToPayId;

  private String bordereau;

  private String orderId;

  private String errorMessage;

  private String errorCode;

  private String orderNumber;

  private String approvalCode;
  private String provider;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  private Boolean isPassed;

  public ClicToPay() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Long getClicToPayId() {
    return clicToPayId;
  }

  public void setClicToPayId(Long clicToPayId) {
    this.clicToPayId = clicToPayId;
  }

  public String getBordereau() {
    return bordereau;
  }

  public void setBordereau(String bordereau) {
    this.bordereau = bordereau;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Boolean getIsPassed() {
    return isPassed;
  }

  public void setIsPassed(Boolean isPassed) {
    this.isPassed = isPassed;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getApprovalCode() {
    return approvalCode;
  }

  public void setApprovalCode(String approvalCode) {
    this.approvalCode = approvalCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getProvider() {
	return provider;
}

public void setProvider(String provider) {
	this.provider = provider;
}

@Override
  public String toString() {
    return "ClicToPay [clicToPayId=" + clicToPayId + ", bordereau=" + bordereau + ", orderId="
        + orderId + ", errorMessage=" + errorMessage + ", createdDate=" + createdDate
        + ", isPassed=" + isPassed + "]";
  }

}
