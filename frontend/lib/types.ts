export type ExpenseType = {
  expenseId: string;
  name: string;
  description: string;
  category: string;
  date: string;
  amount: number;
  createdAt: string;
  updatedAt: string;
};

export type ExpenseInput = {
  name: string;
  description: string;
  category: string;
  date: string;
  amount: number;
};

export type RegisterResponse = {
  token: string;
  email: string;
};

export type ExpenseUpdateInput = ExpenseInput & {
  expenseId: string;
};

export type LoginResponse = {
  token: string;
  email: string;
};

export type SessionType = {
  token: string;
  email: string;
  expires: Date;
};

export type ApiResponse<T> = {
  data: T;
  message: string;
  status: number;
};

// Spring Boot Page response structure
export type PaginatedResponse<T> = {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    offset: number;
    unpaged: boolean;
    paged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number; // Current page (0-based)
  numberOfElements: number;
  empty: boolean;
};
