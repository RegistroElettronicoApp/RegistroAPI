import connectInstances from "./notifications/index.js";
import startServer from "./communications/index.js";
import Database from "./db/index.js";

await Database.connectToDatabase()
await connectInstances()
console.log("Connected instances")
await startServer()
console.log("Started fastify")