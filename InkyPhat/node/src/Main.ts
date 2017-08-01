import * as spi from 'spi-device'
import * as gpio from 'rpi-gpio'

declare const Buffer

enum Palette {
    BLACK,
    RED,
    WHITE
}


class Pin {

    private bcm: number

    constructor(bcm: number) {
        this.bcm = bcm
    }

    value(): number {
        return this.bcm
    }

}

class Payload {

}

const PIN_HIGH = true
const PIN_LOW = false

const SPI_BUS = 0
const SPI_DEVICE = 0
const MODE_0 = 0

const busyPin = new Pin(17)
const commandPin = new Pin(22)
const resetPin = new Pin(27)

let spiDevice


const init = async () => {
    console.log('init')
    spiDevice = spi.openSync(SPI_BUS, SPI_DEVICE, { mode: MODE_0 })
    console.log('spi open')

    gpio.setMode(gpio.MODE_BCM)

    await gpioSetup(commandPin, gpio.DIR_LOW)
    console.log('command open')

    await gpioSetup(resetPin, gpio.DIR_HIGH)
    console.log('reset open')
    
    await gpioSetup(busyPin, gpio.DIR_IN)
    console.log('busy open')

    await turnDisplayOff()
}

const gpioSetup = (pin: Pin, value: any) => {
    return new Promise((resolve, reject) => {
        gpio.setup(busyPin.value(), value, (err) => {
            err ? reject(err) : resolve()
        })
    })
}




const turnDisplayOff = async () => {
    console.log('turning display off')

    await busyWait();
    await sendCommand(0x50, [0x00])
    await sendCommand(0x01, [0x02, 0x00, 0x00, 0x00])
    await writeData(false, [0x02])
}

const sendCommand = (commandData: number, data: number[]) => {
    writeData(false, [commandData])
    writeData(true, data)
}

const writeData = async (commandType: boolean, data: number[]) => {
    await gpioWrite(commandPin, commandType)
    var message = [{
        sendBuffer: new Buffer(data),
        byteLength: data.length
    }];
    spiDevice.transferSync(message)
}

const gpioWrite = (pin: Pin, value: boolean): Promise<any> => {
    return new Promise((resolve, reject) => {
        gpio.write(pin.value(), value, (err) => {
            err ? reject(err) : resolve()
        })
    })
}

const busyWait = async () => {
    while (true) {
        let result = await gpioRead(busyPin)
        if (result) {
            break
        }
    }
}

const gpioRead = (pin: Pin): Promise<boolean> => {
    return new Promise((resolve, reject) => {
        gpio.read(pin.value(), (err, result) => {
            err ? reject(err) : resolve(result)
        })
    })
}

init().then(console.log).catch(console.log)
