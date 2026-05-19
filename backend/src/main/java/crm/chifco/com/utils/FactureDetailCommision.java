package crm.chifco.com.utils;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FactureDetailCommision {
	public String referenceClient;
    public String referenceFacture;
    public Double montantCommisison;
    public Boolean isEcheance;
    public String commissionId;
    public String createdDate;
    public String modifiedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactureDetailCommision)) return false;
        FactureDetailCommision that = (FactureDetailCommision) o;
        return Objects.equals(referenceClient, that.referenceClient) &&
                Objects.equals(referenceFacture, that.referenceFacture) &&
                Objects.equals(montantCommisison, that.montantCommisison) &&
                Objects.equals(isEcheance, that.isEcheance) &&
                Objects.equals(commissionId, that.commissionId) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceClient, referenceFacture, montantCommisison, isEcheance, commissionId, createdDate, modifiedDate);
    }
}
