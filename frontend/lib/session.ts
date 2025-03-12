"use server"

import { cookies } from "next/headers"
import type { SessionType, LoginResponse } from "./types"
import { jwtDecode } from "jwt-decode" // Changed from import jwt_decode from "jwt-decode"

type JwtPayload = {
  sub: string
  exp: number
  email: string
}

// Update the createSession function to await cookies()
export async function createSession(loginResponse: LoginResponse) {
  try {
    // Decode the JWT to get the expiration time
    const decoded = jwtDecode<JwtPayload>(loginResponse.token)

    // Create a session object
    const session: SessionType = {
      token: loginResponse.token,
      email: loginResponse.email,
      expires: new Date(decoded.exp * 1000), // Convert from seconds to milliseconds
    }

    // Store the session in a cookie
    const cookieStore = await cookies()
    cookieStore.set("session", JSON.stringify(session), {
      httpOnly: true,
      expires: new Date(decoded.exp * 1000),
      path: "/",
      secure: process.env.NODE_ENV === "production",
      sameSite: "strict",
    })

    return session
  } catch (error) {
    console.error("Error creating session:", error)
    throw new Error("Failed to create session")
  }
}

// Update the getSession function to await cookies()
export async function getSession(): Promise<SessionType | null> {
  const cookieStore = await cookies()
  const sessionCookie = cookieStore.get("session")

  if (!sessionCookie?.value) {
    return null
  }

  try {
    const session: SessionType = JSON.parse(sessionCookie.value)

    // Check if the session is expired
    if (new Date(session.expires) < new Date()) {
      await deleteSession()
      return null
    }

    return session
  } catch (error) {
    console.error("Error parsing session:", error)
    return null
  }
}

// Update the deleteSession function to await cookies()
export async function deleteSession() {
  const cookieStore = await cookies()
  cookieStore.delete("session")
}

