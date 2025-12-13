"use server"

import {Suspense} from "react";
import {getSession} from "@/app/lib/session";
import Homepage from "@/app/Homepage";
import isDev from "@/app/lib/dev";

export default async function Page() {
  let session = await getSession()
  let dev = await isDev()
  return <Suspense fallback={<div>Loading...</div>}>
    <Homepage session={session} dev={dev}/>
  </Suspense>
}
