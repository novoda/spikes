import * as spi from 'spi-device'
import * as rpio from 'rpio'

enum Palette {
    BLACK,
    RED,
    WHITE
}






class Pin {

}


const SPI_BUS = 0
const SPI_DEVICE = 0
const MODE_0 = 0


let commandPin: Pin
let busyPin: Pin
let resetPin: Pin



const init = () => {
    rpio.init({ mapping: 'gpio' })
    const spiDevice = spi.openSync(SPI_BUS, SPI_DEVICE, { mode: MODE_0 })
    rpio.open()
}

