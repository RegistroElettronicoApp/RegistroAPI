import {PushReceiver} from '@eneris/push-receiver'
import {default as regInstanceJson} from '../../config/credentials/regInstance.json' with {type: 'json'};
import {
    default as cvvStudentiInstanceJson
} from '../../config/credentials/cvvStudentiInstance.json' with {type: 'json'};
import InstanceRepository from "../repositories/instance.repository.js";
import {Credentials} from "@eneris/push-receiver/dist/types";

let globalInstances: Map<number, [string, PushReceiver]> = new Map()

function connectInstance(instance: [string, PushReceiver] | undefined, pk: number, newFcmCallback: (fcm: string) => void) {
    if (instance != undefined) {
        instance[1].onNotification(notification => {
            // Do someting with the notification
            console.log('Notification received', notification)
        })
        instance[1].onCredentialsChanged(notification => {
            InstanceRepository.retrieveById(pk).then((instance) => {
                instance?.update({
                    credentials: JSON.stringify(notification.newCredentials)
                })
                newFcmCallback(notification.newCredentials.fcm.token)
            })
        })
    }

}

function addInstance(pk: number, phoneFcm: string, regId: number | undefined, persistentIds: Array<string> | undefined, credentials: Credentials | undefined, debug: boolean | undefined) {
    return globalInstances.set(pk, [phoneFcm, new PushReceiver({
        firebase: (() => {
            switch (regId) {
                case 0:
                    return cvvStudentiInstanceJson;
                case 77:
                    return regInstanceJson;
                default:
                    return cvvStudentiInstanceJson;
            }
        })(),
        persistentIds: persistentIds,
        credentials: credentials,
        debug: debug
    })]).get(pk)
}

export default async function connectInstances() {
    let instances = await InstanceRepository.retrieveAll()

    instances.map((instance) => {
        if (instance.phoneFcm != undefined && instance.pk != undefined) {
            addInstance(instance.pk, instance.phoneFcm, instance.regId, instance.noti_ids, instance.credentials != null ? JSON.parse(instance.credentials) : {}, instance.debug)
        }
    })

    globalInstances.forEach((instance, pk) => {
        connectInstance(instance, pk, () => {
        })
    })
    console.log('connected instances')
}

export async function getFcm(phoneFcm: string, regId: number, username: string): Promise<[number | undefined, string | undefined]> {
    return new Promise(async (resolve, reject) => {
        if (phoneFcm == undefined) resolve([undefined, undefined])
        let alreadyPresent: [number, string] | undefined = undefined
        globalInstances.forEach((value, key) => {
            if (phoneFcm == value[0]) alreadyPresent = [key, value[0]]
        })
        if (alreadyPresent != undefined) resolve([alreadyPresent[0], alreadyPresent[1]])
        if (regId == undefined) resolve([undefined, undefined])
        let newInstanceDb = await InstanceRepository.save(regId, username, [], true, "{}", phoneFcm)
        if (newInstanceDb.pk != undefined) {
            let newInstance = addInstance(newInstanceDb.pk, phoneFcm, regId, [], undefined, true)
            connectInstance(newInstance, newInstanceDb.pk, (fcm) => {
                resolve([newInstanceDb.pk, fcm])
            })
        }
    })

}

