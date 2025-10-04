import { Outlet } from "react-router"
import Header from "./Header"
import Sidebar from "./Sidebar"

function Layout() {
  return (
    <>
      <Header />
      <Sidebar />
      <main>
        <Outlet />
      </main>
    </>
  )
}

export default Layout