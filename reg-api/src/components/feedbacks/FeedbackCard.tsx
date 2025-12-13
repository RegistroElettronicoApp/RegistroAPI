import {feedbackentry} from "../../../generated/prisma/client";

export default function FeedbackCard({feedback, reply}: {feedback: feedbackentry, reply: (id: number) => void}) {
  return <div className="card justify-content-center w-100 mb-10">
    <div className="card-body">
      <h5 className="card-title">{feedback.name}</h5>
      <h6 className="card-subtitle mb-2 text-body-secondary">ID: {feedback.id}</h6>
      <h6 className="card-subtitle mb-2 text-body-secondary card-date-to-edit"
          data-date={feedback.date}>Data: {feedback.date.toLocaleDateString()}</h6>

      {feedback.reply != "" &&
        <h6 className="card-subtitle mb-2 text-body-secondary">Risposta: {feedback.reply}</h6>
      }

      <p className="card-text">{feedback.description}</p>
      {feedback.reply == "" &&
        <a className="btn btn-primary" onClick={() => reply(feedback.id)}>Rispondi</a>
      }
      {feedback.reply != "" &&
        <a href="#" className="btn btn-primary disabled" aria-disabled="true">Risposto</a>
      }
    </div>
  </div>
}