import {Column, DataType, Model, Table} from "sequelize-typescript";
import {PushReceiver} from "@eneris/push-receiver";

@Table({
    tableName: "instances"
})
export default class Instance extends Model {
    @Column({
        type: DataType.INTEGER,
        primaryKey: true,
        field: "id", allowNull: false, autoIncrement: true
    })
    pk?: number

    @Column({
        type: DataType.INTEGER,
        field: "regId", allowNull: false
    })
    regId?: number

    @Column({
        type: DataType.STRING,
        field: "username", allowNull: false
    })
    username?: string

    @Column({
        type: DataType.ARRAY(DataType.STRING),
        field: "noti_ids", allowNull: false
    })
    noti_ids?: Array<string>

    @Column({
        type: DataType.BOOLEAN,
        field: "debug", allowNull: false
    })
    debug?: boolean

    @Column({
        type: DataType.STRING(4096),
        field: "credentials",
        allowNull: false
    })
    credentials?: string

    @Column({
        type: DataType.STRING,
        field: "phoneFcm", allowNull: false
    })
    phoneFcm?: string
}

export interface InstanceMap {
    [key: string]: PushReceiver;
}
