import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { usePermissions } from '../../hooks/usePermissions'

const SidebarItem = ({ to, children, icon }) => {
  const location = useLocation()
  const isActive = location.pathname.startsWith(to)
  return (
    <li className={`nav-item${isActive ? ' menu-open' : ''}`}>
      <Link to={to} className={`nav-link${isActive ? ' active' : ''}`}>
        {icon && <i className={`nav-icon ${icon}`}></i>}
        <p>{children}</p>
      </Link>
    </li>
  )
}

const SidebarGroup = ({ label, icon, children }) => {
  const location = useLocation()
  const [open, setOpen] = useState(false)

  const hasActive = React.Children.toArray(children).some(child => {
    if (!child?.props?.to) return false
    return location.pathname.startsWith(child.props.to)
  })

  return (
    <li className={`nav-item${open || hasActive ? ' menu-open' : ''}`}>
      <a href="#" className={`nav-link${hasActive ? ' active' : ''}`}
        onClick={(e) => { e.preventDefault(); setOpen(o => !o) }}>
        <i className={`nav-icon ${icon}`}></i>
        <p>{label} <i className="right fas fa-angle-left"></i></p>
      </a>
      <ul className="nav nav-treeview" style={{ display: open || hasActive ? 'block' : 'none' }}>
        {children}
      </ul>
    </li>
  )
}

const SidebarSubItem = ({ to, children }) => {
  const location = useLocation()
  const isActive = location.pathname === to || location.pathname.startsWith(to + '/')
  return (
    <li className="nav-item">
      <Link to={to} className={`nav-link${isActive ? ' active' : ''}`}>
        <p>{children}</p>
      </Link>
    </li>
  )
}

const Sidebar = () => {
  const { hasAnyAuthority } = usePermissions()

  return (
    <aside className="main-sidebar sidebar-light-primary elevation-4">
      <Link to="/" className="brand-link">
        <img src="/img/logo chifco.png" alt="CHIFCO" className="brand-image" style={{ opacity: 0.8 }} />
      </Link>

      <div className="sidebar">
        <nav className="mt-2">
          <ul className="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">

            {hasAnyAuthority('VIEW_DASHBORD_ADMIN') && (
              <SidebarItem to="/admin/dashboard" icon="fas fa-tachometer-alt">Tableau de Bord</SidebarItem>
            )}
            {hasAnyAuthority('VIEW_DASHBORD_OTHER') && (
              <SidebarItem to="/alluser/dashboard" icon="fas fa-tachometer-alt">Tableau de Bord</SidebarItem>
            )}

            {hasAnyAuthority('READ_USER_LIST', 'READ_RETAIL_LIST', 'READ_USER_SYSTEM', 'READ_RETAIL_LIST__AREA', 'ADD_USER') && (
              <SidebarGroup label="Gestion des utilisateurs" icon="fas fa-users">
                {hasAnyAuthority('READ_USER_SYSTEM') && (
                  <SidebarSubItem to="/admin/usersSystem/getusers">Liste des utilisateurs système</SidebarSubItem>
                )}
                {hasAnyAuthority('ADD_USER') && (
                  <SidebarSubItem to="/admin/users/add-user">Ajouter un utilisateur</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_USER_LIST') && (
                  <SidebarSubItem to="/admin/users/allusers/1">Liste des utilisateurs</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_RETAIL_LIST', 'READ_RETAIL_LIST__AREA') && (
                  <SidebarSubItem to="/RevendeurUser/revendeurliste/1">Liste des revendeurs</SidebarSubItem>
                )}
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_CONFIG_NOTIFICATION') && (
              <SidebarGroup label="Config notification" icon="fas fa-bell">
                <SidebarSubItem to="/NotificationConfig/getListConfigNotif">Config notification</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('WRITE_ROLE') && (
              <SidebarGroup label="Rôles et privilèges" icon="fas fa-chart-pie">
                <SidebarSubItem to="/role/allroles/1">Rôles</SidebarSubItem>
                <SidebarSubItem to="/role/allprivileges/1">Privilèges</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_CATEGORY', 'READ_PRODUCT', 'ANTIVIRUS_KEY_MANAGEMENT') && (
              <SidebarGroup label="Services et Produits" icon="fas fa-wifi">
                {hasAnyAuthority('READ_CATEGORY') && (
                  <SidebarSubItem to="/pi/allcategoriesproduitsinternet/1">Catégories produits internet</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_PRODUCT') && (
                  <SidebarSubItem to="/pi/allproduits/1">Produits internet</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_PRODUCT') && (
                  <SidebarSubItem to="/pack/allPack">Packs</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_PRODUCT') && (
                  <SidebarSubItem to="/offre/allOffres">Offres</SidebarSubItem>
                )}
                {hasAnyAuthority('ANTIVIRUS_KEY_MANAGEMENT') && (
                  <SidebarSubItem to="/antiVirus/antivirus_keys_page">Liste des clés</SidebarSubItem>
                )}
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_RETAIL_SUMMARY_LIST_ALL', 'READ_RETAIL_SUMMARY_LIST_AREA') && (
              <SidebarGroup label="Gestion des revendeurs" icon="fa fa-user-secret">
                <SidebarSubItem to="/payement/viewlisterecaperevendeur">Récapitulatif des revendeurs</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_REQUEST_MIGRATION', 'READ_REQUEST_CHDEBIT', 'READ_REQUEST_TRANSFERT') && (
              <SidebarGroup label="Opérations Client" icon="fa fa-folder-open">
                {hasAnyAuthority('READ_REQUEST_MIGRATION') && (
                  <SidebarSubItem to="/operationAbonnement/alldemandesmigration">Demande de Migration</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_REQUEST_TRANSFERT') && (
                  <SidebarSubItem to="/operationAbonnement/alldemandestransfert">Demande de Transfert</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_REQUEST_CHDEBIT') && (
                  <SidebarSubItem to="/operationAbonnement/alldemandeschangementdebit">Demande de Changement de Débit</SidebarSubItem>
                )}
              </SidebarGroup>
            )}

            {hasAnyAuthority(
              'ADD_SUBSCRIPTION_REQUEST', 'SEARCH_SUBSCRIPTION_REQUEST_RETAIL', 'SEARCH_SUBSCRIPTION_REQUEST_ALL',
              'READ_SUBSCRIPTION_REQUEST_ALL', 'READ_SUBSCRIPTION_REQUEST_RETAIL', 'READ_SUBSCRIPTION_REQUEST_AREA'
            ) && (
              <SidebarGroup label="Gestion des abonnements" icon="fas fa-globe">
                {hasAnyAuthority('SEARCH_SUBSCRIPTION_REQUEST_ALL', 'SEARCH_SUBSCRIPTION_REQUEST_RETAIL') && (
                  <SidebarSubItem to="/demandeabonnement/recherchefichedemande">Recherche d&apos;abonnement</SidebarSubItem>
                )}
                {hasAnyAuthority('ADD_SUBSCRIPTION_REQUEST', 'ADD_SUBSCRIPTION_REQUEST_ALL') && (
                  <SidebarSubItem to="/demandeabonnement/adddemandeabonnementwithsteps">Nouvel abonnement</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_SUBSCRIPTION_REQUEST_ALL', 'READ_SUBSCRIPTION_REQUEST_RETAIL', 'READ_SUBSCRIPTION_REQUEST_AREA') && (
                  <SidebarSubItem to="/demandeabonnement/alldemandesabonnement/1/20">Liste des abonnements</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_SUBSCRIPTION_REQUEST_ALL', 'READ_SUBSCRIPTION_REQUEST_RETAIL', 'READ_SUBSCRIPTION_REQUEST_AREA') && (
                  <SidebarSubItem to="/demandeabonnement/alldemandesnonsigneer">Liste des abonnements non signés</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_SUBSCRIPTION_REQUEST_ALL', 'READ_SUBSCRIPTION_REQUEST_RETAIL', 'READ_SUBSCRIPTION_REQUEST_AREA') && (
                  <SidebarSubItem to="/demandeabonnement/alldemandesencours">Liste des abonnements en cours</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_SUBSCRIPTION_REQUEST_ALL', 'READ_SUBSCRIPTION_REQUEST_RETAIL', 'READ_SUBSCRIPTION_REQUEST_AREA') && (
                  <SidebarSubItem to="/demandeabonnement/alldemandestraiter">Liste des abonnements traités</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_SUBSCRIPTION_REQUEST_RETAIL') && (
                  <SidebarSubItem to="/demandeabonnement/alldemandestransferees">Suivi des demandes transférées</SidebarSubItem>
                )}
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_COMMANDE') && (
              <SidebarGroup label="Commandes" icon="fa fa-truck">
                <SidebarSubItem to="/commande/allcommandes/1/20">Liste des commandes</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_PARINAGE') && (
              <SidebarGroup label="Parrainage" icon="fa fa-retweet">
                <SidebarSubItem to="/parinage/allparinage">Liste des Parrainages</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('CREATE_INVOICE_AVOIR', 'READ_BLUR_INVOICE_AVOIR') && (
              <SidebarGroup label="Gestion des avoirs" icon="fas fa-file-invoice-dollar">
                {hasAnyAuthority('READ_INVOICE_AVOIR') && (
                  <SidebarSubItem to="/AvoirClient/AllAvoirClient">Liste des factures d&apos;avoir</SidebarSubItem>
                )}
                {hasAnyAuthority('READ_INVOICE_AVOIR', 'READ_BLUR_INVOICE_AVOIR') && (
                  <SidebarSubItem to="/AvoirClient/AllAvoirClientNotPublic">Liste des avoirs en cours</SidebarSubItem>
                )}
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_BORDEREAU', 'READ_BORDEREAU_RETAIL') && (
              <SidebarGroup label="Bordereaux" icon="fas fa-file-alt">
                <SidebarSubItem to="/bordereaux/Listebordereaux">Liste des bordereaux</SidebarSubItem>
                <SidebarSubItem to="/bordereaux/HistoriqueListeBordereau">Historique des bordereaux</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_RECLAMATION', 'ADD_RECLAMATION') && (
              <SidebarGroup label="Réclamations" icon="fas fa-exclamation-triangle">
                {hasAnyAuthority('READ_RECLAMATION') && (
                  <SidebarSubItem to="/reclamation/allreclamations">Liste des réclamations</SidebarSubItem>
                )}
                {hasAnyAuthority('ADD_RECLAMATION') && (
                  <SidebarSubItem to="/reclamation/addreclamation">Nouvelle réclamation</SidebarSubItem>
                )}
                <SidebarSubItem to="/reclamation/mes_reclamations">Mes réclamations</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('VIEW_HISTORIQUE') && (
              <SidebarGroup label="Historique" icon="fas fa-history">
                <SidebarSubItem to="/historique/historique">Historique global</SidebarSubItem>
              </SidebarGroup>
            )}

            {hasAnyAuthority('READ_COMMISSION') && (
              <SidebarGroup label="Commissions" icon="fas fa-money-bill-wave">
                <SidebarSubItem to="/commission/listeCommision">Liste commissions</SidebarSubItem>
                <SidebarSubItem to="/commission/offreCommissions">Offres commissions</SidebarSubItem>
                <SidebarSubItem to="/commission/allDemandeCommission">Demandes commission</SidebarSubItem>
                <SidebarSubItem to="/commission/suivi">Suivi commissions</SidebarSubItem>
              </SidebarGroup>
            )}

          </ul>
        </nav>
      </div>
    </aside>
  )
}

export default Sidebar
