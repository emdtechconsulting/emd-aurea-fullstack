import { OrderSource } from './order-source';
import { OrderStatus } from './order-status';

export interface OrderFilters {
  status?: OrderStatus;
  source?: OrderSource;
  customer?: string;
  page?: number;
  size?: number;
}