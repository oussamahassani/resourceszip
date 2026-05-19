import React, { useEffect } from 'react'
import { Outlet } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import Navbar from '../components/layout/Navbar'
import Sidebar from '../components/layout/Sidebar'
import Footer from '../components/layout/Footer'

const MainLayout = () => {
  const { sidebarCollapsed } = useSelector((state) => state.ui)

  useEffect(() => {
    document.body.className = 'hold-transition sidebar-mini'
    if (sidebarCollapsed) {
      document.body.classList.add('sidebar-collapse')
    } else {
      document.body.classList.remove('sidebar-collapse')
    }
  }, [sidebarCollapsed])

  return (
    <div className="wrapper">
      <Navbar />
      <Sidebar />
      <div className="content-wrapper">
        <Outlet />
      </div>
      <Footer />
    </div>
  )
}

export default MainLayout
