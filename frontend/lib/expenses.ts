"use server";
import { getSession } from "./session";
import axiosClient from "./axios-client";

export async function getExpenses({
  page = 0,
  pageSize = 10,
  category,
  startDate,
  endDate,
  minAmount,
  maxAmount,
}: {
  page?: number;
  pageSize?: number;
  category?: string;
  startDate?: string;
  endDate?: string;
  minAmount?: string;
  maxAmount?: string;
}) {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    // Build query parameters
    const params: Record<string, string> = {
      page: page.toString(),
      pageSize: pageSize.toString(),
    };

    // Only add filter parameters if they are defined and not empty
    if (category && category !== "all") {
      params.category = category;
    }

    if (startDate) {
      params.startDate = startDate;
    }

    if (endDate) {
      params.endDate = endDate;
    }

    if (minAmount) {
      params.minAmount = minAmount;
    }

    if (maxAmount) {
      params.maxAmount = maxAmount;
    }

    console.log("API request params:", params); // Log the params for debugging

    // Make the API request
    const response = await axiosClient.get<any>("/expenses", { params });

    console.log("API response:", response.data); // Log the response for debugging

    // Extract the data from the response
    const totalAmount =
      response.data.totalAmount !== undefined ? response.data.totalAmount : 0;

    return {
      expenses: response.data.content || [],
      totalPages: response.data.totalPages || 0,
      totalExpenses: response.data.total || 0,
      currentPage: response.data.page || 0,
      pageSize: response.data.pageSize || pageSize,
      isLastPage:
        (response.data.page || 0) >= (response.data.totalPages - 1 || 0),
      isFirstPage: (response.data.page || 0) === 0,
      totalAmount: totalAmount,
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
      totalAmount: 0,
    };
  }
}
