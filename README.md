# Sufi Account Security Manager

A professional, high-security Android application built with **Kotlin** and **Jetpack Compose** (with companion Node.js OAuth token service and Capacitor configuration) designed to manage authorized Instagram and Facebook client account security reviews, compliance checks, and client-reported suspension/appeal intakes.

---

## Visual Reference & Design Implementation

This application faithfully implements the requested reference design:
* **Pristine White Background** (`#FFFFFF` / `#F8FAFC`) with high-contrast Material 3 surfaces.
* **Meta-Inspired Blue-to-Cyan Gradient Progress Bar** (`#0866FF` → `#00B2FF`) with traveling light shimmer.
* **Circular Animated Loading Indicator** with rotating gradient sweep arc and centered client profile avatar.
* **Live Verification Progress Percentage** (`0%` to `100%`).
* **Four Verification Checklist Items**:
  1. *Verifying authorized account connection* (Meta OAuth 2.0 token validation).
  2. *Checking available account information* (profile, business metadata, media counts).
  3. *Reviewing recorded security concerns* (2FA status, third-party apps audit).
  4. *Finalizing security report* (compiling diagnostic score & recommendations).
* **Mobile-Responsive & Adaptive Layout**: Fluid spacing on phones and foldables.

---

## Five Core Application Screens

### Screen 1 – Client Connection Portal
* Input Instagram or Facebook username handle (e.g., `@sufi_official`).
* **Secure Connect with Instagram** button launching official Meta OAuth 2.0 flow.
* **Zero-Password Architecture**: Explicit security guarantee that Sufi Security never prompts for, collects, or stores account passwords.
* **Client Consent Dialog**: Transparently outlines requested permissions (`instagram_basic`, `pages_show_list`, `instagram_manage_insights`) versus explicitly forbidden actions (never posts, never alters credentials, never reads direct messages).
* Disconnect account option with local session revocation.
* One-tap sandbox accounts for testing (`@sufi_official`, `@creativestudio_pk`, `@boutique_couture`).

### Screen 2 – Account Dashboard
* Displays authorized account username, full business name, and profile picture.
* Connection status badge: `● Connected & Verified`.
* Last synchronization timestamp with refresh action.
* Authorized Meta API metrics: Meta ID, account tier (Business / Creator), media post count, follower count, linked Facebook Business page.
* Authorized OAuth scopes list and 60-day token expiration tracker.
* Quick action shortcuts: Start Security Audit, Manage Suspensions, Export Report.

### Screen 3 – Account Security Review (Reference Design)
* Animated review engine executing the 4 verification checklist stages.
* Real-time 0% to 100% progress animation.
* **Mandatory API Limitation Disclosure**: Prominently displays `Not available through Meta API` explaining that Meta Graph API does not expose restriction or policy violation endpoints for privacy reasons.
* Calculated Security Score (e.g., `94/100`) and comprehensive recommendations summary.

### Screen 4 – Suspension & Policy Management
* Six official status categories:
  * **Active** (Green)
  * **Disabled** (Deep Red)
  * **Suspended** (Red)
  * **Restricted** (Purple)
  * **Under Review** (Blue)
  * **Appeal Submitted** (Amber)
* Manual record intake form for authorized administrators:
  * Status selector chips.
  * Meta Support Case / Ticket ID.
  * Appeal submission channel (Meta Help Center, Business Chat, Partner Portal).
  * Evidence & documents attached (Gov ID, Business Registration, Domain records).
  * Administrative case notes and timeline.
* Active case summary card.

### Screen 5 – Admin Dashboard
* **Passcode-Protected Security Gate**: Default passcode `1234` with session lock/unlock toggle.
* Global Metric Counters: Total Clients, Active Valid, Pending Reviews, Reported Suspensions, Active Appeals.
* Client search bar with instant query matching (handle, name, ticket #).
* Status filter chips (`All`, `Active`, `Suspended`, `Restricted`, `Under Review`, `Appeals`).
* Client management cards with direct actions: View, Audit, Edit Status, Delete.
* Comprehensive **Activity Audit Log** tracking all security events, status updates, and admin actions.

---

## Security & Privacy Architecture

1. **Zero-Knowledge Password Handling**:
   * Authorization is strictly negotiated via standard OAuth 2.0 redirect (`sufisecurity://oauth-callback`).
2. **Encrypted Tokens at Rest**:
   * All long-lived Meta access tokens are encrypted with `AES-256-GCM` using unique 96-bit initialization vectors (IVs) and authentication tags.
3. **API Integrity & Non-Fabrication**:
   * The app strictly distinguishes real Meta Graph API data from manual client reports.
   * It never claims to remove strikes or bypass Meta policy enforcement.

---

## Android APK Build Instructions

### Prerequisites
* JDK 17 or JDK 21
* Android SDK (API 34 or higher installed)
* Gradle 8.x+ (Pre-installed in this environment)

### Step 1: Clean and Verify Project
```bash
gradle tasks
```

### Step 2: Build Debug APK
To compile and assemble the debug APK:
```bash
gradle :app:assembleDebug
```
The resulting APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Step 3: Build Signed Release APK
To assemble the release APK:
```bash
gradle :app:assembleRelease
```
The resulting release APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

### Step 4: Run Unit & JVM Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

---

## Node.js Backend Execution

```bash
cd backend
npm install
npm start
```
Server starts on `http://localhost:3000` with `/api/auth/meta/url` and AES-256 encrypted callback exchange.
