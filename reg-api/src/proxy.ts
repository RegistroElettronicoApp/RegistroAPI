import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'
import {getSession} from "@/app/lib/session";

// This function can be marked `async` if using `await` inside
export async function proxy(request: NextRequest) {
  let session = await getSession()

  if (session?.userName == null) {
    return NextResponse.redirect(new URL('/#loginNec', request.url))
  } else {
    return NextResponse.next()
  }
}

// Alternatively, you can use a default export:
// export default function proxy(request: NextRequest) { ... }

// See "Matching Paths" below to learn more
export const config = {
  matcher: ['/((?!api|_next/static|_next/image|.*\\.png$).+)'],
}