import {Sequelize} from "sequelize-typescript";
import {config, dialect} from "../config/db.config.js";
import Instance from "../models/instance.model.js";


class Database {
    public sequelize: Sequelize | undefined;

    constructor() {
        this.connectToDatabase();
    }

    async connectToDatabase(): Promise<Boolean> {
        return new Promise((resolve, reject) => {
            this.sequelize = new Sequelize({
                host: config.HOST,
                username: config.USER,
                password: config.PASSWORD,
                database: config.DB,
                dialect: dialect,
                pool: {
                    max: config.pool.max,
                    min: config.pool.min,
                    acquire: config.pool.acquire,
                    idle: config.pool.idle
                },
                omitNull: true
            })

            this.sequelize.addModels([Instance])

            this.sequelize
                .authenticate()
                .then((s) => {
                    Instance.sync().then(() => {
                        resolve(true)
                        console.log("Connection has been established successfully.")
                    })
                })
                .catch((err) => {
                    resolve(false)
                    console.error("Unable to connect to the Database:", err);
                });
        })

    }
}

export default new Database();