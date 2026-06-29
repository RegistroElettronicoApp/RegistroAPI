"use client"

import Image from "next/image";
import LoginModal from "@/components/modals/LoginModal";
import {useEffect, useState} from "react";
import {login} from "@/app/actions/login";
import NavBar from "@/components/topbar/NavBar";
import {logout} from "@/app/actions/logout";
import SessionPayload from "@/types/SessionPayload";

export default function Homepage({session, dev}: { session: SessionPayload | undefined, dev: boolean }) {
  let logged = session?.userName != undefined
  let [loggedOut, setLoggedOut] = useState(false)
  let name = session?.userName
  let [error, setError] = useState(false)
  let [showLoginModal, setShowLoginModal] = useState(false)
  let [triedSkip, setTriedSkip] = useState(false)
  useEffect(() => {
    if (location.hash === "#loginNec") {
      setShowLoginModal(true)
      setTriedSkip(true)
    }
    if (location.hash === "#loggedOut") {
      setLoggedOut(true)
    }
  }, [])



  return (
    <main>
      <NavBar dev={dev} logged={logged} showLogin={() => setShowLoginModal(true)} logout={logout} />

      <LoginModal error={error} show={showLoginModal} triedSkip={triedSkip} handleClose={() => {
        setShowLoginModal(false)
      }} onLogin={(username, password) => {
        login(username, password).then(res => {
          console.log(res)
          if (res === false) setError(res === false)
          else {
            setShowLoginModal(false)
          }
        })
      }}/>
      {loggedOut && <div className="alert alert-success" role="alert">
        Disconnesso con successo!
      </div>}
      {logged && <div className="row">
        <div className="mx-auto col-md-8">
          <h1 className="text-center">Ciao {name}!</h1>
        </div>
      </div>}
      {!logged && <div className="row">
        <div className="mx-auto col-md-8">
          <h1 className="text-center">Devi prima fare il login.</h1>
        </div>
      </div>}
    </main>
  )
}
