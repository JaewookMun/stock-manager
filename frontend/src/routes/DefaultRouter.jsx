import { createBrowserRouter } from "react-router";
import Layout from "../layouts/Layout";
import Home from "../pages/Home"
import CompanyFiltering from "../pages/analysis/CompanyFiltering"
import CashFlow from "..//pages/assets/CashFlow"
import RealizedPnl from "../pages/assets/RealizedPnl";

const DefaultRouter = createBrowserRouter(
  [
    {
      path: "/",
      Component: Layout,
      children: [
        {
          index: true,
          Component: Home
        }
      ]
    },
    {
      path: "/",
      Component: Layout,
      children: [
        {
          path: "analysis",
          children: [
            {
              path: "company-filtering",
              Component: CompanyFiltering
            }
          ]
        }
      ]
    },
    {
      path: "/",
      Component: Layout,
      children: [
        {
          path: "assets",
          children: [
            {
              path: "realized-pnl",
              Component: RealizedPnl
            },
            {
              path: "cash-flow",
              Component: CashFlow
            }
          ]
        }
      ]
    }
  ]
)

export default DefaultRouter;