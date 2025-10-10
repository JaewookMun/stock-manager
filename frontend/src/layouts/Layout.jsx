import { Outlet } from "react-router"
import Sidebar from "./Sidebar"
import Header from "./Header"

function Layout() {
  return (
    <>
      <Sidebar />
      <Header />
      <main>
        <Outlet />
      </main>
    </>
  )
}

export default Layout