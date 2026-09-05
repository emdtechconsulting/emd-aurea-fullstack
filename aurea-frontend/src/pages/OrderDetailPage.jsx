import { useEffect, useState } from "react";
import Swal from "sweetalert2";

import {
  useNavigate,
  useParams,
} from "react-router-dom";

import {
  addCatalogItem,
  addManualItem,
  deleteOrderItem,
  getOrderById,
  updateOrder,
  updateOrderStatus,
} from "../services/orderService";

import { getProducts } from "../services/productService";

function OrderDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [order, setOrder] = useState(null);
  const [products, setProducts] = useState([]);

  const [
    selectedProductPriceId,
    setSelectedProductPriceId,
  ] = useState("");

  const [manualItem, setManualItem] = useState({
    description: "",
    quantity: "1",
    unitPrice: "",
    notes: "",
  });

  const [editForm, setEditForm] = useState({
    customerName: "",
    deliveryDate: "",
    deliveryTime: "",
    requiresDelivery: false,
    address: "",
    district: "",
    deliveryFee: "0",
    observations: "",
  });

  const [editing, setEditing] =
    useState(false);

  const [savingOrder, setSavingOrder] =
    useState(false);

  const [loading, setLoading] =
    useState(true);

  const [addingItem, setAddingItem] =
    useState(false);

  const [
    addingManualItem,
    setAddingManualItem,
  ] = useState(false);

  const [
    deletingItemId,
    setDeletingItemId,
  ] = useState(null);

  const [error, setError] =
    useState("");

  const [itemMessage, setItemMessage] =
    useState("");

  const [
    manualMessage,
    setManualMessage,
  ] = useState("");

  const [
    deleteMessage,
    setDeleteMessage,
  ] = useState("");

  const [
    editMessage,
    setEditMessage,
  ] = useState("");

  const [
    changingStatus,
    setChangingStatus,
  ] = useState(false);

  const [
    statusMessage,
    setStatusMessage,
  ] = useState("");

  const getStatusInfo = (status) => {
    switch (status) {
      case "DRAFT":
        return {
          label: "Borrador",
          className:
            "status-badge status-draft",
        };

      case "CONFIRMED":
        return {
          label: "Confirmado",
          className:
            "status-badge status-confirmed",
        };

      case "PREPARING":
        return {
          label: "En preparación",
          className:
            "status-badge status-preparing",
        };

      case "DELIVERED":
        return {
          label: "Entregado",
          className:
            "status-badge status-delivered",
        };

      case "CANCELLED":
        return {
          label: "Cancelado",
          className:
            "status-badge status-cancelled",
        };

      default:
        return {
          label: status,
          className: "status-badge",
        };
    }
  };

  const getStatusActions = (status) => {
    switch (status) {
      case "DRAFT":
        return [
          {
            targetStatus: "CONFIRMED",
            label: "Confirmar pedido",
            className: "primary-button",
          },
          {
            targetStatus: "CANCELLED",
            label: "Cancelar pedido",
            className: "secondary-button",
          },
        ];

      case "CONFIRMED":
        return [
          {
            targetStatus: "PREPARING",
            label: "Iniciar preparación",
            className: "primary-button",
          },
          {
            targetStatus: "CANCELLED",
            label: "Cancelar pedido",
            className: "secondary-button",
          },
        ];

      case "PREPARING":
        return [
          {
            targetStatus: "DELIVERED",
            label: "Marcar como entregado",
            className: "primary-button",
          },
          {
            targetStatus: "CANCELLED",
            label: "Cancelar pedido",
            className: "secondary-button",
          },
        ];

      default:
        return [];
    }
  };

  const prepareEditForm = (data) => {
    setEditForm({
      customerName:
        data.customerName ?? "",

      deliveryDate:
        data.deliveryDate ?? "",

      deliveryTime:
        data.deliveryTime
          ? data.deliveryTime.slice(0, 5)
          : "",

      requiresDelivery:
        Boolean(data.requiresDelivery),

      address:
        data.address ?? "",

      district:
        data.district ?? "",

      deliveryFee:
        data.deliveryFee != null
          ? String(data.deliveryFee)
          : "0",

      observations:
        data.observations ?? "",
    });
  };

  const loadOrder = async () => {
    const data =
      await getOrderById(id);

    setOrder(data);

    return data;
  };

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError("");

        const [
          orderData,
          productsData,
        ] = await Promise.all([
          getOrderById(id),
          getProducts(),
        ]);

        setOrder(orderData);

        prepareEditForm(
          orderData
        );

        setProducts(
          productsData.filter(
            (product) =>
              product.active
          )
        );
      } catch (err) {
        console.error(err);

        if (
          err.response?.status ===
          404
        ) {
          setError(
            "El pedido no existe."
          );
        } else {
          setError(
            "No se pudo cargar la información del pedido."
          );
        }
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [id]);

  const handleEditChange = (
    event
  ) => {
    const {
      name,
      value,
      type,
      checked,
    } = event.target;

    setEditForm(
      (current) => ({
        ...current,

        [name]:
          type === "checkbox"
            ? checked
            : value,
      })
    );
  };

  const handleStartEdit = () => {
    prepareEditForm(order);

    setEditMessage("");
    setEditing(true);
  };

  const handleCancelEdit = () => {
    prepareEditForm(order);

    setEditMessage("");
    setEditing(false);
  };

  const handleUpdateOrder = async (
    event
  ) => {
    event.preventDefault();

    setEditMessage("");

    if (
      !editForm.customerName.trim()
    ) {
      setEditMessage(
        "El nombre del cliente es obligatorio."
      );

      return;
    }

    if (
      Number(
        editForm.deliveryFee
      ) < 0
    ) {
      setEditMessage(
        "El costo del delivery no puede ser negativo."
      );

      return;
    }

    try {
      setSavingOrder(true);

      const payload = {
        customerName:
          editForm.customerName.trim(),

        deliveryDate:
          editForm.deliveryDate ||
          null,

        deliveryTime:
          editForm.deliveryTime ||
          null,

        requiresDelivery:
          editForm.requiresDelivery,

        address:
          editForm.address.trim() ||
          null,

        district:
          editForm.district.trim() ||
          null,

        deliveryFee:
          editForm.requiresDelivery
            ? Number(
                editForm.deliveryFee ||
                  0
              )
            : 0,

        observations:
          editForm.observations.trim() ||
          null,
      };

      await updateOrder(
        id,
        payload
      );

      const updatedOrder =
        await loadOrder();

      prepareEditForm(
        updatedOrder
      );

      setEditing(false);

      setEditMessage(
        "Pedido actualizado correctamente."
      );
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      setEditMessage(
        backendMessage ||
          "No se pudo actualizar el pedido."
      );
    } finally {
      setSavingOrder(false);
    }
  };

  const handleAddCatalogItem = async (
    event
  ) => {
    event.preventDefault();

    setDeleteMessage("");

    if (
      !selectedProductPriceId
    ) {
      setItemMessage(
        "Selecciona un producto y una presentación."
      );

      return;
    }

    try {
      setAddingItem(true);
      setItemMessage("");

      await addCatalogItem(
        id,
        Number(
          selectedProductPriceId
        )
      );

      await loadOrder();

      setSelectedProductPriceId(
        ""
      );

      setItemMessage(
        "Producto agregado correctamente."
      );
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      setItemMessage(
        backendMessage ||
          "No se pudo agregar el producto."
      );
    } finally {
      setAddingItem(false);
    }
  };

  const handleManualItemChange = (
    event
  ) => {
    const {
      name,
      value,
    } = event.target;

    setManualItem(
      (current) => ({
        ...current,
        [name]: value,
      })
    );
  };

  const handleAddManualItem = async (
    event
  ) => {
    event.preventDefault();

    setManualMessage("");
    setDeleteMessage("");

    if (
      !manualItem.description.trim()
    ) {
      setManualMessage(
        "La descripción es obligatoria."
      );

      return;
    }

    if (
      Number(
        manualItem.quantity
      ) <= 0
    ) {
      setManualMessage(
        "La cantidad debe ser mayor que cero."
      );

      return;
    }

    if (
      manualItem.unitPrice === "" ||
      Number(
        manualItem.unitPrice
      ) < 0
    ) {
      setManualMessage(
        "Ingresa un precio válido."
      );

      return;
    }

    try {
      setAddingManualItem(
        true
      );

      await addManualItem(
        id,
        {
          description:
            manualItem.description.trim(),

          quantity:
            Number(
              manualItem.quantity
            ),

          unitPrice:
            Number(
              manualItem.unitPrice
            ),

          notes:
            manualItem.notes.trim() ||
            null,
        }
      );

      await loadOrder();

      setManualItem({
        description: "",
        quantity: "1",
        unitPrice: "",
        notes: "",
      });

      setManualMessage(
        "Producto manual agregado correctamente."
      );
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      setManualMessage(
        backendMessage ||
          "No se pudo agregar el producto manual."
      );
    } finally {
      setAddingManualItem(
        false
      );
    }
  };

  const handleDeleteItem = async (
    itemId
  ) => {
    const result = await Swal.fire({
      title: "Eliminar producto",
      text: "¿Deseas eliminar este producto del pedido?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "No, volver",
    });

    if (!result.isConfirmed) {
      return;
    }

    try {
      setDeletingItemId(
        itemId
      );

      setDeleteMessage("");
      setItemMessage("");
      setManualMessage("");

      await deleteOrderItem(
        id,
        itemId
      );

      await loadOrder();

      setDeleteMessage("");

      await Swal.fire({
        title: "Producto eliminado",
        text: "El producto fue eliminado correctamente del pedido.",
        icon: "success",
        confirmButtonText: "Aceptar",
      });
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      setDeleteMessage(
        backendMessage ||
          "No se pudo eliminar el producto."
      );
    } finally {
      setDeletingItemId(
        null
      );
    }
  };

  const handleChangeStatus = async (
    targetStatus
  ) => {
    setStatusMessage("");

    if (
      order.status === "DRAFT" &&
      targetStatus === "CONFIRMED" &&
      (!order.items ||
        order.items.length === 0)
    ) {
      await Swal.fire({
        title: "Pedido sin productos",
        text: "Antes de confirmar el pedido debes agregar al menos un producto.",
        icon: "warning",
        confirmButtonText: "Entendido",
      });
      return;
    }

    const targetInfo =
      getStatusInfo(targetStatus);

    const confirmationText =
      targetStatus === "CANCELLED"
        ? "¿Confirmas que deseas cancelar este pedido? Esta acción cambiará su estado a Cancelado."
        : `¿Confirmas el cambio de estado a "${targetInfo.label}"?`;

    const result = await Swal.fire({
      title:
        targetStatus === "CANCELLED"
          ? "Cancelar pedido"
          : "Cambiar estado",
      text: confirmationText,
      icon:
        targetStatus === "CANCELLED"
          ? "warning"
          : "question",
      showCancelButton: true,
      confirmButtonText:
        targetStatus === "CANCELLED"
          ? "Sí, cancelar pedido"
          : "Sí, continuar",
      cancelButtonText: "No, volver",
    });

    if (!result.isConfirmed) {
      return;
    }

    try {
      setChangingStatus(true);
      setStatusMessage("");

      await updateOrderStatus(
        id,
        targetStatus
      );

      const updatedOrder =
        await loadOrder();

      prepareEditForm(
        updatedOrder
      );

      setStatusMessage("");

      await Swal.fire({
        title: "Estado actualizado",
        text: `El pedido ahora está en estado "${getStatusInfo(updatedOrder.status).label}".`,
        icon: "success",
        timer: 5000,
        timerProgressBar: true,
        showConfirmButton: false,
      });
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message;

      const message =
        backendMessage ||
        "No se pudo actualizar el estado del pedido.";

      setStatusMessage("");

      await Swal.fire({
        title: "No se pudo cambiar el estado",
        text: message,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    } finally {
      setChangingStatus(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-panel">
        Cargando pedido...
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <div className="error-panel">
          {error}
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={() =>
            navigate("/orders")
          }
        >
          Volver a pedidos
        </button>
      </div>
    );
  }

  if (!order) {
    return null;
  }

  const statusInfo =
    getStatusInfo(
      order.status
    );

  const statusActions =
    getStatusActions(
      order.status
    );

  return (
    <div>
      <div className="page-header page-header-row">
        <div>
          <h1>
            Pedido #{order.id}
          </h1>

          <p>
            Consulta y administra la
            información completa del pedido.
          </p>
        </div>

        <div className="page-actions">
          {order.status ===
            "DRAFT" &&
            !editing && (
              <button
                type="button"
                className="primary-button"
                onClick={
                  handleStartEdit
                }
              >
                Editar pedido
              </button>
            )}

          <button
            type="button"
            className="secondary-button"
            onClick={() =>
              navigate("/orders")
            }
          >
            Volver a pedidos
          </button>
        </div>
      </div>

      {editMessage && (
        <div className="catalog-message edit-message">
          {editMessage}
        </div>
      )}

      {statusMessage && (
        <div className="catalog-message edit-message">
          {statusMessage}
        </div>
      )}

      {editing ? (
        <section className="detail-card detail-card-full">
          <div className="detail-card-header">
            <h2>
              Editar pedido
            </h2>
          </div>

          <form
            className="edit-order-form"
            onSubmit={
              handleUpdateOrder
            }
          >
            <div className="form-grid">
              <div className="filter-field">
                <label
                  htmlFor="editCustomerName"
                >
                  Cliente
                </label>

                <input
                  id="editCustomerName"
                  name="customerName"
                  type="text"
                  value={
                    editForm.customerName
                  }
                  onChange={
                    handleEditChange
                  }
                  required
                />
              </div>

              <div className="filter-field">
                <label
                  htmlFor="editDeliveryDate"
                >
                  Fecha de entrega
                </label>

                <input
                  id="editDeliveryDate"
                  name="deliveryDate"
                  type="date"
                  value={
                    editForm.deliveryDate
                  }
                  onChange={
                    handleEditChange
                  }
                />
              </div>

              <div className="filter-field">
                <label
                  htmlFor="editDeliveryTime"
                >
                  Hora de entrega
                </label>

                <input
                  id="editDeliveryTime"
                  name="deliveryTime"
                  type="time"
                  value={
                    editForm.deliveryTime
                  }
                  onChange={
                    handleEditChange
                  }
                />
              </div>
            </div>

            <div className="form-grid">
              <div className="filter-field">
                <label
                  htmlFor="editAddress"
                >
                  Dirección de entrega
                </label>

                <input
                  id="editAddress"
                  name="address"
                  type="text"
                  value={
                    editForm.address
                  }
                  onChange={
                    handleEditChange
                  }
                  placeholder="Dirección del cliente"
                />
              </div>

              <div className="filter-field">
                <label
                  htmlFor="editDistrict"
                >
                  Distrito
                </label>

                <input
                  id="editDistrict"
                  name="district"
                  type="text"
                  value={
                    editForm.district
                  }
                  onChange={
                    handleEditChange
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
                    editForm.requiresDelivery
                  }
                  onChange={
                    handleEditChange
                  }
                />

                Aplicar costo de delivery
              </label>

              {editForm.requiresDelivery && (
                <div className="form-grid">
                  <div className="filter-field">
                    <label
                      htmlFor="editDeliveryFee"
                    >
                      Costo del delivery
                    </label>

                    <input
                      id="editDeliveryFee"
                      name="deliveryFee"
                      type="number"
                      min="0"
                      step="0.01"
                      value={
                        editForm.deliveryFee
                      }
                      onChange={
                        handleEditChange
                      }
                    />
                  </div>
                </div>
              )}
            </div>

            <div className="filter-field">
              <label
                htmlFor="editObservations"
              >
                Observaciones
              </label>

              <textarea
                id="editObservations"
                name="observations"
                rows="4"
                value={
                  editForm.observations
                }
                onChange={
                  handleEditChange
                }
                placeholder="Indicaciones del pedido, referencia de entrega, persona que recibe, etc."
              />
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="secondary-button"
                onClick={
                  handleCancelEdit
                }
                disabled={
                  savingOrder
                }
              >
                Cancelar
              </button>

              <button
                type="submit"
                className="primary-button"
                disabled={
                  savingOrder
                }
              >
                {savingOrder
                  ? "Guardando..."
                  : "Guardar cambios"}
              </button>
            </div>
          </form>
        </section>
      ) : (
        <>
          <div className="detail-grid">
            <section className="detail-card">
              <div className="detail-card-header">
                <h2>
                  Cliente
                </h2>
              </div>

              <div className="detail-list">
                <div>
                  <span>
                    Nombre
                  </span>

                  <strong>
                    {
                      order.customerName
                    }
                  </strong>
                </div>

                <div>
                  <span>
                    Origen
                  </span>

                  <strong>
                    {order.source}
                  </strong>
                </div>

                <div>
                  <span>
                    Estado
                  </span>

                  <span
                    className={
                      statusInfo.className
                    }
                  >
                    {
                      statusInfo.label
                    }
                  </span>
                </div>
              </div>
            </section>

            <section className="detail-card">
              <div className="detail-card-header">
                <h2>
                  Entrega
                </h2>
              </div>

              <div className="detail-list">
                <div>
                  <span>
                    Fecha
                  </span>

                  <strong>
                    {order.deliveryDate ??
                      "-"}
                  </strong>
                </div>

                <div>
                  <span>
                    Hora
                  </span>

                  <strong>
                    {order.deliveryTime ??
                      "-"}
                  </strong>
                </div>

                <div>
                  <span>
                    Dirección
                  </span>

                  <strong>
                    {order.address ??
                      "-"}
                  </strong>
                </div>

                <div>
                  <span>
                    Distrito
                  </span>

                  <strong>
                    {order.district ??
                      "-"}
                  </strong>
                </div>

                <div>
                  <span>
                    Costo de delivery aplicado
                  </span>

                  <strong>
                    {
                      order.requiresDelivery
                        ? "Sí"
                        : "No"
                    }
                  </strong>
                </div>

                <div>
                  <span>
                    Costo delivery
                  </span>

                  <strong>
                    S/{" "}
                    {Number(
                      order.deliveryFee
                    ).toFixed(2)}
                  </strong>
                </div>
              </div>
            </section>
          </div>

          <section className="detail-card detail-card-full">
            <div className="detail-card-header">
              <h2>
                Observaciones
              </h2>
            </div>

            <p className="detail-observations">
              {order.observations ??
                "Sin observaciones registradas."}
            </p>
          </section>
        </>
      )}

      {order.status ===
        "DRAFT" &&
        !editing && (
          <section className="detail-card detail-card-full">
            <div className="detail-card-header">
              <h2>
                Agregar producto del catálogo
              </h2>
            </div>

            <form
              className="catalog-add-form"
              onSubmit={
                handleAddCatalogItem
              }
            >
              <div className="filter-field">
                <label htmlFor="productPrice">
                  Producto y presentación
                </label>

                <select
                  id="productPrice"
                  value={
                    selectedProductPriceId
                  }
                  onChange={(event) =>
                    setSelectedProductPriceId(
                      event.target.value
                    )
                  }
                >
                  <option value="">
                    Selecciona una opción
                  </option>

                  {products.map(
                    (product) =>
                      product.prices
                        ?.filter(
                          (price) =>
                            price.active
                        )
                        .map(
                          (price) => (
                            <option
                              key={
                                price.id
                              }
                              value={
                                price.id
                              }
                            >
                              {
                                product.categoryName
                              }
                              {" - "}
                              {
                                product.name
                              }
                              {" - "}
                              {
                                price.quantity
                              }
                              {
                                " unidades - S/ "
                              }
                              {Number(
                                price.price
                              ).toFixed(
                                2
                              )}
                            </option>
                          )
                        )
                  )}
                </select>
              </div>

              <button
                type="submit"
                className="primary-button"
                disabled={
                  addingItem
                }
              >
                {addingItem
                  ? "Agregando..."
                  : "Agregar producto"}
              </button>
            </form>

            {itemMessage && (
              <div className="catalog-message">
                {
                  itemMessage
                }
              </div>
            )}
          </section>
        )}

      {order.status ===
        "DRAFT" &&
        !editing && (
          <section className="detail-card detail-card-full">
            <div className="detail-card-header">
              <h2>
                Agregar producto manual
              </h2>
            </div>

            <form
              className="manual-item-form"
              onSubmit={
                handleAddManualItem
              }
            >
              <div className="form-grid">
                <div className="filter-field">
                  <label htmlFor="manualDescription">
                    Descripción
                  </label>

                  <input
                    id="manualDescription"
                    name="description"
                    type="text"
                    value={
                      manualItem.description
                    }
                    onChange={
                      handleManualItemChange
                    }
                    placeholder="Ej. Bandeja especial"
                  />
                </div>

                <div className="filter-field">
                  <label htmlFor="manualQuantity">
                    Cantidad
                  </label>

                  <input
                    id="manualQuantity"
                    name="quantity"
                    type="number"
                    min="1"
                    step="1"
                    value={
                      manualItem.quantity
                    }
                    onChange={
                      handleManualItemChange
                    }
                  />
                </div>

                <div className="filter-field">
                  <label htmlFor="manualUnitPrice">
                    Precio unitario
                  </label>

                  <input
                    id="manualUnitPrice"
                    name="unitPrice"
                    type="number"
                    min="0"
                    step="0.01"
                    value={
                      manualItem.unitPrice
                    }
                    onChange={
                      handleManualItemChange
                    }
                    placeholder="0.00"
                  />
                </div>

                <div className="filter-field">
                  <label htmlFor="manualNotes">
                    Notas
                  </label>

                  <input
                    id="manualNotes"
                    name="notes"
                    type="text"
                    value={
                      manualItem.notes
                    }
                    onChange={
                      handleManualItemChange
                    }
                    placeholder="Indicaciones opcionales"
                  />
                </div>
              </div>

              <div className="manual-item-actions">
                <button
                  type="submit"
                  className="primary-button"
                  disabled={
                    addingManualItem
                  }
                >
                  {addingManualItem
                    ? "Agregando..."
                    : "Agregar producto manual"}
                </button>
              </div>
            </form>

            {manualMessage && (
              <div className="catalog-message">
                {
                  manualMessage
                }
              </div>
            )}
          </section>
        )}

      <section className="detail-card detail-card-full">
        <div
          className="
            detail-card-header
            detail-card-header-row
          "
        >
          <h2>
            Productos
          </h2>

          <span className="items-counter">
            {order.items?.length ??
              0}{" "}
            item(s)
          </span>
        </div>

        {order.items?.length ===
        0 ? (
          <div className="empty-state">
            Este pedido no tiene productos.
          </div>
        ) : (
          <div className="table-responsive">
            <table className="orders-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>
                    Descripción
                  </th>
                  <th>
                    Tipo
                  </th>
                  <th>
                    Cantidad
                  </th>
                  <th>
                    Precio
                  </th>
                  <th>
                    Subtotal
                  </th>

                  {order.status ===
                    "DRAFT" &&
                    !editing && (
                      <th>
                        Acción
                      </th>
                    )}
                </tr>
              </thead>

              <tbody>
                {order.items?.map(
                  (item) => (
                    <tr
                      key={
                        item.id
                      }
                    >
                      <td>
                        #{item.id}
                      </td>

                      <td>
                        {
                          item.description
                        }
                      </td>

                      <td>
                        {
                          item.itemType
                        }
                      </td>

                      <td>
                        {
                          item.quantity
                        }
                      </td>

                      <td className="money-cell">
                        S/{" "}
                        {Number(
                          item.referencePrice
                        ).toFixed(
                          2
                        )}
                      </td>

                      <td className="money-cell">
                        S/{" "}
                        {Number(
                          item.subtotal
                        ).toFixed(
                          2
                        )}
                      </td>

                      {order.status ===
                        "DRAFT" &&
                        !editing && (
                          <td>
                            <button
                              type="button"
                              className="delete-item-button"
                              disabled={
                                deletingItemId ===
                                item.id
                              }
                              onClick={() =>
                                handleDeleteItem(
                                  item.id
                                )
                              }
                            >
                              {deletingItemId ===
                              item.id
                                ? "Eliminando..."
                                : "Eliminar"}
                            </button>
                          </td>
                        )}
                    </tr>
                  )
                )}
              </tbody>
            </table>
          </div>
        )}

        {deleteMessage && (
          <div className="catalog-message">
            {
              deleteMessage
            }
          </div>
        )}
      </section>

      <section className="totals-card">
        <div>
          <span>
            Subtotal productos
          </span>

          <strong>
            S/{" "}
            {Number(
              order.productsSubtotal
            ).toFixed(2)}
          </strong>
        </div>

        <div>
          <span>
            Costo delivery
          </span>

          <strong>
            S/{" "}
            {Number(
              order.deliveryFee
            ).toFixed(2)}
          </strong>
        </div>

        <div className="total-final">
          <span>
            Total
          </span>

          <strong>
            S/{" "}
            {Number(
              order.total
            ).toFixed(2)}
          </strong>
        </div>
      </section>

      {!editing &&
        statusActions.length > 0 && (
          <section className="detail-card detail-card-full">
            <div className="detail-card-header">
              <h2>
                Flujo del pedido
              </h2>
            </div>

            <div className="edit-order-form">
              <div className="detail-list">
                <div>
                  <span>
                    Estado actual
                  </span>

                  <span
                    className={
                      statusInfo.className
                    }
                  >
                    {statusInfo.label}
                  </span>
                </div>
              </div>

              <div className="form-actions">
                {statusActions.map(
                  (action) => (
                    <button
                      key={
                        action.targetStatus
                      }
                      type="button"
                      className={
                        action.className
                      }
                      disabled={
                        changingStatus
                      }
                      onClick={() =>
                        handleChangeStatus(
                          action.targetStatus
                        )
                      }
                    >
                      {changingStatus
                        ? "Procesando..."
                        : action.label}
                    </button>
                  )
                )}
              </div>
            </div>
          </section>
        )}
    </div>
  );
}

export default OrderDetailPage;