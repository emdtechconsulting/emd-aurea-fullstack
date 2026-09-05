import { useEffect, useState } from "react";
import {
  useNavigate,
  useSearchParams,
} from "react-router-dom";

import { getOrders } from "../services/orderService";

function OrdersPage() {
  const navigate = useNavigate();

  const [searchParams] = useSearchParams();

  const initialStatus =
  searchParams.get("status") ?? "";


  const [orders, setOrders] = useState([]);

  const [page, setPage] = useState(0);
  const [size] = useState(5);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [first, setFirst] = useState(true);
  const [last, setLast] = useState(true);

  const [customerName, setCustomerName] = useState("");
 const [status, setStatus] =
  useState(initialStatus);

  const [appliedCustomerName, setAppliedCustomerName] =
    useState("");

 const [appliedStatus, setAppliedStatus] =
  useState(initialStatus);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const getStatusInfo = (orderStatus) => {
    switch (orderStatus) {
      case "DRAFT":
        return {
          label: "Borrador",
          className: "status-badge status-draft",
        };

      case "CONFIRMED":
        return {
          label: "Confirmado",
          className: "status-badge status-confirmed",
        };

      case "PREPARING":
        return {
          label: "En preparación",
          className: "status-badge status-preparing",
        };

      case "DELIVERED":
        return {
          label: "Entregado",
          className: "status-badge status-delivered",
        };

      case "CANCELLED":
        return {
          label: "Cancelado",
          className: "status-badge status-cancelled",
        };

      default:
        return {
          label: orderStatus,
          className: "status-badge",
        };
    }
  };

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getOrders({
          page,
          size,
          customerName: appliedCustomerName,
          status: appliedStatus,
        });

        setOrders(data.content);

        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);

        setFirst(data.first);
        setLast(data.last);
      } catch (err) {
        console.error(err);

        setError("No se pudieron cargar los pedidos.");
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, [
    page,
    size,
    appliedCustomerName,
    appliedStatus,
  ]);

  const handleSearch = (event) => {
    event.preventDefault();

    setPage(0);
    setAppliedCustomerName(customerName);
    setAppliedStatus(status);
  };

  const handleClearFilters = () => {
    setCustomerName("");
    setStatus("");

    setAppliedCustomerName("");
    setAppliedStatus("");

    setPage(0);
  };

  const handlePreviousPage = () => {
    if (!first) {
      setPage((currentPage) => currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (!last) {
      setPage((currentPage) => currentPage + 1);
    }
  };

  return (
    <div>
      <div className="page-header page-header-row">
        <div>
          <h1>Pedidos</h1>

          <p>
            Consulta, filtra y administra los pedidos
            registrados en AUREA.
          </p>
        </div>

        <div className="page-actions">
  <button
    type="button"
    className="secondary-button"
    onClick={() => navigate("/dashboard")}
  >
    Volver al panel
  </button>

  <button
    type="button"
    className="primary-button"
    onClick={() => navigate("/orders/new")}
  >
    Nuevo pedido
  </button>
</div>
      </div>

      <form
        className="filters-card"
        onSubmit={handleSearch}
      >
        <div className="filter-field">
          <label htmlFor="customerName">
            Cliente
          </label>

          <input
            id="customerName"
            type="text"
            value={customerName}
            onChange={(event) =>
              setCustomerName(event.target.value)
            }
            placeholder="Buscar por nombre"
          />
        </div>

        <div className="filter-field">
          <label htmlFor="status">
            Estado
          </label>

          <select
            id="status"
            value={status}
            onChange={(event) =>
              setStatus(event.target.value)
            }
          >
            <option value="">
              Todos
            </option>

            <option value="DRAFT">
              Borrador
            </option>

            <option value="CONFIRMED">
              Confirmado
            </option>

            <option value="PREPARING">
              En preparación
            </option>

            <option value="DELIVERED">
              Entregado
            </option>

            <option value="CANCELLED">
              Cancelado
            </option>
          </select>
        </div>

        <div className="filter-actions">
          <button
            type="submit"
            className="primary-button"
          >
            Buscar
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={handleClearFilters}
          >
            Limpiar
          </button>
        </div>
      </form>

      <div className="orders-summary">
        <div>
          <span className="summary-label">
            Resultados
          </span>

          <strong>
            {totalElements}
          </strong>
        </div>

        <div>
          <span className="summary-label">
            Página actual
          </span>

          <strong>
            {totalPages === 0 ? 0 : page + 1}
          </strong>
        </div>

        <div>
          <span className="summary-label">
            Total de páginas
          </span>

          <strong>
            {totalPages}
          </strong>
        </div>
      </div>

      {loading ? (
        <div className="loading-panel">
          Cargando pedidos...
        </div>
      ) : error ? (
        <div className="error-panel">
          {error}
        </div>
      ) : (
        <>
          <div className="table-card">
            {orders.length === 0 ? (
              <div className="empty-state">
                No se encontraron pedidos
                con los filtros seleccionados.
              </div>
            ) : (
              <div className="table-responsive">
                <table className="orders-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Cliente</th>
                      <th>Fecha</th>
                      <th>Hora</th>
                      <th>Distrito</th>
                      <th>Total</th>
                      <th>Estado</th>
                      <th>Acción</th>
                    </tr>
                  </thead>

                  <tbody>
                    {orders.map((order) => {
                      const statusInfo =
                        getStatusInfo(order.status);

                      return (
                        <tr key={order.id}>
                          <td>
                            <strong>
                              #{order.id}
                            </strong>
                          </td>

                          <td>
                            {order.customerName}
                          </td>

                          <td>
                            {order.deliveryDate ?? "-"}
                          </td>

                          <td>
                            {order.deliveryTime ?? "-"}
                          </td>

                          <td>
                            {order.district ?? "-"}
                          </td>

                          <td className="money-cell">
                            S/{" "}
                            {Number(
                              order.total
                            ).toFixed(2)}
                          </td>

                          <td>
                            <span
                              className={
                                statusInfo.className
                              }
                            >
                              {statusInfo.label}
                            </span>
                          </td>

                          <td>
                            <button
                              type="button"
                              className="table-action-button"
                              onClick={() =>
                                navigate(
                                  `/orders/${order.id}`
                                )
                              }
                            >
                              Ver detalle
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {totalPages > 0 && (
            <div className="pagination">
              <button
                type="button"
                onClick={handlePreviousPage}
                disabled={first}
              >
                Anterior
              </button>

              <span>
                Página{" "}
                <strong>{page + 1}</strong>
                {" "}de{" "}
                <strong>{totalPages}</strong>
              </span>

              <button
                type="button"
                onClick={handleNextPage}
                disabled={last}
              >
                Siguiente
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default OrdersPage;