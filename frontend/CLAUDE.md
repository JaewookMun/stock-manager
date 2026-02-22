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

No test framework is configured.

## Architecture

React 19 + Vite frontend for a stock management application. Uses react-router v7 for routing and SWR for data fetching/state management.

### Entry Flow

`src/main.jsx` → `src/App.jsx` → `src/routes/DefaultRouter.jsx` → `src/layouts/Layout.jsx` (wraps all pages via `<Outlet />`)

### Routing

`src/routes/DefaultRouter.jsx` uses multiple root entries all with `path: "/"` and `Component: Layout`. New routes should follow the existing pattern: add a new root entry with Layout and nest the page under its category path.

Routes follow `/[category]/[page]`:
- `/` - Home
- `/analysis/company-filtering` - 조건 검색
- `/assets/realized-pnl` - Realized P&L
- `/assets/cash-flow` - Cash flow

### Sidebar Navigation

Menu items live in `src/menu-items/` and are aggregated in `index.jsx`. Only `navigation.jsx`, `analysis.jsx`, and `assets.jsx` are active — the other files (`charts-maps.jsx`, `forms.jsx`, `pages.jsx`, `tables.jsx`, `ui-components.jsx`, `other.jsx`) are unused placeholders from the Datta Able template and should not be used.

Each active menu file exports an object with this structure:

```js
{
  id: "group-id",
  title: "Group Title",
  type: "group",        // top-level group (renders as sidebar section label)
  children: [
    {
      id: "item-id",
      title: "Item Title",
      type: "item",     // leaf nav item
      icon: <i className="ph ph-icon-name" />,  // Phosphor icon
      url: "/category/page"
    }
    // type: "collapse" is also supported for nested groups
  ]
}
```

The Navigation component (`src/layouts/Sidebar/Navigation/`) renders groups → items/collapses, with active route detection via `matchPath` and auto-open of parent groups. To add a new page: create a menu item file, add it to `src/menu-items/index.jsx`, and add the route to `DefaultRouter.jsx`.

### UI State (SWR as local state)

`src/api/menu.js` manages sidebar open/close state using SWR with a static initial value (no network call). SWR's `mutate` is used as a client-side state manager. This pattern is used because SWR is already the data-fetching solution — follow this pattern for other UI state if needed.

### API Data Fetching

Put SWR hooks in `src/api/`. Backend calls go to `/api/*` which the Vite dev server proxies to `http://localhost:8080`.

### Styling

- SCSS with Bootstrap 5 and react-bootstrap
- Icon fonts: Phosphor (`ph ph-*`) and Tabler Icons
- SCSS entry chain: `src/index.scss` → `src/assets/scss/style.scss` → all partials
- Theme variables in `src/assets/scss/settings/` (`_bootstrap-variables.scss`, `_color-variables.scss`, `_theme-variables.scss`)
- Based on Datta Able admin template — CSS class names like `pc-sidebar`, `pc-item`, `pc-container`, `pc-link`, `pc-micon`, `pc-trigger` come from this template

### Components

- All components use PropTypes for prop validation — follow this pattern when adding new components.
- `src/components/third-party/SimpleBar.jsx` wraps `simplebar-react` with `react-device-detect` to render a custom scrollbar on desktop and a native scroll on mobile.
- Page components in `src/pages/` are currently stubs — they only render a placeholder `<div>`.
