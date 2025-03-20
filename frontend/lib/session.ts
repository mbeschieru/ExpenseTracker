"use server";

import { cookies } from "next/headers";
import type { SessionType, LoginResponse } from "./types";
import { jwtDecode } from "jwt-decode";

interface JwtPayload {
  exp: number;
}

export async function createSession(loginResponse: {
  token: string;
  email: string;
}) {
  try {
    const decoded = jwtDecode<{ exp: number }>(loginResponse.token);

    const session = {
      token: loginResponse.token,
      email: loginResponse.email,
      expires: new Date(decoded.exp * 1000),
    };

    cookies().set("session", JSON.stringify(session), {
      httpOnly: true,
      expires: session.expires,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
    });

    return session;
  } catch (error) {
    console.error("Error creating session:", error);
    throw new Error("Failed to create session");
  }
}

export async function getSession() {
  try {
    const cookie = cookies().get("session");
    if (!cookie) return null;

    const session = JSON.parse(cookie.value) as {
      token: string;
      email: string;
      expires: string;
    };

    if (new Date(session.expires) < new Date()) {
      deleteSession();
      return null;
    }

    return session;
  } catch (error) {
    console.error("Error parsing session:", error);
    return null;
  }
}

export async function deleteSession() {
  cookies().delete("session");
}
