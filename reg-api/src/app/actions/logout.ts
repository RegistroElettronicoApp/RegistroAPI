"use server"

import {redirect} from "next/navigation";
import {createSession, deleteSession} from "@/app/lib/session";

export async function logout() {
  await deleteSession()
  // 5. Redirect user
  redirect('/#loggedOut')
}