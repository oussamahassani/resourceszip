package crm.chifco.com.radius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.radius.model.Radusergroup;

public interface RadusergroupRepository extends JpaRepository<Radusergroup, Long> {

  @Query(value = "select * from radusergroup where username = :userName LIMIT 1",
      nativeQuery = true)
  Radusergroup findAllByUsername(String userName);

}
