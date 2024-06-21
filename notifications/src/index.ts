import {PushReceiver} from '@eneris/push-receiver'
import regInstanceJson from '../config/credentials/regInstance.json' with {type: 'json'};
import cvvStudentiInstanceJson from '../config/credentials/cvvStudentiInstance.json' with {type: 'json'};
// for testing, the school year has finished, and we can't receive school notifications
const regInstance = new PushReceiver(regInstanceJson)

const cvvStudentiInstance = new PushReceiver(cvvStudentiInstanceJson)

const stopListeningToCredentials = cvvStudentiInstance.onCredentialsChanged(({oldCredentials, newCredentials}) => {
    console.log('Client generated new credentials.', newCredentials)
    // Save them somewhere! And decide if thing are needed to re-subscribe
})

const stopListeningToNotifications = cvvStudentiInstance.onNotification(notification => {
    // Do someting with the notification
    console.log('Notification received', notification)
})

await regInstance.connect()
await cvvStudentiInstance.connect()

console.log('connected')

console.log('server created')

stopListeningToCredentials()
stopListeningToNotifications()
