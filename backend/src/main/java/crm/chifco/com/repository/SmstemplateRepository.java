package crm.chifco.com.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.Smstemplate;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
@Repository
public interface SmstemplateRepository extends JpaRepository<Smstemplate, Long> {
  Smstemplate findSmstemplateByname(String name);
  
  @Transactional
  @Modifying
  @Query("DELETE FROM Smstemplate")
  void deleteAllSms();
}
