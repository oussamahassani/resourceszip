package crm.chifco.com.utils;

import java.util.List;
import java.util.Map;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.User;

public final class HtmlTemplateEmail {
  HtmlTemplateEmail() {

  }

  public static String HtmlEmailDemandeModemsRevToAdmin(Long quantiter, String typeModel,
      String revendeurname) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Demande de stock</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>le revendeur :" + revendeurname
        + " a besoin d’un approvisionnement de son stock de modems </p>";
    modelHtml += "<p>la quantité  Demandée  est de " + quantiter
        + " modems . Le type des modems demandés est :  " + typeModel + "  </p>";
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailDemandeModemsPOSToAdmin(Long quantiter, String typeModel,
      String posName) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Demande de stock</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>l'agence nety :" + posName
        + " a besoin d’un approvisionnement de son stock de modems </p>";
    modelHtml += "<p>la quantité  Demandée  est de " + quantiter
        + " modems . Le type des modems demandés est :  " + typeModel + "  </p>";
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailDemandeModemsRevToDist(Long quantiter, String typeModel,
      String revendeurname) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Demande de stock</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>le revendeur :" + revendeurname
        + " a besoin d’un approvisionnement de son stock de modems </p>";
    modelHtml += "<p>la quantité  Demandée  est de " + quantiter
        + " modems .Le type des modems demandés est :  " + typeModel + "  </p>";
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailDemandeModemsDistToAdmin(Long quantiter, String typeModel,
      String userName) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Demande de stock</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>Le chef de secteur :" + userName
        + " a besoin d’un approvisionnement de son stock de modems </p>";
    modelHtml += "<p>la quantité  Demandée  est de " + quantiter
        + " modems .Le type des modems demandés est :  " + typeModel + "  </p>";
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailDemandeModemsDistToAdminGroup(String modemDetails,
      String fullName) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Demande de stock</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>Le chef de secteur :" + fullName
        + " a besoin d’un approvisionnement de son stock de modems </p>";
    modelHtml += "<p><Strong>les quantitées  Demandée sont:</Strong> </p>" + modemDetails;
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlNotificationFactureNonPyaeeRevendeur15jr(
      List<Encaissement> enciaismentNonPayee) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml += "<h2 style='color: #333333; font-family: Arial, sans-serif;'> </h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml +=
        "<p> Vous êtes tenu de verser les factures non versées suivantes , sinon votre compte sera bloqué </p>";
    modelHtml += "<table border='1'>";
    modelHtml +=
        "<tr><th>Référence Facture</th><th>Date de paiement </th><th>Montant à verser</th></tr>";

    for (Encaissement encaisment : enciaismentNonPayee) {
      modelHtml += "<tr>";
      if (encaisment.getFacture() != null) {
        modelHtml += "<td>" + (encaisment.getFacture().getRef_facture()) + "</td>";
      } else if (encaisment.getAvoirClient() != null) {
        modelHtml += "<td>" + (encaisment.getAvoirClient().getRefAvoirClient()) + "</td>";
      }

      modelHtml += "<td>" + encaisment.getDate() + "</td>";
      modelHtml += "<td>" + encaisment.getMontantFacture() + "</td>";
      modelHtml += "</tr>";
    }

    modelHtml += "</table>";

    modelHtml += "</div>";

    return modelHtml;
  }


  public static String HtmlNotificationFactureNonPyaeeRevendeur25jr(
      List<Encaissement> enciaismentNonPayee, String revendeur) {

    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>facture non versée  depuis 25 jours</h2>";
    modelHtml += "<p>Bonjour, + " + revendeur + "</p>";
    modelHtml +=
        "<p>Votre compte NETY a été bloqué pour non versement des factures dans un délai dépassant les 25 jours  </p>";
    modelHtml += "<table border='1'>";
    modelHtml +=
        "<tr><th>Référence Facture</th><th>Date de paiement </th><th>Montant à verser</th></tr>";

    for (Encaissement encaisment : enciaismentNonPayee) {
      modelHtml += "<tr>";
      if (encaisment.getFacture() != null) {
        modelHtml += "<td>" + (encaisment.getFacture().getRef_facture()) + "</td>";
      } else if (encaisment.getAvoirClient() != null) {
        modelHtml += "<td>" + (encaisment.getAvoirClient().getRefAvoirClient()) + "</td>";
      }

      modelHtml += "<td>" + encaisment.getDate() + "</td>";
      modelHtml += "<td>" + encaisment.getMontantFacture() + "</td>";
      modelHtml += "</tr>";
    }

    modelHtml += "</table>";

    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailParrainage(String nomParrain, String client, String identifiant,
      String telephone) {
    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Notification de Parrainage</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>Le parrain <b>" + nomParrain + "</b> vient d'inviter un nouveau client : <b>"
        + client + "</b>.</p>";
    modelHtml += "<p>Qui a un identifiant  : <b>" + identifiant
        + "</b> et numéro de téléphone : </p>" + telephone + "</b>. </p>";
    modelHtml += "<p>Merci de vérifier et valider ce parrainage dans le système.</p>";
    modelHtml += "</div>";
    return modelHtml;
  }

  public static String HtmlEmailReclamation(String nomClient, String typeReclamation,
      String details, String refreclamation) {
    String modelHtml = "<div style='background-color: #f5f5f5; padding: 20px;'>";
    modelHtml +=
        "<h2 style='color: #333333; font-family: Arial, sans-serif;'>Nouvelle Réclamation</h2>";
    modelHtml += "<p>Bonjour,</p>";
    modelHtml += "<p>Le client <b>" + nomClient
        + "</b> a soumis une réclamation sous référence </p>" + refreclamation + "</b>. </p>";
    modelHtml += "<p>Type de réclamation : <b>" + typeReclamation + "</b></p>";
    modelHtml += "<p>Détails : <b>" + details + "</b></p>";
    modelHtml += "<p>Merci de prendre les mesures nécessaires pour traiter cette réclamation.</p>";
    modelHtml += "</div>";
    return modelHtml;
  }

  private static String safe(String value) {
    return value != null ? value : "N/A";
  }

  public static String HtmlEmailTechnicianDistribution(User technician,
      List<Reclamation> reclamations, List<String> gouvernorats) {

    String modelHtml = "<div style='background-color:#f5f5f5;padding:20px;font-family:Arial;'>";

    modelHtml +=
        "<div style='max-width:1200px;margin:0 auto;background:white;border-radius:10px;padding:20px;box-shadow:0 2px 5px rgba(0,0,0,0.1);'>";

    modelHtml +=
        "<div style='background-color:#2c3e50;color:white;padding:15px;border-radius:8px;margin-bottom:20px;'>";
    modelHtml += "<h2 style='margin:0;'>📋 Nouvelles Réclamations Assignées</h2>";
    modelHtml += "</div>";

    modelHtml += "<div style='margin-bottom:20px;'>";
    modelHtml += "<p style='font-size:16px;'><strong>Bonjour " + safe(technician.getFirstName())
        + " " + safe(technician.getLastName()) + ",</strong></p>";
    modelHtml += "<p>Vous avez reçu <strong style='color:#e74c3c;'>" + reclamations.size()
        + "</strong> nouvelle(s) réclamation(s) à traiter.</p>";
    modelHtml += "</div>";

    modelHtml +=
        "<div style='background-color:#ecf0f1;padding:10px;border-radius:5px;margin-bottom:20px;'>";
    modelHtml +=
        "<p><strong>🏢 Gouvernorats assignés:</strong> " + String.join(", ", gouvernorats) + "</p>";
    modelHtml += "</div>";

    modelHtml += "<h3 style='color:#2c3e50;'>📊 Détails des Réclamations</h3>";

    modelHtml +=
        "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:100%;background:white;font-size:14px;'>";

    modelHtml += "<tr style='background-color:#34495e;color:white;'>";
    modelHtml += "<th>#</th>";
    modelHtml += "<th>Réf Réclamation</th>";
    modelHtml += "<th>Réf TT</th>";
    modelHtml += "<th>Client</th>";
    modelHtml += "<th>Tél Fixe</th>";
    modelHtml += "<th>Gouvernorat</th>";
    modelHtml += "<th>Central</th>";
    modelHtml += "<th>Service</th>";
    modelHtml += "<th>Motif</th>";
    modelHtml += "<th>Status</th>";
    modelHtml += "<th>Tél Mobile</th>";
    modelHtml += "<th>Type Abon</th>";
    modelHtml += "</tr>";

    int counter = 1;
    for (Reclamation r : reclamations) {
      String rowColor = counter % 2 == 0 ? "#f9f9f9" : "white";
      modelHtml += "<tr style='background-color:" + rowColor + ";'>";
      modelHtml += "<td style='text-align:center;'>" + counter++ + "</td>";
      modelHtml += "<td><strong>" + safe(r.getRef_reclamation()) + "</strong></td>";
      modelHtml += "<td>" + safe(r.getReferencett()) + "</td>";
      modelHtml += "<td>" + getClientReference(r) + "</td>";
      modelHtml += "<td>" + getClientPhone(r) + "</td>";
      modelHtml += "<td>" + safe(r.getGouvernorat()) + "</td>";
      modelHtml += "<td>" + safe(r.getCentral()) + "</td>";
      modelHtml += "<td>" + getServiceType(r) + "</td>";
      modelHtml += "<td>" + getMotif(r) + "</td>";
      modelHtml += "<td>" + getStatus(r) + "</td>";
      modelHtml += "<td>" + r.getClient().getTelMobile() + "</td>";
      modelHtml += "<td>"
          + r.getClient().getPack().getCategoriePack().getCategorieProduitInternetCode() + "</td>";
      modelHtml += "<tr>";
    }

    modelHtml += "</table>";
    modelHtml += "<br><p style='color:gray;font-size:12px;text-align:center;margin-top:20px;'>";
    modelHtml +=
        "📧 Email généré automatiquement par le système CRM. Merci de ne pas répondre à cet email.<br>";
    modelHtml +=
        "⏰ Ce rappel est envoyé quotidiennement à 8h00 pour les réclamations non traitées.";
    modelHtml += "</p>";

    modelHtml += "</div>";
    modelHtml += "</div>";

    return modelHtml;
  }

  public static String HtmlEmailDistributionSummary(Map<User, List<Reclamation>> distributionMap) {
    String modelHtml = "<div style='background-color:#f5f5f5;padding:20px;font-family:Arial;'>";

    modelHtml +=
        "<div style='max-width:800px;margin:0 auto;background:white;border-radius:10px;padding:20px;'>";
    modelHtml += "<h2 style='color:#2c3e50;'>📊 Rapport de Distribution des Réclamations</h2>";
    modelHtml += "<p>La distribution des réclamations a été effectuée avec succès.</p>";

    modelHtml +=
        "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:100%;'>";
    modelHtml += "<tr style='background-color:#34495e;color:white;'>";
    modelHtml += "<th>Technicien</th>";
    modelHtml += "<th>Email</th>";
    modelHtml += "<th>Nombre de Réclamations</th>";
    modelHtml += "<th>Gouvernorats</th>";
    modelHtml += "</tr>";

    for (Map.Entry<User, List<Reclamation>> entry : distributionMap.entrySet()) {
      User tech = entry.getKey();
      List<Reclamation> recs = entry.getValue();
      modelHtml += "<tr>";
      modelHtml += "<td>" + safe(tech.getFirstName()) + " " + safe(tech.getLastName()) + "</td>";
      modelHtml += "<td>" + safe(tech.getEmail()) + "</td>";
      modelHtml += "<td style='text-align:center;'>" + recs.size() + "</td>";
      modelHtml += "<td>" + recs.stream().map(Reclamation::getGouvernorat).distinct()
          .collect(java.util.stream.Collectors.joining(", ")) + "</td>";
      modelHtml += "</tr>";
    }

    modelHtml += "</table>";
    modelHtml += "<br><p style='color:gray;'>Email généré automatiquement par le système CRM.</p>";
    modelHtml += "</div></div>";

    return modelHtml;
  }



  private static String getClientReference(Reclamation r) {
    if (r.getClient() != null && r.getClient().getReferenceClient() != null) {
      return r.getClient().getReferenceClient();
    }
    return "N/A";
  }

  private static String getClientPhone(Reclamation r) {
    if (r.getClient() != null && r.getClient().getTelFixe() != null) {
      return String.valueOf(r.getClient().getTelFixe());
    }
    return "N/A";
  }

  private static String getServiceType(Reclamation r) {
    return (r.getServiceType() != null && r.getServiceType().getCategorytype() != null)
        ? r.getServiceType().getCategorytype()
        : "N/A";
  }

  private static String getMotif(Reclamation r) {
    return (r.getMotif() != null && r.getMotif().getNomMotif() != null) ? r.getMotif().getNomMotif()
        : "N/A";
  }

  private static String getStatus(Reclamation r) {
    return (r.getStatus() != null && r.getStatus().getNomStatut() != null)
        ? r.getStatus().getNomStatut()
        : "N/A";
  }

  private static String formatDate(java.util.Date date) {
    if (date == null)
      return "N/A";
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
    return sdf.format(date);
  }


}
