import * as spi from 'spi-device'
import * as rpio from 'rpi-gpio'

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



const init = () => {
    const spiDevice = spi.openSync(SPI_BUS, SPI_DEVICE, { mode: MODE_0 })
}


const writeData = (payload: number[]) => {
    gpioWrite(commandPin, PIN_HIGH)
    // spi transfer payload
    spi.transferSync(null);
}


const gpioWrite = (pin: Pin, value: boolean) => {

}
