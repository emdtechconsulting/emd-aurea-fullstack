import { ProductPriceResponse } from './product-price-response';

export interface ProductResponse {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  description: string | null;
  active: boolean;
  prices: ProductPriceResponse[];
  createdAt: string;
  updatedAt: string;
}
