import { OrderSource } from './order-source';
import { OrderStatus } from './order-status';

export interface OrderResponse {
  id: number;
  customerName: string;
  deliveryDate: string | null;
  deliveryTime: string | null;
  requiresDelivery: boolean;
  district: string | null;
  deliveryFee: number;
  productsSubtotal: number;
  total: number;
  status: OrderStatus;
  source: OrderSource;
}