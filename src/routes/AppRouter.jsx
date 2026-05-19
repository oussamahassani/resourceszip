import React, { Suspense, lazy } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import PrivateRoute from './PrivateRoute'
import MainLayout from '../layouts/MainLayout'
import Loader from '../components/common/Loader'

const LoginPage = lazy(() => import('../pages/auth/LoginPage'))
const AdminDashboard = lazy(() => import('../pages/dashboard/AdminDashboard'))
const OtherDashboard = lazy(() => import('../pages/dashboard/OtherDashboard'))

const AllUsers = lazy(() => import('../pages/users/AllUsers'))
const AddUser = lazy(() => import('../pages/users/AddUser'))
const EditUser = lazy(() => import('../pages/users/EditUser'))
const UsersSystem = lazy(() => import('../pages/users/UsersSystem'))
const AllRevendeurs = lazy(() => import('../pages/users/AllRevendeurs'))
const DetailRevendeur = lazy(() => import('../pages/users/DetailRevendeur'))
const EditRevendeur = lazy(() => import('../pages/users/EditRevendeur'))
const HistoriqueUser = lazy(() => import('../pages/users/HistoriqueUser'))

const AllClients = lazy(() => import('../pages/clients/AllClients'))
const AllClientsActive = lazy(() => import('../pages/clients/AllClientsActive'))
const AllClientsResilier = lazy(() => import('../pages/clients/AllClientsResilier'))
const AllClientsEnRecouvrement = lazy(() => import('../pages/clients/AllClientsEnRecouvrement'))
const AllClientsNonConnecter = lazy(() => import('../pages/clients/AllClientsNonConnecter'))
const AddClient = lazy(() => import('../pages/clients/AddClient'))
const EditClient = lazy(() => import('../pages/clients/EditClient'))
const ViewClient = lazy(() => import('../pages/clients/ViewClient'))

const AllAbonnements = lazy(() => import('../pages/abonnements/AllAbonnements'))
const AllAbonnementsNonSignees = lazy(() => import('../pages/abonnements/AllAbonnementsNonSignees'))
const AllAbonnementsEnCours = lazy(() => import('../pages/abonnements/AllAbonnementsEnCours'))
const AllAbonnementsTraites = lazy(() => import('../pages/abonnements/AllAbonnementsTraites'))
const AddAbonnement = lazy(() => import('../pages/abonnements/AddAbonnement'))
const ViewAbonnement = lazy(() => import('../pages/abonnements/ViewAbonnement'))
const EditAbonnement = lazy(() => import('../pages/abonnements/EditAbonnement'))
const RechercheAbonnement = lazy(() => import('../pages/abonnements/RechercheAbonnement'))
const SuiviDemandesTransferees = lazy(() => import('../pages/abonnements/SuiviDemandesTransferees'))
const VerificationCin = lazy(() => import('../pages/abonnements/VerificationCin'))

const AllCommandes = lazy(() => import('../pages/commandes/AllCommandes'))

const AllCommissions = lazy(() => import('../pages/commissions/AllCommissions'))
const MesCommissions = lazy(() => import('../pages/commissions/MesCommissions'))
const ListeCommissions = lazy(() => import('../pages/commissions/ListeCommissions'))
const OffreCommissions = lazy(() => import('../pages/commissions/OffreCommissions'))
const AllDemandeCommission = lazy(() => import('../pages/commissions/AllDemandeCommission'))
const DetailsCommission = lazy(() => import('../pages/commissions/DetailsCommission'))
const SuiviCommissions = lazy(() => import('../pages/commissions/SuiviCommissions'))

const AllBordereaux = lazy(() => import('../pages/bordereaux/AllBordereaux'))
const DetailsBordereau = lazy(() => import('../pages/bordereaux/DetailsBordereau'))
const EditBordereau = lazy(() => import('../pages/bordereaux/EditBordereau'))
const HistoriqueListeBordereau = lazy(() => import('../pages/bordereaux/HistoriqueListeBordereau'))

const AllFactures = lazy(() => import('../pages/factures/AllFactures'))
const ViewFacture = lazy(() => import('../pages/factures/ViewFacture'))
const OpenFacture = lazy(() => import('../pages/factures/OpenFacture'))

const AllAvoirClient = lazy(() => import('../pages/avoir/AllAvoirClient'))
const AddAvoir = lazy(() => import('../pages/avoir/AddAvoir'))
const ListAvoirNotPublish = lazy(() => import('../pages/avoir/ListAvoirNotPublish'))

const AllReclamations = lazy(() => import('../pages/reclamations/AllReclamations'))
const AddReclamation = lazy(() => import('../pages/reclamations/AddReclamation'))
const ViewReclamation = lazy(() => import('../pages/reclamations/ViewReclamation'))
const EditReclamation = lazy(() => import('../pages/reclamations/EditReclamation'))
const MesReclamations = lazy(() => import('../pages/reclamations/MesReclamations'))

const AllModems = lazy(() => import('../pages/modems/AllModems'))
const EnregistrerModem = lazy(() => import('../pages/modems/EnregistrerModem'))
const AdminModem = lazy(() => import('../pages/modems/AdminModem'))
const StockDetails = lazy(() => import('../pages/modems/StockDetails'))
const DemandModem = lazy(() => import('../pages/modems/DemandModem'))

const AllPayements = lazy(() => import('../pages/payements/AllPayements'))
const RecapRevendeur = lazy(() => import('../pages/payements/RecapRevendeur'))
const ViewListeRecapRevendeur = lazy(() => import('../pages/payements/ViewListeRecapRevendeur'))

const AllRoles = lazy(() => import('../pages/roles/AllRoles'))
const AllPrivileges = lazy(() => import('../pages/roles/AllPrivileges'))
const AddRole = lazy(() => import('../pages/roles/AddRole'))
const EditRole = lazy(() => import('../pages/roles/EditRole'))
const AddPrivilege = lazy(() => import('../pages/roles/AddPrivilege'))

const AllProduits = lazy(() => import('../pages/produits/AllProduits'))
const AllCategories = lazy(() => import('../pages/produits/AllCategories'))
const AllPacks = lazy(() => import('../pages/produits/AllPacks'))
const AllOffres = lazy(() => import('../pages/produits/AllOffres'))
const AntivirusKeys = lazy(() => import('../pages/produits/AntivirusKeys'))

const AllGouvernorats = lazy(() => import('../pages/referentiels/AllGouvernorats'))
const AllVilles = lazy(() => import('../pages/referentiels/AllVilles'))
const AllZones = lazy(() => import('../pages/referentiels/AllZones'))
const AllProfessions = lazy(() => import('../pages/referentiels/AllProfessions'))
const AllMotifs = lazy(() => import('../pages/referentiels/AllMotifs'))
const AllStatuts = lazy(() => import('../pages/referentiels/AllStatuts'))
const AllTypesPaiement = lazy(() => import('../pages/referentiels/AllTypesPaiement'))
const AllTypesVisite = lazy(() => import('../pages/referentiels/AllTypesVisite'))
const AllServiceTypes = lazy(() => import('../pages/referentiels/AllServiceTypes'))

const AllEngagements = lazy(() => import('../pages/engagements/AllEngagements'))

const OperationsMigration = lazy(() => import('../pages/operations/AllMigrations'))
const OperationsTransfert = lazy(() => import('../pages/operations/AllTransferts'))
const OperationsChangementDebit = lazy(() => import('../pages/operations/AllChangementsDebit'))

const ConfigNotification = lazy(() => import('../pages/notifications/ConfigNotification'))

const AllParinage = lazy(() => import('../pages/parinage/AllParinage'))

const HistoriqueGlobal = lazy(() => import('../pages/historique/HistoriqueGlobal'))
const HistoriqueClient = lazy(() => import('../pages/historique/HistoriqueClient'))
const HistoriqueAbonnement = lazy(() => import('../pages/historique/HistoriqueAbonnement'))

const Error403 = lazy(() => import('../pages/errors/Error403'))
const Error404 = lazy(() => import('../pages/errors/Error404'))
const Error500 = lazy(() => import('../pages/errors/Error500'))

const SuspenseWrapper = ({ children }) => (
  <Suspense fallback={<Loader />}>{children}</Suspense>
)

const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<SuspenseWrapper><LoginPage /></SuspenseWrapper>} />
        <Route path="/error/403" element={<SuspenseWrapper><Error403 /></SuspenseWrapper>} />
        <Route path="/error/404" element={<SuspenseWrapper><Error404 /></SuspenseWrapper>} />
        <Route path="/error/500" element={<SuspenseWrapper><Error500 /></SuspenseWrapper>} />

        <Route element={<PrivateRoute />}>
          <Route element={<MainLayout />}>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />

            <Route path="/admin/dashboard" element={<SuspenseWrapper><AdminDashboard /></SuspenseWrapper>} />
            <Route path="/alluser/dashboard" element={<SuspenseWrapper><OtherDashboard /></SuspenseWrapper>} />
            <Route path="/dashboard" element={<SuspenseWrapper><AdminDashboard /></SuspenseWrapper>} />

            <Route path="/admin/users">
              <Route path="allusers/:page?" element={<SuspenseWrapper><AllUsers /></SuspenseWrapper>} />
              <Route path="add-user" element={<SuspenseWrapper><AddUser /></SuspenseWrapper>} />
              <Route path="edit-user/:id" element={<SuspenseWrapper><EditUser /></SuspenseWrapper>} />
              <Route path="historique/:id" element={<SuspenseWrapper><HistoriqueUser /></SuspenseWrapper>} />
            </Route>
            <Route path="/admin/usersSystem/getusers" element={<SuspenseWrapper><UsersSystem /></SuspenseWrapper>} />
            <Route path="/RevendeurUser/revendeurliste/:page?" element={<SuspenseWrapper><AllRevendeurs /></SuspenseWrapper>} />
            <Route path="/RevendeurUser/revendeurdetail/:id" element={<SuspenseWrapper><DetailRevendeur /></SuspenseWrapper>} />
            <Route path="/RevendeurUser/editrevendeur/:id" element={<SuspenseWrapper><EditRevendeur /></SuspenseWrapper>} />

            <Route path="/client">
              <Route path="allclients" element={<SuspenseWrapper><AllClients /></SuspenseWrapper>} />
              <Route path="active" element={<SuspenseWrapper><AllClientsActive /></SuspenseWrapper>} />
              <Route path="resilie" element={<SuspenseWrapper><AllClientsResilier /></SuspenseWrapper>} />
              <Route path="recouvrement" element={<SuspenseWrapper><AllClientsEnRecouvrement /></SuspenseWrapper>} />
              <Route path="non-connecte" element={<SuspenseWrapper><AllClientsNonConnecter /></SuspenseWrapper>} />
              <Route path="addclient" element={<SuspenseWrapper><AddClient /></SuspenseWrapper>} />
              <Route path="editclient/:id" element={<SuspenseWrapper><EditClient /></SuspenseWrapper>} />
              <Route path="viewclient/:id" element={<SuspenseWrapper><ViewClient /></SuspenseWrapper>} />
            </Route>

            <Route path="/demandeabonnement">
              <Route path="alldemandesabonnement/:page?/:size?" element={<SuspenseWrapper><AllAbonnements /></SuspenseWrapper>} />
              <Route path="alldemandesnonsigneer" element={<SuspenseWrapper><AllAbonnementsNonSignees /></SuspenseWrapper>} />
              <Route path="alldemandesencours" element={<SuspenseWrapper><AllAbonnementsEnCours /></SuspenseWrapper>} />
              <Route path="alldemandestraiter" element={<SuspenseWrapper><AllAbonnementsTraites /></SuspenseWrapper>} />
              <Route path="adddemandeabonnementwithsteps" element={<SuspenseWrapper><AddAbonnement /></SuspenseWrapper>} />
              <Route path="getdemandeabonnement/:id" element={<SuspenseWrapper><ViewAbonnement /></SuspenseWrapper>} />
              <Route path="editdemandeabonnement/:id" element={<SuspenseWrapper><EditAbonnement /></SuspenseWrapper>} />
              <Route path="recherchefichedemande" element={<SuspenseWrapper><RechercheAbonnement /></SuspenseWrapper>} />
              <Route path="alldemandestransferees" element={<SuspenseWrapper><SuiviDemandesTransferees /></SuspenseWrapper>} />
              <Route path="verificationcin" element={<SuspenseWrapper><VerificationCin /></SuspenseWrapper>} />
            </Route>

            <Route path="/commande/allcommandes/:page?/:size?" element={<SuspenseWrapper><AllCommandes /></SuspenseWrapper>} />

            <Route path="/commission">
              <Route path="allCommission" element={<SuspenseWrapper><AllCommissions /></SuspenseWrapper>} />
              <Route path="mesCommission" element={<SuspenseWrapper><MesCommissions /></SuspenseWrapper>} />
              <Route path="listeCommision" element={<SuspenseWrapper><ListeCommissions /></SuspenseWrapper>} />
              <Route path="offreCommissions" element={<SuspenseWrapper><OffreCommissions /></SuspenseWrapper>} />
              <Route path="allDemandeCommission" element={<SuspenseWrapper><AllDemandeCommission /></SuspenseWrapper>} />
              <Route path="detailsCommission/:id" element={<SuspenseWrapper><DetailsCommission /></SuspenseWrapper>} />
              <Route path="suivi" element={<SuspenseWrapper><SuiviCommissions /></SuspenseWrapper>} />
            </Route>

            <Route path="/bordereaux">
              <Route path="Listebordereaux" element={<SuspenseWrapper><AllBordereaux /></SuspenseWrapper>} />
              <Route path="detailsbordereau/:id" element={<SuspenseWrapper><DetailsBordereau /></SuspenseWrapper>} />
              <Route path="editbordereaux/:id" element={<SuspenseWrapper><EditBordereau /></SuspenseWrapper>} />
              <Route path="HistoriqueListeBordereau" element={<SuspenseWrapper><HistoriqueListeBordereau /></SuspenseWrapper>} />
            </Route>

            <Route path="/facture">
              <Route index element={<SuspenseWrapper><AllFactures /></SuspenseWrapper>} />
              <Route path="view/:id" element={<SuspenseWrapper><ViewFacture /></SuspenseWrapper>} />
              <Route path="open/:id" element={<SuspenseWrapper><OpenFacture /></SuspenseWrapper>} />
            </Route>

            <Route path="/AvoirClient">
              <Route path="AllAvoirClient" element={<SuspenseWrapper><AllAvoirClient /></SuspenseWrapper>} />
              <Route path="addNewAvoir" element={<SuspenseWrapper><AddAvoir /></SuspenseWrapper>} />
              <Route path="AllAvoirClientNotPublic" element={<SuspenseWrapper><ListAvoirNotPublish /></SuspenseWrapper>} />
            </Route>

            <Route path="/reclamation">
              <Route path="allreclamations" element={<SuspenseWrapper><AllReclamations /></SuspenseWrapper>} />
              <Route path="addreclamation" element={<SuspenseWrapper><AddReclamation /></SuspenseWrapper>} />
              <Route path="view/:id" element={<SuspenseWrapper><ViewReclamation /></SuspenseWrapper>} />
              <Route path="edit/:id" element={<SuspenseWrapper><EditReclamation /></SuspenseWrapper>} />
              <Route path="mes_reclamations" element={<SuspenseWrapper><MesReclamations /></SuspenseWrapper>} />
            </Route>

            <Route path="/modem">
              <Route path="modems" element={<SuspenseWrapper><AllModems /></SuspenseWrapper>} />
              <Route path="enregistrermodem" element={<SuspenseWrapper><EnregistrerModem /></SuspenseWrapper>} />
              <Route path="adminmodem" element={<SuspenseWrapper><AdminModem /></SuspenseWrapper>} />
              <Route path="stockDetails" element={<SuspenseWrapper><StockDetails /></SuspenseWrapper>} />
              <Route path="demandemodem" element={<SuspenseWrapper><DemandModem /></SuspenseWrapper>} />
            </Route>

            <Route path="/payement">
              <Route path="Listepayement" element={<SuspenseWrapper><AllPayements /></SuspenseWrapper>} />
              <Route path="recaprevendeur" element={<SuspenseWrapper><RecapRevendeur /></SuspenseWrapper>} />
              <Route path="viewlisterecaperevendeur" element={<SuspenseWrapper><ViewListeRecapRevendeur /></SuspenseWrapper>} />
            </Route>

            <Route path="/role">
              <Route path="allroles/:page?" element={<SuspenseWrapper><AllRoles /></SuspenseWrapper>} />
              <Route path="allprivileges/:page?" element={<SuspenseWrapper><AllPrivileges /></SuspenseWrapper>} />
              <Route path="addrole" element={<SuspenseWrapper><AddRole /></SuspenseWrapper>} />
              <Route path="editrole/:id" element={<SuspenseWrapper><EditRole /></SuspenseWrapper>} />
              <Route path="addprivilege" element={<SuspenseWrapper><AddPrivilege /></SuspenseWrapper>} />
            </Route>

            <Route path="/pi">
              <Route path="allproduits/:page?" element={<SuspenseWrapper><AllProduits /></SuspenseWrapper>} />
              <Route path="allcategoriesproduitsinternet/:page?" element={<SuspenseWrapper><AllCategories /></SuspenseWrapper>} />
            </Route>
            <Route path="/pack/allPack" element={<SuspenseWrapper><AllPacks /></SuspenseWrapper>} />
            <Route path="/offre/allOffres" element={<SuspenseWrapper><AllOffres /></SuspenseWrapper>} />
            <Route path="/antiVirus/antivirus_keys_page" element={<SuspenseWrapper><AntivirusKeys /></SuspenseWrapper>} />

            <Route path="/ville">
              <Route path="allgouvernorat" element={<SuspenseWrapper><AllGouvernorats /></SuspenseWrapper>} />
              <Route path="allvillees" element={<SuspenseWrapper><AllVilles /></SuspenseWrapper>} />
            </Route>
            <Route path="/zone/allzones" element={<SuspenseWrapper><AllZones /></SuspenseWrapper>} />
            <Route path="/profession/allprofessions" element={<SuspenseWrapper><AllProfessions /></SuspenseWrapper>} />
            <Route path="/motif/allmotifs" element={<SuspenseWrapper><AllMotifs /></SuspenseWrapper>} />
            <Route path="/statut/allstatus" element={<SuspenseWrapper><AllStatuts /></SuspenseWrapper>} />
            <Route path="/typepaiement/alltypepaiements" element={<SuspenseWrapper><AllTypesPaiement /></SuspenseWrapper>} />
            <Route path="/typevisite/alltypevisites" element={<SuspenseWrapper><AllTypesVisite /></SuspenseWrapper>} />
            <Route path="/servicetype/allservicetypes" element={<SuspenseWrapper><AllServiceTypes /></SuspenseWrapper>} />

            <Route path="/engagement">
              <Route path="allengagements" element={<SuspenseWrapper><AllEngagements /></SuspenseWrapper>} />
            </Route>

            <Route path="/operationAbonnement">
              <Route path="alldemandesmigration" element={<SuspenseWrapper><OperationsMigration /></SuspenseWrapper>} />
              <Route path="alldemandestransfert" element={<SuspenseWrapper><OperationsTransfert /></SuspenseWrapper>} />
              <Route path="alldemandeschangementdebit" element={<SuspenseWrapper><OperationsChangementDebit /></SuspenseWrapper>} />
            </Route>

            <Route path="/NotificationConfig/getListConfigNotif" element={<SuspenseWrapper><ConfigNotification /></SuspenseWrapper>} />
            <Route path="/parinage/allparinage" element={<SuspenseWrapper><AllParinage /></SuspenseWrapper>} />

            <Route path="/historique">
              <Route path="historique" element={<SuspenseWrapper><HistoriqueGlobal /></SuspenseWrapper>} />
              <Route path="historiqueClient/:id" element={<SuspenseWrapper><HistoriqueClient /></SuspenseWrapper>} />
              <Route path="historiqueAbonnement/:id" element={<SuspenseWrapper><HistoriqueAbonnement /></SuspenseWrapper>} />
            </Route>
          </Route>
        </Route>

        <Route path="*" element={<SuspenseWrapper><Error404 /></SuspenseWrapper>} />
      </Routes>
    </BrowserRouter>
  )
}

export default AppRouter
