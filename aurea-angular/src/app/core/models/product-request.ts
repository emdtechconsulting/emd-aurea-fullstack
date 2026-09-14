import { ProductPriceRequest } from './product-price-request';

export interface ProductRequest {
  categoryId: number;
  name: string;
  description: string | null;
  active: boolean;
  prices: ProductPriceRequest[];
}
