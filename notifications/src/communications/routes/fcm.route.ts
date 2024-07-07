import {FastifyReply, FastifyRequest} from "fastify";
import {getFcm} from "../../notifications/index.js";
import {Endpoint} from "../../models/route.js";

const ENDPOINT: Endpoint = {
    endpoint: "/getfcm",
    method: "post",
    handler: async (request: FastifyRequest<{
        Body: {
            regId?: number,
            phoneFcm?: string,
            username?: string,
        }
    }>, reply: FastifyReply) => {
        let regId = request.body.regId
        let username = request.body.username
        let phoneFcm = request.body.phoneFcm
        if (regId == undefined || username == undefined || phoneFcm == undefined) return {fcm: undefined, pk: undefined}
        let fcm = await getFcm(phoneFcm, regId, username)
        return {fcm: fcm[1], pk: fcm[0]}
    }
}

export default ENDPOINT