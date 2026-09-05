import { useState } from "react";

import {
  NavLink,
  Outlet,
  useNavigate,
} from "react-router-dom";

function MainLayout() {
  const navigate = useNavigate();

  const [sidebarOpen, setSidebarOpen] =
    useState(true);

  const handleLogout = () => {
    localStorage.removeItem("aurea_token");

    navigate("/login");
  };

  const toggleSidebar = () => {
    setSidebarOpen((current) => !current);
  };

  return (
    <div
      className={
        sidebarOpen
          ? "app-shell"
          : "app-shell sidebar-hidden"
      }
    >
      <aside className="sidebar">
        <div className="sidebar-brand">
          <h1>AUREA</h1>

          <p>
            Gestión de Pedidos
          </p>
        </div>

        <nav className="sidebar-nav">
          <NavLink
            to="/dashboard"
            className={({ isActive }) =>
              isActive
                ? "nav-link active"
                : "nav-link"
            }
          >
            Panel principal
          </NavLink>

          <NavLink
            to="/orders"
            className={({ isActive }) =>
              isActive
                ? "nav-link active"
                : "nav-link"
            }
          >
            Pedidos
          </NavLink>
        </nav>

        <button
          type="button"
          className="logout-button"
          onClick={handleLogout}
        >
          Cerrar sesión
        </button>
      </aside>

      <div className="app-content">
        <header className="topbar">
          <button
            type="button"
            className="sidebar-toggle-button"
            onClick={toggleSidebar}
            aria-label={
              sidebarOpen
                ? "Ocultar menú"
                : "Mostrar menú"
            }
            title={
              sidebarOpen
                ? "Ocultar menú"
                : "Mostrar menú"
            }
          >
            {sidebarOpen ? "☰" : "☰"}
          </button>

          <div>
            <h2>Sistema AUREA</h2>

            <p>
              Administración y seguimiento
              de pedidos
            </p>
          </div>
        </header>

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default MainLayout;