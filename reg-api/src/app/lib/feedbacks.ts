"use server"

import {prisma} from "../../../lib/prisma";
import {messaging} from "../../../lib/firebase"
import {AddFeedback} from "@/app/schemas/feedback/AddFeedbackPayload";
export async function getFeedbacks() {
  return prisma.feedbackentry.findMany({})
}

export async function replyFeedback(id: number, reply: string) {
  let newFeedback = await prisma.feedbackentry.update({
    where: {id: id},
    data: {reply: reply}
  })
  await messaging.send(
    {
      token: newFeedback.devicefcm,
      notification: {
        title: "Nuova risposta al tuo feedback",
        body: "Il tuo feedback ha ricevuto una risposta"
      },
      android: {
        notification: {
          title: "Nuova risposta al tuo feedback",
          body: "Il tuo feedback ha ricevuto una risposta"
        },
        priority: "high",
        data: {
          feedbackSecret: newFeedback?.secret
        }
      }
    }
  )
  return true
}

export async function addFeedback(feedbackPayload: AddFeedback) {
  return prisma.feedbackentry.create({
    data: {
      devicefcm: feedbackPayload.deviceFcm,
      name: feedbackPayload.name,
      description: feedbackPayload.description,
      reply: "",
      secret: crypto.randomUUID()
    }
  });
}