"use client";

import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Pencil, Trash2, ChevronLeft, ChevronRight } from "lucide-react";
import { format } from "date-fns";
import { deleteExpense } from "@/lib/actions";
import UpdateExpenseModal from "./update-expense-modal";
import type { ExpenseType } from "@/lib/types";

type ExpenseListProps = {
  expenses: ExpenseType[];
  currentPage: number;
  totalPages: number;
  pageSize: number;
  isFirstPage: boolean;
  isLastPage: boolean;
};

export default function ExpenseList({
  expenses,
  currentPage,
  totalPages,
  pageSize,
  isFirstPage,
  isLastPage,
}: ExpenseListProps) {
  const router = useRouter();
  const pathname = usePathname();

  const [updateModalOpen, setUpdateModalOpen] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState<ExpenseType | null>(
    null
  );

  const handleUpdateClick = (expense: ExpenseType) => {
    setSelectedExpense(expense);
    setUpdateModalOpen(true);
  };

  const handleDeleteClick = async (id: string) => {
    if (confirm("Are you sure you want to delete this expense?")) {
      await deleteExpense(id);
      router.refresh();
    }
  };

  const handlePageChange = (page: number) => {
    const params = new URLSearchParams();
    params.set("page", page.toString());

    // Preserve the pageSize if it exists
    if (pageSize !== 10) {
      params.set("pageSize", pageSize.toString());
    }

    router.push(`${pathname}?${params.toString()}`);
  };

  const getCategoryColor = (category: string) => {
    switch (category) {
      case "Bills":
        return "bg-green-100 text-green-800";
      case "Health":
        return "bg-pink-100 text-pink-800";
      case "Entertainment":
        return "bg-purple-100 text-purple-800";
      case "Groceries":
        return "bg-red-100 text-red-800";
      case "Other":
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="space-y-4">
      {expenses.length === 0 ? (
        <Card>
          <CardContent className="p-6 text-center">
            <p className="text-muted-foreground">
              No expenses found. Create a new expense to get started.
            </p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {expenses.map((expense) => (
            <Card key={expense.expenseId} className="overflow-hidden">
              <CardContent className="p-0">
                <div className="p-4">
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-medium truncate">{expense.name}</h3>
                    <Badge
                      className={getCategoryColor(expense.category)}
                      variant="outline"
                    >
                      {expense.category}
                    </Badge>
                  </div>
                  <p className="text-sm text-muted-foreground mb-2 line-clamp-2">
                    {expense.description}
                  </p>
                  <div className="flex justify-between items-center">
                    <p className="text-lg font-semibold">
                      ${expense.amount.toFixed(2)}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {format(new Date(expense.date), "MMM d, yyyy")}
                    </p>
                  </div>
                </div>
                <div className="flex border-t">
                  <Button
                    variant="ghost"
                    className="flex-1 rounded-none h-10"
                    onClick={() => handleUpdateClick(expense)}
                  >
                    <Pencil className="h-4 w-4 mr-2" />
                    Edit
                  </Button>
                  <div className="w-px bg-border" />
                  <Button
                    variant="ghost"
                    className="flex-1 rounded-none h-10 text-destructive hover:text-destructive"
                    onClick={() => handleDeleteClick(expense.expenseId)}
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={isFirstPage}
          >
            <ChevronLeft className="h-4 w-4" />
            Previous
          </Button>
          <div className="flex items-center gap-1">
            {Array.from({ length: totalPages }, (_, i) => i).map((page) => (
              <Button
                key={page}
                variant={page === currentPage ? "default" : "outline"}
                size="sm"
                className="w-8 h-8 p-0"
                onClick={() => handlePageChange(page)}
              >
                {page + 1} {/* Display 1-based page numbers for users */}
              </Button>
            ))}
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={isLastPage}
          >
            Next
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      )}

      {selectedExpense && (
        <UpdateExpenseModal
          isOpen={updateModalOpen}
          onClose={() => setUpdateModalOpen(false)}
          expense={selectedExpense}
        />
      )}
    </div>
  );
}
