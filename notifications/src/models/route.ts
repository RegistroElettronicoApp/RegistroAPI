import {FastifyReply, FastifyRequest, HTTPMethods} from "fastify";

export type Endpoint = {
    method: HTTPMethods,
    endpoint: string
    handler: (request: FastifyRequest<{ Body: any | undefined }>, reply: FastifyReply) => Promise<{
        fcm: undefined,
        pk: undefined
    } | { fcm: string | undefined, pk: number | undefined }>
}