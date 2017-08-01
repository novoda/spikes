import * as spi from 'spi-device'
import * as gpio from 'rpi-gpio'

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





const init = async () => {
    const spiDevice = spi.openSync(SPI_BUS, SPI_DEVICE, { mode: MODE_0 })
    await gpioSetup(commandPin, gpio.DIR_LOW)
    await gpioSetup(resetPin, gpio.DIR_HIGH)
    await gpioSetup(busyPin, gpio.DIR_IN)
}


const writeData = (payload: number[]) => {
    // gpioSetup(commandPin, PIN_HIGH)
    // spi transfer payload
    spi.transferSync(null);
}


const gpioSetup = (pin: Pin, value: any) => {
    return new Promise((resolve, reject) => {
        gpio.setup(busyPin.value(), value, (err) => {
            err ? reject(err) : resolve()
        })
    })
}

init().then(console.log)
