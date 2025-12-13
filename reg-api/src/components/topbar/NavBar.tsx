import NavItem from "@/components/topbar/NavItem";
import {Container, Nav, Navbar, NavDropdown} from "react-bootstrap";

export default function NavBar({dev, logged, showLogin, logout}: {dev: boolean, logged: boolean, showLogin: () => void, logout: () => void}) {
  let items: UiPage[] = [
    {title: "Home", href: "/", disabled: false},
    {title: "Feedback", href: "/feedbackManager", disabled: false},
    {title: "Notifiche", href: "/notifications", disabled: true},
    {title: "Chiavi di accesso", href: "/accessKeysManager", disabled: false},
    {title: "Changelog", href: "/changelogManager", disabled: false},
  ]

  return <Navbar expand="lg" className="bg-body-tertiary">
    <Container className={"m-0 w-100 mw-100"}>
      <Navbar.Brand href="#">
        <img src="/regicon.png" alt="Logo" width="28" className="d-inline-block align-text-top" />
        Registro admin page {dev ? "dev" : ""}
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="me-auto">
          {items.map((item, index) => <Nav.Link href={item.href} disabled={item.disabled} key={index}>{item.title}</Nav.Link> )}
        </Nav>
      </Navbar.Collapse>
      <div id="navbarLogin">
        {!logged &&
          <button className="btn btn-outline-success" type="submit" data-bs-toggle="modal" onClick={() => showLogin()}>Login
          </button>
        }
        {logged &&
          <input type="submit" className="btn btn-danger" value="Esci" id="logoutButton" name="button" onClick={() => logout()}/>
        }
      </div>
    </Container>
  </Navbar>
}