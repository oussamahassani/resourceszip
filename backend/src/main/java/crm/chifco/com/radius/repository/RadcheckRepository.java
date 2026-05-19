/*
 * created by hatem ghozzi on 28 10 2022
 */

package crm.chifco.com.radius.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.radius.model.Radcheck;

public interface RadcheckRepository extends JpaRepository<Radcheck, Long> {
  // @Modifying
  // @Transactional
  @Query(
      value = "UPDATE radcheck rch SET rch.value=:expirationDate WHERE rch.username like :username  and rch.attribute like 'Expiration'",
      nativeQuery = true)
  Radcheck updateDateExpiration(@Param("expirationDate") String expirationDate,
      @Param("username") String username);

  @Query("select rch from  Radcheck rch where rch.username like  :username and rch.attribute  like  :attribute ORDER BY rch.id DESC")
  Radcheck findUsernameAndAttribute(@Param("username") String username,
      @Param("attribute") String attribute);



  @Query("select rch from  Radcheck rch where rch.username like  :username")
  List<Radcheck> findUsername(@Param("username") String username);

  @Query(value = "select rch.username from  Radcheck rch where rch.value =  :date")
  List<String> findExpirationByDate(@Param("date") String date);

  @Query(value = "SHOW TABLES", nativeQuery = true)
  List<String> Listetabel();

}
