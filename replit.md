# Nety CRM — React Frontend Migration

## Project Overview

A full modernization of the Nety (formerly Chifco) Tunisian ISP CRM system — migrating from a Spring Boot + Thymeleaf monolith to a **React SPA frontend** backed by the same **Spring Boot REST API**.

### Tech Stack

**Frontend (this repo root)**
- React 18 + Vite 5
- React Router v6
- Redux Toolkit 2 (state management)
- Axios (API client with JWT interceptors)
- AdminLTE 3 (UI framework — CSS served from `/static/css/`)
- Brand color: `#590E9C` (purple)

**Backend (`/backend/`)**
- Spring Boot 2.6 (Java 8)
- Spring Security (form login for legacy + JWT for REST API)
- JPA/Hibernate with SQL Server (CRM DB) + MariaDB (RADIUS DB)
- JasperReports (PDF generation)

---

## Architecture

```
Browser (port 5000)
    └── Vite Dev Server
            ├── /api/auth/*  → Spring Boot :8282/api/auth/*  (NO rewrite — JWT auth)
            └── /api/*       → Spring Boot :8282/*           (prefix stripped)
```

### Authentication Flow

1. React login form POSTs JSON to `/api/auth/login`
2. Backend authenticates via Spring Security, returns JWT + refresh token
3. JWT stored in `localStorage`, sent as `Authorization: Bearer <token>` on all requests
4. Token auto-refreshed via `/api/auth/refresh` on 401 responses
5. `/api/auth/me` — fetch current user profile

### New Backend Files (REST auth layer)

| File | Purpose |
|------|---------|
| `security/WebAuthController.java` | REST endpoints: `/api/auth/login`, `/api/auth/refresh`, `/api/auth/me`, `/api/auth/logout` |
| `security/WebJwtService.java` | JWT generation & validation for web (24h expiry, 7d refresh) |
| `security/WebJwtAuthFilter.java` | Filter: sets SecurityContext from Bearer token for all `/api/**` requests |
| `security/SecurityConfig.java` | Updated: CORS bean, WebJwtAuthFilter, `/api/auth/**` permitted |

---

## Frontend Structure

```
src/
├── api/          axiosInstance.js (JWT interceptors, auto-refresh)
├── components/
│   ├── common/   DataTable, Modal, Alert, FormField, StatCard, PageHeader, Loader
│   └── layout/   Navbar, Sidebar, Footer
├── hooks/        usePermissions, useAuth, usePagination
├── layouts/      MainLayout.jsx
├── pages/        60+ page components (auth, dashboard, users, clients, abonnements,
│                 commandes, commissions, bordereaux, factures, avoir, reclamations,
│                 modems, payements, roles, produits, referentiels, engagements,
│                 operations, historique, parinage, notifications, errors)
├── redux/
│   ├── store.js
│   └── slices/   authSlice, uiSlice, dashboardSlice, usersSlice, clientsSlice,
│                 abonnementsSlice, notificationsSlice
├── routes/       AppRouter.jsx (60+ routes), PrivateRoute.jsx
├── services/     authService, usersService, clientsService, abonnementsService,
│                 dashboardService, notificationsService, referentielsService
├── styles/       global.css (AdminLTE overrides + brand colors)
└── utils/        helpers.js (formatDate, formatCurrency…), constants.js
```

---

## Running the Project

**Frontend only (this repo):**
```bash
npm run dev    # starts on port 5000
```

**Backend:**  
Run the Spring Boot app from `/backend/` on port 8282. The Vite dev server proxies API requests to it automatically.

---

## Key Configuration

| Setting | Value |
|---------|-------|
| Vite port | 5000 |
| Spring Boot port | 8282 |
| Brand color | `#590E9C` |
| JWT expiry | 24 hours |
| Refresh token expiry | 7 days |
| JWT secret property | `web.jwt.secret` in `application.properties` |

---

## User Preferences

- AdminLTE 3 design must be preserved exactly (no Tailwind, no Material UI)
- Purple brand color `#590E9C` everywhere
- French language UI throughout
- All business logic from the original Spring Boot app must be preserved
- Production-ready, enterprise-grade code quality
