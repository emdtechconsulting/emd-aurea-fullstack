import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getOrderMetrics } from "../services/orderService";

function DashboardPage() {
  const navigate = useNavigate();

  const [metrics, setMetrics] = useState({
    total: 0,
    draft: 0,
    confirmed: 0,
    preparing: 0,
    delivered: 0,
    cancelled: 0,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadMetrics = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getOrderMetrics();

        setMetrics(data);
      } catch (err) {
        console.error(err);

        setError(
          "No se pudieron cargar las métricas del sistema."
        );
      } finally {
        setLoading(false);
      }
    };

    loadMetrics();
  }, []);

  const goToOrders = (status = "") => {
    if (status) {
      navigate(`/orders?status=${status}`);
    } else {
      navigate("/orders");
    }
  };

  return (
    <div>
      <div className="page-header page-header-row">
        <div>
          <h1>Panel principal</h1>

          <p>
            Resumen general de la operación
            de pedidos AUREA.
          </p>
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={() => navigate("/orders")}
        >
          Ver pedidos
        </button>
      </div>

      {loading ? (
        <div className="loading-panel">
          Cargando métricas...
        </div>
      ) : error ? (
        <div className="error-panel">
          {error}
        </div>
      ) : (
        <>
          <div className="metrics-grid">
            <button
              type="button"
              className="metric-card metric-total"
              onClick={() => goToOrders()}
            >
              <span className="metric-label">
                Total de pedidos
              </span>

              <strong className="metric-value">
                {metrics.total}
              </strong>

              <span className="metric-footer">
                Ver todos los pedidos
              </span>
            </button>

            <button
              type="button"
              className="metric-card"
              onClick={() =>
                goToOrders("DRAFT")
              }
            >
              <span
                className="
                  status-badge
                  status-draft
                "
              >
                Borrador
              </span>

              <strong className="metric-value">
                {metrics.draft}
              </strong>

              <span className="metric-footer">
                Pedidos pendientes
              </span>
            </button>

            <button
              type="button"
              className="metric-card"
              onClick={() =>
                goToOrders("CONFIRMED")
              }
            >
              <span
                className="
                  status-badge
                  status-confirmed
                "
              >
                Confirmados
              </span>

              <strong className="metric-value">
                {metrics.confirmed}
              </strong>

              <span className="metric-footer">
                Pedidos confirmados
              </span>
            </button>

            <button
              type="button"
              className="metric-card"
              onClick={() =>
                goToOrders("PREPARING")
              }
            >
              <span
                className="
                  status-badge
                  status-preparing
                "
              >
                En preparación
              </span>

              <strong className="metric-value">
                {metrics.preparing}
              </strong>

              <span className="metric-footer">
                En proceso
              </span>
            </button>

            <button
              type="button"
              className="metric-card"
              onClick={() =>
                goToOrders("DELIVERED")
              }
            >
              <span
                className="
                  status-badge
                  status-delivered
                "
              >
                Entregados
              </span>

              <strong className="metric-value">
                {metrics.delivered}
              </strong>

              <span className="metric-footer">
                Pedidos finalizados
              </span>
            </button>

            <button
              type="button"
              className="metric-card"
              onClick={() =>
                goToOrders("CANCELLED")
              }
            >
              <span
                className="
                  status-badge
                  status-cancelled
                "
              >
                Cancelados
              </span>

              <strong className="metric-value">
                {metrics.cancelled}
              </strong>

              <span className="metric-footer">
                Pedidos cancelados
              </span>
            </button>
          </div>

          <section className="dashboard-card dashboard-main-card">
            <span className="card-label">
              Operaciones
            </span>

            <h2>
              Gestión de pedidos
            </h2>

            <p>
              Consulta pedidos, revisa sus detalles,
              aplica filtros y realiza el seguimiento
              de su estado.
            </p>

            <button
              type="button"
              className="primary-button"
              onClick={() => navigate("/orders")}
            >
              Administrar pedidos
            </button>
          </section>
        </>
      )}
    </div>
  );
}

export default DashboardPage;