package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.Offre;

public interface OffreRepository extends JpaRepository<Offre, Long> {

  Page<Offre> findAll(Pageable pageable);

  Offre findByOffreId(Long offre);

  List<Offre> findAllOffreByIdOffreBase(Long IdOffreBase);

  List<Offre> findAllOffreByIdOffreBaseAndOffreIdNot(Long IdOffreBase, Long Id);

  List<Offre> findAllOffreByIsActive(boolean b);

  @Query(" SELECT a FROM Offre a  WHERE EXISTS (SELECT 1 FROM Pack b WHERE a.offreId = b.offre.offreId) and a.isActive = true")
  List<Offre> findAllOffreExisteInPack();

  @Query("SELECT o FROM Offre o WHERE ((o.isPromo = TRUE AND o.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND o.dateFinPromo >= CONVERT(DATE, GETDATE())) "
      + "OR(o.idOffreBase IS NULL AND (NOT EXISTS (SELECT child FROM Offre child WHERE child.idOffreBase = o.offreId AND child.isPromo = true AND child.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND child.dateFinPromo >= CONVERT(DATE, GETDATE())) OR ( o.dateFinPromo IS NULL OR o.dateFinPromo <= CONVERT(DATE, GETDATE()))))) "
      + "AND (EXISTS (SELECT pack1 from Pack pack1 WHERE o.offreId = pack1.offre.offreId)) "
      + "AND o.isPrivate = FALSE   AND (o.isRevSelected = FALSE or o.isRevSelected is null )  AND o.isActive = TRUE "
      + "AND NOT EXISTS ( SELECT child FROM Offre child WHERE child.idOffreBase = o.offreId AND child.isPromo = TRUE AND child.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND child.dateFinPromo >= CONVERT(DATE, GETDATE())"
      + ")")
  List<Offre> findAllOffreExisteInPackByVisibility();

  @Query("select p from Offre p where " + "((:offre is null or p.title like %:offre%  ) "
      + "and (p.isActive = :etat or :etat is null ) "
      + "and (p.isPromo = :Promotion or :Promotion is null ) "
      + "and (p.createdDate >= :datedebuts or :datedebuts is null) "
      + "and (p.createdDate <= :datefins or :datefins is null)"
      + "and (p.dateDebutPromo >= :datedebutsPromo or :datedebutsPromo is null)"
      + "and (p.dateDebutPromo <= :datefinsPromo or :datefinsPromo is null)" + ")")
  Page<Offre> findListPackFilter(String offre, Boolean etat, Boolean Promotion, Date datedebuts,
      Date datefins, Date datedebutsPromo, Date datefinsPromo, Pageable pageable);

  @Query("SELECT o FROM Offre o WHERE ((o.isPromo = TRUE AND o.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND o.dateFinPromo >= CONVERT(DATE, GETDATE())) "
      + "OR(o.idOffreBase IS NULL AND (NOT EXISTS (SELECT child FROM Offre child WHERE child.idOffreBase = o.offreId AND child.isPromo = true AND child.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND child.dateFinPromo >= CONVERT(DATE, GETDATE())) OR ( o.dateFinPromo IS NULL OR o.dateFinPromo <= CONVERT(DATE, GETDATE()))))) "
      + "AND (EXISTS (SELECT pack1 from Pack pack1 WHERE o.offreId = pack1.offre.offreId)) "
      + "AND ((o.isPrivate = FALSE AND o.isActive = TRUE )  OR (o.isPrivate = FALSE AND o.isActive = TRUE  and o.isRevSelected = true) )"
      + "AND NOT EXISTS ( SELECT child FROM Offre child WHERE child.idOffreBase = o.offreId AND child.isPromo = TRUE AND child.dateDebutPromo <= CONVERT(DATE, GETDATE()) AND child.dateFinPromo >= CONVERT(DATE, GETDATE())"
      + ")")
  List<Offre> findAllOffreExisteInPackByRevSelectedAndVisibility();


}
