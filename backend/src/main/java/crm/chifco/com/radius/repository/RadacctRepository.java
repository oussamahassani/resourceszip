/*
 * created by hatem ghozzi on 28 10 2022
 */

package crm.chifco.com.radius.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.radius.model.Radacct;

public interface RadacctRepository extends JpaRepository<Radacct, Long> {

  @Query(value = "select * from radacct where username = :username and acctstoptime is NULL",
      nativeQuery = true)
  List<Radacct> findUsernameAndAttribute(@Param("username") String username);

  @Query(
      value = "select radacctid, username,acctstarttime, acctstoptime from radacct where username = :username AND acctstarttime BETWEEN :DateCreation AND :DateNow ORDER BY acctstarttime  DESC",
      nativeQuery = true)
  List<Radacct> findSessionByUsername(@Param("username") String username,
      @Param("DateCreation") String DateCreation, @Param("DateNow") String DateNow);

  @Query(value = "select * from radacct where username = :username LIMIT 1", nativeQuery = true)
  Radacct findTop1ByUserName(@Param("username") String username);

  @Query(
      value = "select radacctid, username,acctstarttime, acctstoptime from radacct where username = :username  ORDER BY acctstarttime  ASC LIMIT 1",
      nativeQuery = true)
  Radacct findFirstSessionByUsername(@Param("username") String username);

  @Query(
      value = "select radacctid, username,acctstarttime, acctstoptime from radacct where username = :loginModem and YEAR(acctstarttime) = YEAR(:formattedCurrentYearMonth)  and MONTH(acctstarttime) = MONTH(:formattedCurrentYearMonth)  ORDER BY acctstarttime  DESC LIMIT 1",
      nativeQuery = true)
  Radacct getRadacctConnectionToClaculateFraisTTAndDateDeConnection(String loginModem,
      String formattedCurrentYearMonth);

  @Query(
      value = "select DISTINCT  username from radacct where YEAR(acctstarttime) = YEAR(:formattedCurrentYearMonth)  and MONTH(acctstarttime) = MONTH(:formattedCurrentYearMonth)  ORDER BY acctstarttime  DESC ",
      nativeQuery = true)
  List<String> getListeConnectionToClaculateFraisTTAndDateDeConnection(
      String formattedCurrentYearMonth);


  @Query(
      value = "select radacctid,username,acctstarttime, acctstoptime from radacct where YEAR(acctstarttime) = YEAR(:formattedCurrentYearMonth)  and MONTH(acctstarttime) = MONTH(:formattedCurrentYearMonth)  and username = :username  ORDER BY acctstarttime  asc LIMIT 1",
      nativeQuery = true)
  Radacct getLastConnectionToClaculateFraisServiceTTIfIsResilation(String formattedCurrentYearMonth,
      String username);

  @Query(value = "SELECT COUNT(*) FROM radacct WHERE framedipaddress = :ipAddress",
      nativeQuery = true)
  int existsByIpAddress(@Param("ipAddress") String ipAddress);

  @Query(
      value = "SELECT username FROM radacct WHERE (framedipaddress = :ipAddress and acctstoptime IS NULL) order by acctstarttime desc",
      nativeQuery = true)
  String getUsernameexistsByIpAddress(@Param("ipAddress") String ipAddress);
}
