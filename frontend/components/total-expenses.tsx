import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

type TotalExpensesProps = {
  amount: number
}

export default function TotalExpenses({ amount }: TotalExpensesProps) {
  return (
    <Card className="mb-6">
      <CardHeader className="pb-2">
        <CardTitle className="text-lg">Total Expenses</CardTitle>
        <CardDescription>Sum of all your tracked expenses</CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-3xl font-bold">${amount.toFixed(2)}</p>
      </CardContent>
    </Card>
  )
}

