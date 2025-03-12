"use server";

import type { PaginatedResponse, ExpenseType } from "./types";
import { getSession } from "./session";
import axiosClient from "./axios-client";

export async function getExpenses({
  page = 0,
  pageSize = 10,
}: {
  page?: number;
  pageSize?: number;
}) {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    // Build query parameters - only pagination
    const params: Record<string, string> = {
      page: page.toString(),
      pageSize: pageSize.toString(),
    };

    // Make the API request
    const response = await axiosClient.get<PaginatedResponse<ExpenseType>>(
      "/expenses",
      { params }
    );

    // Extract the data from the Spring Boot Page response
    return {
      expenses: response.data.content,
      totalPages: response.data.totalPages,
      totalExpenses: response.data.totalElements,
      currentPage: response.data.number, // This is 0-based
      pageSize: response.data.size,
      isLastPage: response.data.last,
      isFirstPage: response.data.first,
    };
  } catch (error) {
    console.error("Get expenses error:", error);
    // Return empty data on error
    return {
      expenses: [],
      totalPages: 0,
      totalExpenses: 0,
      currentPage: 0,
      pageSize: pageSize,
      isLastPage: true,
      isFirstPage: true,
    };
  }
}

export async function getTotalAmount() {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    // Make the API request - no filters
    const response = await axiosClient.get<number>("/expenses/total");

    // If the API returns just a number directly
    return response.data;
  } catch (error) {
    console.error("Get total amount error:", error);
    return 0;
  }
}
