package crm.chifco.com.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.EntryFactures;

public interface EntriesfacturesRepository extends JpaRepository<EntryFactures, Long> {


}
