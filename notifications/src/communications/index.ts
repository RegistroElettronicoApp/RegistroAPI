import Fastify from 'fastify'
import * as fs from "node:fs";
import {Endpoint} from "../models/route.js";

export const fastify = Fastify({
    logger: true
})

export default async function startServer() {
    // Run the server!
    const dir = fs.readdirSync("./dist/communications/routes").filter(file => file.endsWith(".js"))
    for (const file of dir) {
        const importedFile: Endpoint = (await import(`./routes/${file}`)).default //  + un punto a favore se fai un type, basta che typi gli exports
        fastify.route({
            method: importedFile.method,
            url: importedFile.endpoint,
            handler: importedFile.handler
        })
        console.log(`Loaded endpoint ${file}`)
    }

    try {
        await fastify.listen({port: 3737, host: "0.0.0.0"})
    } catch (err) {
        fastify.log.error(err)
    }
}

