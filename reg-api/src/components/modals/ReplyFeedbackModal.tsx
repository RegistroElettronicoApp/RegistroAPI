import {Button, Form, InputGroup, Modal} from "react-bootstrap";
import {useState} from "react";
import {feedbackentry} from "../../../generated/prisma/client";

export default function ReplyFeedbackModal({feedback, show, handleClose, send}: {
  feedback: feedbackentry,
  show: boolean,
  handleClose: () => void,
  send: (desc: string) => void
}) {
  const [desc, setDesc] = useState("")

  return <Modal show={show} onHide={handleClose}>
    <Modal.Header closeButton>
      <Modal.Title>Rispondi a {feedback.name}</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      <InputGroup className="p-2">
        <Form.Control
          as={"textarea"}
          name={"desc"}
          aria-label="Descrizione"
          value={desc}
          onChange={(e) => setDesc(e.target.value)}
        />
      </InputGroup>
    </Modal.Body>
    <Modal.Footer>
      <Button variant="secondary" onClick={handleClose}>
        Chiudi
      </Button>
      <Button variant="primary" onClick={() => send(desc)}>
        Invia risposta
      </Button>
    </Modal.Footer>
  </Modal>
}