import { useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  createOrder,
} from "../services/orderService";

function CreateOrderPage() {
  const navigate = useNavigate();

  const [formData, setFormData] =
    useState({
      customerName: "",
      deliveryDate: "",
      deliveryTime: "",
      requiresDelivery: false,
      address: "",
      district: "",
      deliveryFee: "0",
      source: "WEB",
      observations: "",
    });

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const handleChange = (event) => {
    const {
      name,
      value,
      type,
      checked,
    } = event.target;

    setFormData((current) => {
      const updated = {
        ...current,

        [name]:
          type === "checkbox"
            ? checked
            : value,
      };

      /*
       * Si se desactiva el costo de delivery,
       * dejamos inmediatamente su importe en 0.
       *
       * Dirección y distrito NO se eliminan.
       */
      if (
        name === "requiresDelivery" &&
        type === "checkbox" &&
        !checked
      ) {
        updated.deliveryFee = "0";
      }

      return updated;
    });
  };

  const handleSubmit = async (
    event
  ) => {
    event.preventDefault();

    setError("");

    if (
      !formData.customerName.trim()
    ) {
      setError(
        "El nombre del cliente es obligatorio."
      );

      return;
    }

    if (
      formData.requiresDelivery &&
      Number(formData.deliveryFee) < 0
    ) {
      setError(
        "El costo del delivery no puede ser negativo."
      );

      return;
    }

    try {
      setLoading(true);

      const payload = {
        customerName:
          formData.customerName.trim(),

        deliveryDate:
          formData.deliveryDate ||
          null,

        deliveryTime:
          formData.deliveryTime ||
          null,

        /*
         * requiresDelivery ahora significa:
         *
         * ¿Se aplica costo de delivery?
         */
        requiresDelivery:
          formData.requiresDelivery,

        /*
         * Dirección y distrito son
         * independientes del costo.
         */
        address:
          formData.address.trim() ||
          null,

        district:
          formData.district.trim() ||
          null,

        /*
         * Si no se aplica costo,
         * enviamos siempre cero.
         */
        deliveryFee:
          formData.requiresDelivery
            ? Number(
                formData.deliveryFee ||
                  0
              )
            : 0,

        source:
          formData.source,

        observations:
          formData.observations.trim() ||
          null,
      };

      const createdOrder =
        await createOrder(
          payload
        );

      navigate(
        `/orders/${createdOrder.id}`
      );
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      setError(
        backendMessage ||
          "No se pudo crear el pedido."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header page-header-row">
        <div>
          <h1>
            Nuevo pedido
          </h1>

          <p>
            Registra los datos generales
            y la información de entrega.
          </p>
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={() =>
            navigate("/orders")
          }
        >
          Cancelar
        </button>
      </div>

      <form
        className="order-form-card"
        onSubmit={handleSubmit}
      >
        {/* =========================
            CLIENTE
            ========================= */}

        <div className="form-section">
          <h2>
            Cliente
          </h2>

          <div className="form-grid">
            <div className="filter-field">
              <label
                htmlFor="customerName"
              >
                Nombre del cliente
              </label>

              <input
                id="customerName"
                name="customerName"
                type="text"
                value={
                  formData.customerName
                }
                onChange={
                  handleChange
                }
                required
              />
            </div>

            <div className="filter-field">
              <label
                htmlFor="source"
              >
                Origen
              </label>

              <select
                id="source"
                name="source"
                value={
                  formData.source
                }
                onChange={
                  handleChange
                }
              >
                <option value="WEB">
                  Web
                </option>

                <option value="MOBILE">
                  Móvil
                </option>

                <option value="MANUAL">
                  Manual
                </option>
              </select>
            </div>
          </div>
        </div>

        {/* =========================
            ENTREGA
            ========================= */}

        <div className="form-section">
          <h2>
            Entrega
          </h2>

          <div className="form-grid">
            <div className="filter-field">
              <label
                htmlFor="deliveryDate"
              >
                Fecha de entrega
              </label>

              <input
                id="deliveryDate"
                name="deliveryDate"
                type="date"
                value={
                  formData.deliveryDate
                }
                onChange={
                  handleChange
                }
              />
            </div>

            <div className="filter-field">
              <label
                htmlFor="deliveryTime"
              >
                Hora de entrega
              </label>

              <input
                id="deliveryTime"
                name="deliveryTime"
                type="time"
                value={
                  formData.deliveryTime
                }
                onChange={
                  handleChange
                }
              />
            </div>

            <div className="filter-field">
              <label
                htmlFor="address"
              >
                Dirección de entrega
              </label>

              <input
                id="address"
                name="address"
                type="text"
                value={
                  formData.address
                }
                onChange={
                  handleChange
                }
                placeholder="Ej. Av. Los Cedros 500"
              />
            </div>

            <div className="filter-field">
              <label
                htmlFor="district"
              >
                Distrito
              </label>

              <input
                id="district"
                name="district"
                type="text"
                value={
                  formData.district
                }
                onChange={
                  handleChange
                }
                placeholder="Ej. Chorrillos"
              />
            </div>
          </div>

          <div className="delivery-edit-block">
            <label className="checkbox-field">
              <input
                name="requiresDelivery"
                type="checkbox"
                checked={
                  formData.requiresDelivery
                }
                onChange={
                  handleChange
                }
              />

              Aplicar costo de delivery
            </label>

            {formData.requiresDelivery && (
              <div className="form-grid">
                <div className="filter-field">
                  <label
                    htmlFor="deliveryFee"
                  >
                    Costo del delivery
                  </label>

                  <input
                    id="deliveryFee"
                    name="deliveryFee"
                    type="number"
                    min="0"
                    step="0.01"
                    value={
                      formData.deliveryFee
                    }
                    onChange={
                      handleChange
                    }
                    placeholder="0.00"
                  />
                </div>
              </div>
            )}
          </div>
        </div>

        {/* =========================
            OBSERVACIONES
            ========================= */}

        <div className="form-section">
          <h2>
            Observaciones
          </h2>

          <textarea
            name="observations"
            rows="4"
            value={
              formData.observations
            }
            onChange={
              handleChange
            }
            placeholder="Referencia de entrega, persona que recibe, indicaciones especiales, etc."
          />
        </div>

        {error && (
          <div className="error-panel">
            {error}
          </div>
        )}

        <div className="form-actions">
          <button
            type="button"
            className="secondary-button"
            onClick={() =>
              navigate("/orders")
            }
            disabled={loading}
          >
            Cancelar
          </button>

          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            {loading
              ? "Guardando..."
              : "Crear pedido"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default CreateOrderPage;