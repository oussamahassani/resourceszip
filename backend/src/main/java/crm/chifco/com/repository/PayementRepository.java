package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.ApiDTO.PaymentDTOApi;
import crm.chifco.com.DTOclass.PayementDataDTO;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Payement;

public interface PayementRepository extends JpaRepository<Payement, Long> {
  Payement findPayementByfacture(Facture facture);

  Payement findPayementByfacture_factureId(Long factureId);

  @Query(value = "select Count(*) from payement py  where py.code_payement like %:codeuser%  ",
      nativeQuery = true)
  Long countuser(@Param("codeuser") String codeuser);

  @Query(
      value = "select facture_id from payement pay where pay.recu_numero_sequence_id = :codeRecuId",
      nativeQuery = true)
  List<Long> factureByCodeRecuId(@Param("codeRecuId") Long codeRecuId);

  @Query(
      value = "select avoir_client_id from payement pay where pay.recu_numero_sequence_id = :codeRecuId ",
      nativeQuery = true)
  List<Long> FactureAvoirClientByCodeRecuId(@Param("codeRecuId") Long codeRecuId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE payement  SET payement.recu_numero_sequence_id = :codeRecuId WHERE payement.payementid = :payementId  ",
      nativeQuery = true)
  void updateCodeRecu(@Param("payementId") Long payementId, @Param("codeRecuId") Long codeRecuId);

  @Query(
      value = "select  TOP 1 facture_id from payement where recu_numero_sequence_id = (select recu_numero_sequence_id from payement p where avoir_client_id = :avoirId)",
      nativeQuery = true)
  Long getFacutreIdByAvoirId(Long avoirId);

  @Query(value = "select * from payement py  where py.avoir_client_id = :avoirId  ",
      nativeQuery = true)
  Payement findPayementByAvoirId(String avoirId);

  @Query(
      value = "SELECT CASE WHEN EXISTS (SELECT * FROM payement WHERE avoir_client_id in :avoirList) THEN  CAST(1 AS BIT) ELSE CAST(0 AS BIT)  END AS result_exists; ",
      nativeQuery = true)
  Boolean findIfAnyPayementExisteWithAvoir(List<String> avoirList);

  @Query(
      value = "SELECT CASE WHEN EXISTS (SELECT * FROM payement WHERE facture_id in :factureList) THEN  CAST(1 AS BIT) ELSE CAST(0 AS BIT)  END AS result_exists; ",
      nativeQuery = true)
  Boolean findIfAnyPayementExisteWithFacture(List<String> factureList);

  @Query("SELECT NEW crm.chifco.com.DTOclass.PayementDataDTO(" + "entry.payementid, "
      + "entry.montant, " + "entry.typePayment, "
      + "entry.facture.factureId,entry.AvoirClient.avoirId,entry.createdDate,entry.modifiedDate,entry.ischifcoPayed"
      + ",entry.numeroCarte,entry.numeroCheque,entry.nomBank ,entry.user.lastName , entry.user.firstName, entry.user.codeUser) "
      + "FROM Payement entry ")
  List<PayementDataDTO> findPayementDataDTOAll();

  @Query("SELECT NEW crm.chifco.com.DTOclass.PayementDataDTO(" + "entry.payementid, "
      + "entry.montant, " + "entry.typePayment, "
      + "entry.facture.factureId,entry.AvoirClient.avoirId,entry.createdDate,entry.modifiedDate,entry.ischifcoPayed"
      + ",entry.numeroCarte,entry.numeroCheque,entry.nomBank ,entry.user.lastName , entry.user.firstName, entry.user.codeUser) "
      + "FROM Payement entry where entry.facture is not null and entry.facture.factureId IN :factureList ")
  List<PayementDataDTO> findPayementDataDTOByIds(List<Long> factureList);

  /*
   * @Query("SELECT NEW crm.chifco.com.ApiDTO(" + "entry.montant, " + "entry.typePayment, " +
   * "entry.facture.ref_facture,entry.AvoirClient.refAvoirClient,entry.createdDate,entry.modifiedDate,entry.ischifcoPayed"
   * +
   * ",entry.numeroCarte,entry.numeroCheque,entry.nomBank ,entry.user.lastName , entry.user.firstName, entry.user.codeUser) "
   * +
   * "FROM Payement entry where (entry.transactionId = :transactionId or :transactionId is null) and"
   * + "(entry.typePayment like concat('%', :frournisseur, '%') or :frournisseur is null) and " +
   * " (entry.createdDate >= :dateDebut or :dateDebut is null) and " +
   * "(entry.createdDate <= :dateFin or :dateFin is null) ") List<PaymentDTOApi>
   * findPayementByFilterApi(String dateDebut, String dateFin, String transactionId, String
   * frournisseur);
   */
  @Query("SELECT NEW crm.chifco.com.ApiDTO.PaymentDTOApi(" + "entry.montant, "
      + "entry.typePayment, " + "entry.facture.ref_facture, " + "avoir.refAvoirClient, " + // corrigé
                                                                                           // ici
      "entry.createdDate, " + "entry.modifiedDate, " + "entry.numeroCarte, "
      + "entry.numeroCheque, " + "entry.nomBank) " + "FROM Payement entry "
      + "LEFT JOIN entry.AvoirClient avoir "
      + "WHERE (:transactionId IS NULL OR entry.transactionId = :transactionId) "
      + "AND (:frournisseur IS NULL OR entry.typePayment LIKE CONCAT('%', :frournisseur, '%')) "
      + "AND (:dateDebut IS NULL OR entry.createdDate >= :dateDebut) " + "AND (:dateFin "
      + "IS NULL OR entry.createdDate <= :dateFin)")

  List<PaymentDTOApi> findPayementByFilterApi(@Param("dateDebut") Date dateDebut,
      @Param("dateFin") Date dateFin, @Param("transactionId") String transactionId,
      @Param("frournisseur") String frournisseur);

}
