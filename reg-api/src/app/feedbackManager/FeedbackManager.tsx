"use client"

import NavBar from "@/components/topbar/NavBar";
import {logout} from "@/app/actions/logout";
import {feedbackentry} from "../../../generated/prisma/client";
import FeedbackCard from "@/components/feedbacks/FeedbackCard";
import {useState} from "react";
import ReplyFeedbackModal from "@/components/modals/ReplyFeedbackModal";
import {replyFeedback} from "@/app/lib/feedbacks";
import {useRouter} from "next/navigation";

export default function FeedbackManager({feedbacks, dev}: { feedbacks: feedbackentry[], dev: boolean }) {
  const router = useRouter();
  let [replyId, setReplyId] = useState<number | null>(null)
  return (
    <main>
      <NavBar dev={dev} logged={true} showLogin={() => {}} logout={logout}/>

      <div className="row">
        <div className="mx-auto col-md-8">
          <h1 className="text-center">Feedback:</h1>
          <div className="mx-auto col-md-2 text-center">
            {feedbacks.map(feedback => <div key={feedback.id}>
                <FeedbackCard feedback={feedback} reply={setReplyId}/>
                <ReplyFeedbackModal feedback={feedback} show={replyId == feedback.id}
                                    send={(reply) => {
                                      replyFeedback(feedback.id, reply).then(() => {
                                        setReplyId(null)
                                        router.refresh();
                                      })
                                    }}
                                    handleClose={() => setReplyId(null)}/>
              </div>
            )}

            {feedbacks.length == 0 &&
              <span className="text-center">Nessun feedback</span>
            }
          </div>
        </div>
      </div>
    </main>
  )
}
