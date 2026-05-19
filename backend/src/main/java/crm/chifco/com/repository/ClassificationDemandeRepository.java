package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ClassificationDemande;

public interface ClassificationDemandeRepository
    extends JpaRepository<ClassificationDemande, Long> {

  ClassificationDemande findClassificationDemandeByValue(String value);

  ClassificationDemande findClassificationDemandeByCodeClassification(String code);
}
