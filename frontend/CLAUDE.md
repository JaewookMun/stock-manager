# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Development server (port 5173, proxies /api to localhost:8080)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint
npm run lint
```

## Architecture

This is a React 19 + Vite frontend for a stock management application. It uses react-router v7 for routing and SWR for data fetching/state management.

### Key Structure

- **Entry**: `src/main.jsx` → `src/App.jsx` → `src/routes/DefaultRouter.jsx`
- **Layout**: `src/layouts/Layout.jsx` wraps all pages with Sidebar and Header
- **Routing**: `src/routes/DefaultRouter.jsx` - all routes use Layout as parent component
- **Menu Configuration**: `src/menu-items/` - sidebar navigation items, aggregated in `index.jsx`
- **UI State**: `src/api/menu.js` - manages sidebar drawer state via SWR's mutate

### Pages

Routes follow `/[category]/[page]` pattern:
- `/` - Home
- `/analysis/company-filtering` - 조건 검색 (Company filtering)
- `/assets/realized-pnl` - Realized P&L
- `/assets/cash-flow` - Cash flow

### Styling

- SCSS with Bootstrap 5 and react-bootstrap
- Icon fonts: Phosphor and Tabler Icons
- Entry: `src/index.scss` imports all styles
- Theme variables in `src/assets/scss/settings/`
- Based on Datta Able admin template

### Backend Integration

API calls proxy through Vite dev server (`/api` → `http://localhost:8080`).
