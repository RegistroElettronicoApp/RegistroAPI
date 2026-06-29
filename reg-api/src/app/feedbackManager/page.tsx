"use server"

import {Suspense} from "react";
import isDev from "@/app/lib/dev";
import FeedbackManager from "@/app/feedbackManager/FeedbackManager";
import {getFeedbacks} from "@/app/lib/feedbacks";

export default async function Page() {
  let feedbacks = await getFeedbacks()
  let dev = await isDev()
  return <Suspense fallback={<div>Loading...</div>}>
    <FeedbackManager feedbacks={feedbacks} dev={dev}/>
  </Suspense>
}
