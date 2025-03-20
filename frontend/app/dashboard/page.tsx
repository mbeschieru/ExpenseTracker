import { getSession } from "@/lib/session";
import { redirect } from "next/navigation";
import { getExpenses, getTotalAmount } from "@/lib/expenses";
import Navbar from "@/components/navbar";
import ExpenseList from "@/components/expense-list";
import TotalExpenses from "@/components/total-expenses";
import { cookies } from "next/headers";

export default async function DashboardPage({
  searchParams,
}: {
  searchParams: {
    page?: string;
    pageSize?: string;
  };
}) {
  const searchParam = await searchParams;
  // Get page and pageSize from search params or use defaults
  const page = searchParam.page ? parseInt(searchParam.page) : 0;
  const pageSize = searchParam.pageSize ? parseInt(searchParam.pageSize) : 10;

  // Get expenses with pagination only
  const { expenses, totalPages, currentPage, isFirstPage, isLastPage } =
    await getExpenses({
      page,
      pageSize,
    });

  // Get total amount without filters
  const totalAmount = await getTotalAmount();

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 container mx-auto p-4 md:p-6">
        <h1 className="text-2xl font-bold mb-6">Your Expenses</h1>
        <TotalExpenses amount={totalAmount} />
        <ExpenseList
          expenses={expenses}
          currentPage={currentPage}
          totalPages={totalPages}
          pageSize={pageSize}
          isFirstPage={isFirstPage}
          isLastPage={isLastPage}
        />
      </main>
    </div>
  );
}
