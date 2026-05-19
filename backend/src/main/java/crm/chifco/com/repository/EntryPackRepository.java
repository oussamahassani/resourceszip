package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Pack;
import crm.chifco.com.radius.model.Radcheck;


public interface EntryPackRepository extends JpaRepository<EntryPack, Long> {

  List<EntryPack> getEntryPackByPack(Pack pack);
  
  EntryPack getEntryBypack_packIdAndProduit_produitId(Long packId ,Long produitId );
  
  @Query("select new EntryPack(e.entryPackId , e.produit, e.showProduitFacture) from EntryPack e where  e.pack.packId  = :packId")
      List<EntryPack> getEntryPackByPackId(Long packId);

  
  @Modifying
  @Transactional
  @Query(
      value = "DELETE FROM entry_pack WHERE pack_id = :idpack",
      nativeQuery = true)
  void deleteEntryPack(@Param("idpack") Long idpack);
}
