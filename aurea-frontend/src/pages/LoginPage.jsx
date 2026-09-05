import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login } from "../services/authService";

function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setMessage("");
    setLoading(true);

    try {
      const data = await login(username, password);

      localStorage.setItem("aurea_token", data.token);

      navigate("/dashboard");
    } catch (error) {
      console.error(error);

      if (error.response?.status === 401) {
        setMessage("Usuario o contraseña incorrectos.");
      } else {
        setMessage("No se pudo conectar con el servidor.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-panel">
        <div className="login-brand">
          <div className="login-logo">
            A
          </div>

          <div>
            <h1>AUREA</h1>

            <p>
              Sistema de Gestión de Pedidos
            </p>
          </div>
        </div>

        <div className="login-content">
          <h2>Bienvenido</h2>

          <p className="login-subtitle">
            Ingresa tus credenciales para acceder
            al sistema.
          </p>

          <form
            className="login-form"
            onSubmit={handleSubmit}
          >
            <div className="form-group">
              <label htmlFor="username">
                Usuario
              </label>

              <input
                id="username"
                type="text"
                value={username}
                onChange={(event) =>
                  setUsername(event.target.value)
                }
                placeholder="Ingresa tu usuario"
                autoComplete="username"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">
                Contraseña
              </label>

              <input
                id="password"
                type="password"
                value={password}
                onChange={(event) =>
                  setPassword(event.target.value)
                }
                placeholder="Ingresa tu contraseña"
                autoComplete="current-password"
                required
              />
            </div>

            {message && (
              <div className="login-error">
                {message}
              </div>
            )}

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading
                ? "Ingresando..."
                : "Ingresar"}
            </button>
          </form>
        </div>

        <div className="login-footer">
          <span>
            AUREA
          </span>

          <span>
            EMD Tech Consulting
          </span>
        </div>
      </div>

      <div className="login-side">
        <div className="login-side-content">
          <span className="login-side-label">
            Gestión inteligente
          </span>

          <h2>
            Controla tus pedidos desde
            un solo lugar
          </h2>

          <p>
            Administra pedidos, estados,
            productos, entregas y operaciones
            de manera centralizada.
          </p>

          <div className="login-features">
            <div>
              <strong>
                Pedidos
              </strong>

              <span>
                Seguimiento en tiempo real
              </span>
            </div>

            <div>
              <strong>
                Seguridad
              </strong>

              <span>
                Autenticación mediante JWT
              </span>
            </div>

            <div>
              <strong>
                Integración
              </strong>

              <span>
                React + Spring Boot + MariaDB
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;