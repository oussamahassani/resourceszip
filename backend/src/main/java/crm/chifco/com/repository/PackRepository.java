package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.DTOclass.PackDto;
import crm.chifco.com.DTOclass.PackDto2;
import crm.chifco.com.model.Pack;

public interface PackRepository extends JpaRepository<Pack, Long> {

  List<Pack> getPackSByOffre_offreIdOrderByDebitPackAsc(Long offreid);

  List<Pack> getPackSByCategoriePack_categorieProduitInternetId(Long categoryid);

  Pack getPackSByCodePack(String codePack);

  Pack getBypackId(Long idPackBase);

  List<Pack> findPackByIdPackBase(Long idPackBase);

  @Query(
      value = "SELECT p.pack_id,p.code_pack,p.description,p.debit_pack,p.title,cat.categorie_produit_internet_code,"
          + "o.offre_id FROM Pack p Left JOIN offre o on p.offre_id=o.offre_id left join "
          + "categorieproduitinternet cat on cat.categorie_produit_internet_id=p.categorie_pack "
          + "where (o.offre_id = :offreId AND cat.categorie_produit_internet_nom != :preventpack AND cat.categorie_produit_internet_nom !='ADSL' ) ",
      nativeQuery = true)
  List<PackDto> findPacksWithDifferentCategoryInSameOffre(@Param("preventpack") String preventpack,
      @Param("offreId") Long offreId);


  @Query(
      value = "SELECT p FROM Pack p LEFT JOIN FETCH p.offre o LEFT JOIN FETCH p.categoriePack cat WHERE o.offreId = :offreId AND cat.categorieProduitInternetNom != :preventpack")
  List<Pack> findPacksWithDifferentCategoryInSameOffreEager(
      @Param("preventpack") String preventpack, @Param("offreId") Long offreId);

  @Query(
      value = "SELECT p FROM Pack p LEFT JOIN FETCH p.offre o LEFT JOIN FETCH p.categoriePack cat WHERE o.offreId = :offreId AND cat.categorieProduitInternetNom !='ADSL'")
  List<Pack> findPacksWithCategoryInSameOffreEager(@Param("offreId") Long offreId);

  @Query(
      value = "SELECT p FROM Pack p LEFT JOIN FETCH p.offre o LEFT JOIN FETCH p.categoriePack cat WHERE o.offreId = :offreId AND cat.categorieProduitInternetNom =:category")
  List<Pack> findPacksWithCategoryInSameOffreEagerSameCategory(@Param("offreId") Long offreId,
      String category);

  @Query(
      value = "SELECT p FROM Pack p LEFT JOIN FETCH p.offre o LEFT JOIN FETCH p.categoriePack cat WHERE cat.categorieProduitInternetNom =:category")
  List<Pack> findPacksWithSameCategory(String category);

  @Query("SELECT p FROM Pack p " + "LEFT JOIN p.offre o " + "LEFT JOIN FETCH p.categoriePack cat "
      + "WHERE cat.categorieProduitInternetNom = :category " + "AND o.isPrivate = false "
      + "AND o.title = :offre")
  List<Pack> findPacksWithSameCategoryNotPrivate(@Param("category") String category,
      @Param("offre") String offre);

  @Query(
      value = "SELECT p.pack_id,p.code_pack,p.description,p.debit_pack,p.title,cat.categorie_produit_internet_code,"
          + "o.offre_id FROM Pack p Left JOIN offre o on p.offre_id=o.offre_id left join "
          + "categorieproduitinternet cat on cat.categorie_produit_internet_id=p.categorie_pack "
          + "where (o.offre_id = :offreId AND cat.categorie_produit_internet_nom = :preventpack) ",
      nativeQuery = true)
  List<PackDto> findPacksWithSameCategoryInSameOffre(@Param("preventpack") String preventpack,
      @Param("offreId") Long offreId);

  @Query("select p.packId as packId,p.offre.title as offre, p.title as title, p.description as description, "
      + "p.createdDate as createdDate, p.categoriePack.categorieProduitInternetNom as categoriePack, "
      + "t.prixTTc as prixTTc from Pack p left join Tarification t on t.packId=p.packId where "
      + "((:titre is null or p.title like %:titre%) and (p.offre.title = :forfait or :forfait is null) "
      + "and (p.categoriePack.categorieProduitInternetId = :categories or :categories is null) "
      + "and (p.createdDate >= :datedebuts or :datedebuts is null) "
      + "and (p.createdDate <= :datefins or :datefins is null))")
  Page<PackDto2> findListPackFilter(String forfait, String titre, Long categories, Date datedebuts,
      Date datefins, Pageable pageable);

}
