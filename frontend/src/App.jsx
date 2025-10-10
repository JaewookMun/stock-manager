import { RouterProvider } from 'react-router'
import router from './routes/DefaultRouter'
// import Layout from './layouts/Layout'
// import Home from './pages/Home'
// import About from './pages/About'
// import './App.css'

function App() {
  return (
    <>
      <RouterProvider router={router} />
    </>
  )
}

export default App
