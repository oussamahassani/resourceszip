package crm.chifco.com.ApiDTO;
import java.math.BigDecimal;

public class ManualPaymentRequest {
    private String contractId;
    private String type; // PAYMENT_CASH or PAYMENT_TRANSFER
    private BigDecimal amount;
    
    
    
	public ManualPaymentRequest(String contractId, String type, BigDecimal amount) {
		super();
		this.contractId = contractId;
		this.type = type;
		this.amount = amount;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
    
    
}