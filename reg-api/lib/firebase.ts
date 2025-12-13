// Here you are initializing your firebase
import admin, {ServiceAccount} from "firebase-admin";

import serviceAccount from "../config/firebase-service-acc.json";

const acc = serviceAccount as ServiceAccount;

admin.initializeApp({
  credential: admin.credential.cert(acc)
});

export let messaging = admin.messaging();

module.exports = admin;