import { Outlet } from "react-router"
import Sidebar from "./Sidebar"
import Header from "./Header"

function Layout() {
  return (
    <>
      <Sidebar />
      <Header />
      <div className="pc-container">
        <div className="pc-content">
          <Outlet />
        </div>
      </div>
    </>
  )
}

export default Layout