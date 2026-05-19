package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.RecuNumeroSequence;

public interface RecuNumeroSequenceRepository extends JpaRepository<RecuNumeroSequence, Long> {

  RecuNumeroSequence findByCodePayement(String codePayement);

  @Query(value = "select Count(*) from recu_numero_sequence re  where re.user_id = :userId ",
      nativeQuery = true)
  Long countRecuByCodeUser(@Param("userId") Long userId);

}
