package crm.chifco.com.controller;

public class ScriptBD {
  // insert user system
  // SELECT * FROM sys.sequences
  // ALTER SEQUENCE codeClientSeq Restart with 0000005
  String addUserSsytem = "INSERT INTO users (email,enabled,first_name"
      + ",last_name ,password  ,photo  ,role_id   ,createdbyuserid"
      + ",createddate  ,modifieddate   ,activiteprincipale   ,regimefiscal ,adresse"
      + " ,codepostale  ,coordonneesbancaires,pcfacturerecurent ,pc_first_facture"
      + " ,plafonrevendeur  ,telephone   ,withstock" + ",gouvernoratid ,villeid ,is_locked"
      + ",carte_fiscale  ,contrat ,rne" + ",type_user ,classuser ,identificationfiscale"
      + ",formejuridique ,cin)" + "VALUES" + "( system@chifco.com ,1"
      + " ,system ,CRM,$2a$10$FCCpWnpqdv0eA/Jrj6hmoO3YqUDqpvW5I8zPIak4LZH1aIcWyLY.C"
      + ",NULL ,13,257" + " ,2022-11-07 09:37:57.2295312  ,2022-11-07 09:37:57.2295312 ,NULL"
      + " ,NULL, NULL,NULL" + " ,NULL ,NULL" + "  ,NULL  ,NULL" + "  ,NULL ,1  ,NULL ,NULL"
      + " ,NULL,NULL,NULL" + "  ,<rne, varchar(255),>   ,SYSTEM" + " ,NULL  ,NULL"
      + "  ,NULL  ,11111111)";

  String addUserAcs = "INSERT INTO users (email,enabled,first_name"
      + ",last_name ,password  ,photo  ,role_id   ,createdbyuserid"
      + ",createddate  ,modifieddate   ,activiteprincipale   ,regimefiscal ,adresse"
      + " ,codepostale  ,coordonneesbancaires,pcfacturerecurent ,pc_first_facture"
      + " ,plafonrevendeur  ,telephone   ,withstock" + ",gouvernoratid ,villeid ,is_locked"
      + ",carte_fiscale  ,contrat ,rne" + ",type_user ,classuser ,identificationfiscale"
      + ",formejuridique ,cin)" + "VALUES" + "( ACS@chifco.com ,1"
      + " ,ACS ,CRM,$2a$10$FCCpWnpqdv0eA/Jrj6hmoO3YqUDqpvW5I8zPIak4LZH1aIcWyLY.C" + ",NULL ,13,257"
      + " ,2022-11-07 09:37:57.2295312  ,2022-11-07 09:37:57.2295312 ,NULL" + " ,NULL, NULL,NULL"
      + " ,NULL ,NULL" + "  ,NULL  ,NULL" + "  ,NULL ,1  ,NULL ,NULL" + " ,NULL,NULL,NULL"
      + "  ,<rne, varchar(255),>   ,SYSTEM" + " ,NULL  ,NULL" + "  ,NULL  ,00000001)";
  String addCategoryRaccordement = "INSERT INTO categorieproduitinternet" + "           ("
      + "           ,categorieproduitinternetcode" + "           ,categorieproduitinternetnom"
      + "           ,createddate" + "           ,modifieddate)" + "     VALUES"
      + "           (racordement" + "           ,racordement"
      + "           ,2022-11-07 15:22:34.0703820" + "           ,2022-11-07 15:22:34.0703820)";

  String addProduitRaccordement = "INSERT INTO produits"
      + "           (produitcode ,produitdebit ,produitnom ,produitprixannuel"
      + "           ,categorieproduitinternetid,createddate ,modifieddate"
      + "           ,produitprix,remise  ,pourcent_tva"
      + "           ,produitprixttc,produit_prixht" + "           ,comandeid_id)" + "     VALUES"
      + "           (<produitid, bigint,>   ,<produitcode, varchar(255),>"
      + "           ,<produitdebit, bigint,>  ,<produitnom, varchar(255),>"
      + "           ,<produitprixannuel, bigint,>  ,<categorieproduitinternetid, bigint,>"
      + "           ,<createddate, datetime2(7),> ,<modifieddate, datetime2(7),>  ,<produitprix, float,>"
      + "           ,<remise, float,>    ,<pourcent_tva, float,>    ,<produitprixttc, float,>"
      + "           ,<produit_prixht, float,>   ,<comandeid_id, bigint,>)";

  String updateCoordoneeBancairesUSER =
      "ALTER TABLE users ALTER COLUMN coordonneesbancaires VARCHAR (115)";

  String createSequence = "CREATE SEQUENCE codeClientSeq START WITH 0000001  INCREMENT BY 1";
  String factureSequence = "CREATE SEQUENCE factureSeq  START WITH 00000001  INCREMENT BY 1";
  String ClientReferenceSeq =
      "CREATE SEQUENCE ClientReferenceSeq START WITH 100000  INCREMENT BY 1";
  String recuSeq = "CREATE SEQUENCE recuSeq START WITH 1  INCREMENT BY 1";

}
