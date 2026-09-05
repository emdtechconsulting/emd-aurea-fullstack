import apiClient from "../api/apiClient";

export const getOrders = async ({
  page = 0,
  size = 5,
  customerName = "",
  status = "",
} = {}) => {
  const params = {
    page,
    size,
  };

  if (customerName.trim()) {
    params.customer = customerName.trim();
  }

  if (status) {
    params.status = status;
  }

  const response = await apiClient.get(
    "/api/orders",
    {
      params,
    }
  );

  return response.data;
};

export const getOrderById = async (id) => {
  const response = await apiClient.get(
    `/api/orders/${id}`
  );

  return response.data;
};

export const createOrder = async (
  orderData
) => {
  const response = await apiClient.post(
    "/api/orders",
    orderData
  );

  return response.data;
};

export const addCatalogItem = async (
  orderId,
  productPriceId
) => {
  const response = await apiClient.post(
    `/api/orders/${orderId}/catalog-items`,
    {
      productPriceId,
    }
  );

  return response.data;
};

export const updateOrder = async (
  orderId,
  orderData
) => {
  const response = await apiClient.put(
    `/api/orders/${orderId}`,
    orderData
  );

  return response.data;
};

export const updateOrderStatus = async (
  orderId,
  status
) => {
  const response = await apiClient.patch(
    `/api/orders/${orderId}/status`,
    {
      status,
    }
  );

  return response.data;
};

export const addManualItem = async (
  orderId,
  itemData
) => {
  const response = await apiClient.post(
    `/api/orders/${orderId}/manual-items`,
    itemData
  );

  return response.data;
};

export const deleteOrderItem = async (
  orderId,
  itemId
) => {
  await apiClient.delete(
    `/api/orders/${orderId}/items/${itemId}`
  );
};

export const getOrderMetrics = async () => {
  const [
    allOrders,
    draftOrders,
    confirmedOrders,
    preparingOrders,
    deliveredOrders,
    cancelledOrders,
  ] = await Promise.all([
    getOrders({
      page: 0,
      size: 1,
    }),

    getOrders({
      page: 0,
      size: 1,
      status: "DRAFT",
    }),

    getOrders({
      page: 0,
      size: 1,
      status: "CONFIRMED",
    }),

    getOrders({
      page: 0,
      size: 1,
      status: "PREPARING",
    }),

    getOrders({
      page: 0,
      size: 1,
      status: "DELIVERED",
    }),

    getOrders({
      page: 0,
      size: 1,
      status: "CANCELLED",
    }),
  ]);

  return {
    total: allOrders.totalElements,
    draft: draftOrders.totalElements,
    confirmed:
      confirmedOrders.totalElements,
    preparing:
      preparingOrders.totalElements,
    delivered:
      deliveredOrders.totalElements,
    cancelled:
      cancelledOrders.totalElements,
  };
};
