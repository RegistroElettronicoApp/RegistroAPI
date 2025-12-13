import {Alert, Button, Form, InputGroup, Modal} from "react-bootstrap";
import {useState} from "react";

export default function LoginModal({error, show, triedSkip, handleClose, onLogin}: {
  error: boolean,
  triedSkip: boolean,
  show: boolean,
  handleClose: () => void, onLogin: (username: string, password: string) => void
}) {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  return <Modal show={show} onHide={handleClose}>
    <Modal.Header closeButton>
      <Modal.Title>Login</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      {error &&
        <Alert variant="error">
          Dati non corretti
        </Alert>
      }
      <Alert variant={"danger"} show={triedSkip}>
        Per questa azione è necessario il login!
      </Alert>
      <InputGroup className="p-2">
        <Form.Control
          placeholder="Username"
          name={"username"}
          aria-label="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </InputGroup>
      <InputGroup className="p-2">
        <Form.Control
          placeholder="Password"
          aria-label="Password"
          name={"password"}
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </InputGroup>
    </Modal.Body>
    <Modal.Footer>
      <Button variant="primary" onClick={() => onLogin(username, password)}>
        Login
      </Button>
    </Modal.Footer>
  </Modal>
}