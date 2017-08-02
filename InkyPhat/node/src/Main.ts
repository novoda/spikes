import { Spi } from './Spi'
import { Gpio } from './Gpio'
import { Pin } from './Pin'

declare const Buffer

enum Palette {
    BLACK,
    RED,
    WHITE
}



class Command {
    type: number
    data: number[]

    constructor(type: number, data: number[]) {
        this.type = type
        this.data = data
    }
}


const WIDTH = 104
const HEIGHT = 212


const PIN_HIGH = true
const PIN_LOW = false

const SPI_BUS = 0
const SPI_DEVICE = 0
const MODE_0 = 0


const POWER_SETTING_OFF = new Command(0x01, [0x02, 0x00, 0x00, 0x00])
const VCOM_DATA_INTERVAL_SETTING_OFF = new Command(0x50, [0x00])

const POWER_SETTING_ON = new Command(0x01, [0x07, 0x00, 0x0A, 0x00]);
const BOOSTER_SOFT_START = new Command(0x060, [0x07, 0x07, 0x07]);
const PANEL_SETTING = new Command(0x00, [0b11001111])
const VCOM_DATA_INTERVAL_SETTING_ON = new Command(0x50, [0b00000111 | 0b00000000]);

const OSCILLATOR_CONTROL = new Command(0x30, [0x29]);
const RESOLUTION_SETTING = new Command(0x61, [0x68, 0x00, 0xD4]);
const VCOM_DC_SETTING = new Command(0x82, [0x0A]);


const busyPin = new Pin(11)
const commandPin = new Pin(15)
const resetPin = new Pin(13)

const display = {}

const gpio = new Gpio()
const spi = new Spi()

const init = async () => {
    console.log('init')
    spi.init()
    console.log('spi open')

    await gpio.open(commandPin, 'low')
    await gpio.open(resetPin, 'high')
    await gpio.open(busyPin, 'in')

    await refresh()
    return 'finished'
}

const refresh = async () => {
    await turnDisplayOn()
    await update()
    await turnDisplayOff()
}

const turnDisplayOn = async () => {
    console.log('turning display on')
    await busyWait()

    await sendCommand(POWER_SETTING_OFF)
    await sendCommand(BOOSTER_SOFT_START)
    await writeData(PIN_LOW, [0x04])

    await busyWait()

    await sendCommand(PANEL_SETTING)
    await sendCommand(VCOM_DATA_INTERVAL_SETTING_ON)

    await sendCommand(OSCILLATOR_CONTROL);
    await sendCommand(RESOLUTION_SETTING);
    await sendCommand(VCOM_DC_SETTING);

    await writeData(PIN_LOW, [0x92])
}

const update = async () => {
    const black = new Array((WIDTH * HEIGHT) / 8).fill(0)
    await sendCommand(new Command(0x10, black))

    // const red = asDisplayArray(flatten(display), 1)
    // sendCommand(new Command(0x13, red))

    await writeData(false, [0x12])
}

const turnDisplayOff = async () => {
    console.log('turning display off')
    await busyWait();
    await sendCommand(VCOM_DATA_INTERVAL_SETTING_OFF)
    await sendCommand(POWER_SETTING_OFF)
    await writeData(PIN_LOW, [0x02])
}

const sendCommand = async (command: Command) => {
    console.log('sending command', command)
    await writeData(PIN_LOW, [command.type])
    await writeData(PIN_HIGH, command.data)
}

const writeData = async (commandType: boolean, data: number[]) => {
    await gpio.write(commandPin, commandType)
    await spi.write(data)
}

const busyWait = async () => {
    while (true) {
        let result = await gpio.read(busyPin)
        if (result) {
            break
        }
    }
}

init().then(console.log).catch(console.log)


// const setPixel = (x: number, y: number, color: number) => {
//     if (x > WIDTH) {
//         throw new Error(`${x} cannot be drawn. Max width is ${WIDTH}`)
//     }
//     if (y > HEIGHT) {
//         throw new Error(`${y} cannot be drawn. Max height is ${HEIGHT}`)
//     }
//     display[`${x}x${y}`] = color;
// }


// const flatten = (input: any) => {
//     const output: number[] = new Array[WIDTH * HEIGHT];
//     let index = 0;
//     for (let y = 0; y < HEIGHT; y++) {
//         for (let x = 0; x < WIDTH; x++) {
//             const color = display[`${x}x${y}`];
//             output[index++] = color;
//         }
//     }
//     return output;
// }

// const asDisplayArray = (buffer: number[], choice: number): number[] => {
//     const display = []
//     let bitPosition = 7
//     let segment = 0
//     let colorByte = 0b00000000
//     buffer.forEach(each => {
//         if (each === choice) {
//             colorByte |= 1 << bitPosition;
//         }
//         bitPosition--;
//         if (bitPosition == -1) {
//             display[segment++] = colorByte;
//             bitPosition = 7;
//             colorByte = 0b00000000;
//         }
//     })
//     return display;
// }
