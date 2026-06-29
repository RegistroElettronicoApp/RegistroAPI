import * as z from "zod";

export const AddFeedbackPayload = z.object({
  deviceFcm: z.string()
    .describe("FCM del dispositivo a cui rimandare la notifica di lettura"),
  name: z.string()
    .nonempty({error: "Il nome non può essere vuoto!"})
    .describe("Nome dell'utente che ha inviato il feedback"),
  description: z.string()
    .nonempty({error: "La descrizione non può essere vuota!"})
    .describe("Descrizione del feedback")
});

export type AddFeedback = z.infer<typeof AddFeedbackPayload>