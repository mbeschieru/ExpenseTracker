"use server";
import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache";
import type { ExpenseInput, ExpenseUpdateInput, LoginResponse } from "./types";
import { createSession, deleteSession, getSession } from "./session";
import axiosClient from "./axios-client";

export async function login(email: string, password: string) {
  try {
    const response = await axiosClient.post<LoginResponse>("/auth/login", {
      email,
      password,
    });

    // Create a session with the JWT token
    await createSession(response.data);

    return { success: true };
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (error: any) {
    console.error("Login error:", error);
    return {
      success: false,
      error: error.response?.data?.message || "Invalid email or password",
    };
  }
}

export async function logout() {
  try {
    // Delete the session cookie first to ensure local logout happens
    await deleteSession();

    // Call the logout endpoint if your API has one
    // We do this after local logout so even if API logout fails, user is still logged out locally
    const session = await getSession();
    if (session?.token) {
      try {
        await axiosClient.post("/auth/logout");
      } catch (error) {
        console.error("Error during API logout:", error);
        // Continue with redirect even if API logout fails
      }
    }

    // Redirect to login page
    redirect("/");
  } catch (error) {
    console.error("Logout error:", error);
    // Instead of throwing an error, just redirect to login page
    redirect("/");
  }
}

export async function createExpense(data: ExpenseInput) {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    const response = await axiosClient.post("/expenses", data);

    revalidatePath("/dashboard");
    return response.data;
  } catch (error) {
    console.error("Create expense error:", error);
    throw new Error("Failed to create expense");
  }
}

export async function updateExpense(data: ExpenseUpdateInput) {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    const response = await axiosClient.put(`/expenses/${data.expenseId}`, data);

    revalidatePath("/dashboard");
    return response.data;
  } catch (error) {
    console.error("Update expense error:", error);
    throw new Error("Failed to update expense");
  }
}

export async function deleteExpense(id: string) {
  try {
    const session = await getSession();

    if (!session) {
      throw new Error("Unauthorized");
    }

    await axiosClient.delete(`/expenses/${id}`);

    revalidatePath("/dashboard");
    return { success: true };
  } catch (error) {
    console.error("Delete expense error:", error);
    throw new Error("Failed to delete expense");
  }
}
