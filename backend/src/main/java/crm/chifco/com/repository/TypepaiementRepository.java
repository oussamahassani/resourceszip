package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import crm.chifco.com.model.Typepaiement;

public interface TypepaiementRepository extends JpaRepository<Typepaiement, Long> {
  Typepaiement findTypepaiementByreferenceTypePaiement(String ref);
}
