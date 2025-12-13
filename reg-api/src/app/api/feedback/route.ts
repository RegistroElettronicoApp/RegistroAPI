import * as z from "zod";
import {AddFeedbackPayload} from "@/app/schemas/feedback/AddFeedbackPayload";
import {addFeedback} from "@/app/lib/feedbacks";
import isDev from "@/app/lib/dev";
import {NextResponse} from "next/server";
import {StatusCodes} from "http-status-codes";

const TG_BOT = process.env.TG_BOT;
const TG_GROUP = process.env.TG_GROUP;

export async function PUT(request: Request) {
  try {
    const body = await request.json();
    const data = AddFeedbackPayload.parse(body);
    let dev = await isDev()

    let newFeedback = await addFeedback(data)

    let formData = new FormData();
    formData.append("chat_id", TG_GROUP || "")
    formData.append("text", `
    <b>Nuovo feedback!</b>
    <b>Da:</b> ${newFeedback.name}
    <b>Messaggio:</b> ${newFeedback.description}
    <b>ID:</b> ${newFeedback.id}

    https://regapi${dev ? "-dev" : ""}.chrif.me
    `)
    formData.append("parse_mode", "HTML")
    await fetch("https://api.telegram.org/bot" + TG_BOT + "/sendMessage", {
      body: formData,
      method: "POST"
    })

    return NextResponse.json(
      { message: 'Feedback creato', ...data },
      { status: StatusCodes.CREATED }
    );
  } catch (error) {
    if (error instanceof z.ZodError) {
      // Return validation errors
      return NextResponse.json(
        { message: 'Validation error', error: error },
        { status: 400 }
      );
    }
  }

}