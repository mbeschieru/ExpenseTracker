import { getSession } from "@/lib/session";
import { redirect } from "next/navigation";
import { getExpenses } from "@/lib/expenses";
import Navbar from "@/components/navbar";
import ExpenseList from "@/components/expense-list";
import TotalExpenses from "@/components/total-expenses";

export default async function DashboardPage({
  searchParams,
}: {
  searchParams: {
    page?: string;
    pageSize?: string;
    category?: string;
    startDate?: string;
    endDate?: string;
    minAmount?: string;
    maxAmount?: string;
  };
}) {
  const session = await getSession();

  if (!session) {
    redirect("/");
  }

  // Get page and pageSize from search params or use defaults
  const page = searchParams.page ? Number.parseInt(searchParams.page) : 0;
  const pageSize = searchParams.pageSize
    ? Number.parseInt(searchParams.pageSize)
    : 10;

  // Extract filter parameters
  const category = searchParams.category;
  const startDate = searchParams.startDate;
  const endDate = searchParams.endDate;
  const minAmount = searchParams.minAmount;
  const maxAmount = searchParams.maxAmount;

  // Get expenses with filters
  const {
    expenses,
    totalPages,
    currentPage,
    isFirstPage,
    isLastPage,
    totalAmount,
  } = await getExpenses({
    page,
    pageSize,
    category,
    startDate,
    endDate,
    minAmount,
    maxAmount,
  });

  console.log("Dashboard totalAmount:", totalAmount); // Add this for debugging

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
          filters={{
            category,
            startDate,
            endDate,
            minAmount,
            maxAmount,
          }}
        />
      </main>
    </div>
  );
}
