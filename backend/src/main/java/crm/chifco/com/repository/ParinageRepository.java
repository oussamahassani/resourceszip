package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import crm.chifco.com.ApiDTO.ParinageDTO;
import crm.chifco.com.model.Parinage;

@Repository
public interface ParinageRepository extends JpaRepository<Parinage, Long> {


  @Query(
      value = "select pr from Parinage pr " + "where ( ( pr.statut = :status or :status is null ) "
          + "and ( pr.createdDate >= :datedebut or :datedebut is null )"
          + " and (pr.createdDate <= :datefin or :datefin is null ) )")
  Page<Parinage> findAllDemandeParinageWithFilter(Pageable pageable, Date datedebut, Date datefin,
      String status);

  @Query(value = "select pr from Parinage pr "
      + "where ( ( pr.statut = :statutfiltre or :statutfiltre is null ) "
      + "and ( pr.createdDate >= :datedebut or :datedebut is null )"
      + " and (pr.createdDate <= :datefin or :datefin is null ) )")
  List<Parinage> findAllDemandeParinageWithFilterXls(Date datedebut, Date datefin,
      String statutfiltre);


 @Query("select new crm.chifco.com.ApiDTO.ParinageDTO(" +
	       "pr.referenceParinage, pr.cinParrain, pr.cinParinee, pr.statut, " +
	       "pr.nomParrain, pr.nomParinee, pr.telFixe, pr.createdDate , pr.email) " +
	       "from Parinage pr " +
	       "where ( ( pr.statut = :statutfiltre or :statutfiltre is null ) " +
	       "and ( pr.cinParrain = :cinParrain or :cinParrain is null ) " +
	       "and ( pr.createdDate >= :datedebut or :datedebut is null ) " +
	       "and ( pr.createdDate <= :datefin or :datefin is null ) )")
	List<ParinageDTO> findAllDemandeParinageWithFilterForApp(
	        @Param("datedebut") Date datedebut,
	        @Param("datefin") Date datefin,
	        @Param("cinParrain") String cinParrain,
	        @Param("statutfiltre") String statut);


}
