package crm.chifco.com.repository;

import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.ImportXlsHistoryFileReclamation;

public interface ImportXlsHistoryFileReclamationRepository
    extends JpaRepository<ImportXlsHistoryFileReclamation, Long> {

  ImportXlsHistoryFileReclamation findByxlsHistoriqueFile(Long id);

  @Query(value = "select cls from ImportXlsHistoryFileReclamation cls "
      + "where ( ( cls.user.userid = :creePar or :creePar is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) " + ") ")
  Page<ImportXlsHistoryFileReclamation> findAllReclamationHistory(Long creePar, Date datedebut,
      Date datefin, Pageable pageable);


}

