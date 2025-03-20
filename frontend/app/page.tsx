import { redirect } from "next/navigation";
import { getSession } from "@/lib/session";
import LoginForm from "@/components/login-form";

export default async function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-6">Expense Tracker</h1>
        <LoginForm />
      </div>
    </main>
  );
}
