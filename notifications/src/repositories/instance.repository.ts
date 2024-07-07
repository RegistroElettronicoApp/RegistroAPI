import Instance from "../models/instance.model.js";
import {Op, WhereOptions} from "sequelize";

interface IInstanceRepository {
    save(regId: number,
         username: string,
         noti_ids: Array<string>,
         debug: boolean,
         credentials: string,
         phoneFcm: string): Promise<Instance>;

    retrieveAll(searchParams: { regId: number, username: string, phoneFcm: string }): Promise<Instance[]>;

    retrieveById(pk: number): Promise<Instance | null>;

    update(instance: Instance): Promise<number>;

    delete(pk: number): Promise<number>;

    deleteAll(): Promise<number>;
}

class InstanceRepository implements IInstanceRepository {
    async save(regId: number,
               username: string,
               noti_ids: Array<string>,
               debug: boolean,
               credentials: string,
               phoneFcm: string): Promise<Instance> {
        try {
            return await Instance.create({
                regId: regId,
                username: username,
                noti_ids: noti_ids,
                debug: debug,
                credentials: credentials,
                phoneFcm: phoneFcm
            });
        } catch (err) {
            throw err
        }
    }

    async retrieveAll(searchParams?: { regId: number, username: string, phoneFcm: string, }): Promise<Instance[]> {
        try {
            let condition: WhereOptions = {};

            if (searchParams?.regId) condition.regId = searchParams.regId;

            if (searchParams?.username)
                condition.username = {[Op.like]: `%${searchParams.username}%`};

            if (searchParams?.phoneFcm)
                condition.phoneFcm = {[Op.like]: `%${searchParams.phoneFcm}%`};

            return await Instance.findAll({where: condition});
        } catch (error) {
            throw /*new Error("Failed to retrieve Instances!");*/ error
        }
    }

    async retrieveById(pk: number): Promise<Instance | null> {
        try {
            return await Instance.findByPk(pk);
        } catch (error) {
            throw new Error("Failed to retrieve Instances!");
        }
    }

    async update(instance: Instance): Promise<number> {
        const {
            pk,
            regId,
            username,
            noti_ids,
            debug,
            credentials,
            phoneFcm
        } = instance;

        try {
            const affectedRows = await Instance.update(
                {regId, username, noti_ids, debug, credentials, phoneFcm},
                {where: {pk: pk}}
            );

            return affectedRows[0];
        } catch (error) {
            throw new Error("Failed to update Instance!");
        }
    }

    async delete(pk: number): Promise<number> {
        try {
            return await Instance.destroy({where: {pk: pk}});
        } catch (error) {
            throw new Error("Failed to delete Instance!");
        }
    }

    async deleteAll(): Promise<number> {
        try {
            return Instance.destroy({
                where: {},
                truncate: false
            });
        } catch (error) {
            throw new Error("Failed to delete Instances!");
        }
    }
}

export default new InstanceRepository();
