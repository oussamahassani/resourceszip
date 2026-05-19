package crm.chifco.com.repository;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
  Produit findProduitByProduitCode(String code);

  Produit findProduitByProduitId(Long id);

  // List<Produit> findProduitsByCategorieProduitInternet_CategorieProduitInternetId(Long
  // categorieid);

  @Query(
      value = "   select   ISNULL(    (   select  ISNULL( p.pourcent_tva , 0)  as pourcenttva    from produits p  where p.produit_code like 'racordement')  , 0)",
      nativeQuery = true)
  Double getPurcentTvaByRacordement();

  @Query(
      value = "   select   ISNULL(    (   select  ISNULL( Tr.prixttc , 0)  as produit_prixttc    from tarification Tr  where Tr.produit_id = :idProduitRacordement)  , 0)",
      nativeQuery = true)
  Double getProduitprixttcByRacordement(Long idProduitRacordement);

  @Query(
      value = "select  ISNULL( p.produit_prixht , 0)  as produit_prixht    from produits p  where p.produit_code like 'racordement'",
      nativeQuery = true)
  Double getProduitprixtHtRacordement();

  List<Produit> findDistinctByIsExtraAndIsActive(boolean isExtra, boolean isActive);

  Produit getFirstProduitByIsDefaultAndIsRacordement(boolean b, boolean c);


  @Modifying
  @Transactional
  @Query(
      value = "UPDATE produits  SET produits.is_active = 1 WHERE produits.produit_id = :produitId ",
      nativeQuery = true)
  void activerProduit(@Param("produitId") Long produitId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE produits  SET produits.is_active = 0 WHERE produits.produit_id = :produitId ",
      nativeQuery = true)
  void desactiverProduit(@Param("produitId") Long produitId);

  List<Produit> findByIsActive(boolean isActive);
}
