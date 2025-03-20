"use server";
import { type NextRequest, NextResponse } from "next/server";
import { createSession, getSession } from "@/lib/session";

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;

  const token = searchParams.get("token");

  const email = searchParams.get("email");

  if (!token || !email) {
    return NextResponse.redirect(
      new URL("/?error=invalid_oauth_response", request.url)
    );
  }

  await createSession({ token, email });

  const response = NextResponse.redirect(new URL("/dashboard", request.url), {
    status: 301,
  });

  return response;
}
