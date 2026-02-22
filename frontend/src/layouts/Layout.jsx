import { Outlet } from "react-router"
import Sidebar from "./Sidebar"
import Header from "./Header"
import Breadcrumbs from "../components/Breadcrumbs"

function Layout() {
  return (
    <>
      <Sidebar />
      <Header />
      <div className="pc-container">
        <div className="pc-content">
          <Breadcrumbs />
          <Outlet />
        </div>
      </div>
    </>
  )
}

export default Layout