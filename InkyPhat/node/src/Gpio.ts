import * as gpio from 'rpi-gpio'
import { Pin } from './Pin'

export class Gpio {

    constructor() {
        gpio.setMode(gpio.MODE_RPI)
    }

    write = (pin: Pin, value: boolean): Promise<any> => {
        return new Promise((resolve, reject) => {
            console.log('write', pin.value)
            gpio.write(pin.value, value, (err) => {
                err ? reject(err) : resolve()
            })
        })
    }

    read = (pin: Pin): Promise<boolean> => {
        return new Promise((resolve, reject) => {
            console.log('read', pin.value)
            gpio.read(pin.value, (err, result) => {
                err ? reject(err) : resolve(result)
            })
        })
    }

    open = (pin: Pin, value: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            gpio.setup(pin.value, value, (err) => {
                err ? reject(err) : resolve()
            })
        })
    }

}
