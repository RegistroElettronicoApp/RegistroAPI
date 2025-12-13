"use server"

import {redirect} from "next/navigation";
import {createSession} from "@/app/lib/session";

const PASSWORD = process.env.PASSWORD

export async function login(username: string, password: string): Promise<Boolean> {
  if (password !== PASSWORD) {
    return false
  }
  await createSession(username)
  // 5. Redirect user
  //redirect('/#loggedIn')
  return true
}