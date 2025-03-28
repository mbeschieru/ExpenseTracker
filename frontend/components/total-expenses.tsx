import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

type TotalExpensesProps = {
  amount: number | undefined | null;
};

export default function TotalExpenses({ amount }: TotalExpensesProps) {
  // Handle undefined or null amount
  const displayAmount = amount !== undefined && amount !== null ? amount : 0;

  return (
    <Card className="mb-6">
      <CardHeader className="pb-2">
        <CardTitle className="text-lg">Total Expenses</CardTitle>
        <CardDescription>Sum of all your tracked expenses</CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-3xl font-bold">${displayAmount.toFixed(2)}</p>
      </CardContent>
    </Card>
  );
}
