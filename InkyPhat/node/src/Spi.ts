import * as spi from 'pi-spi'
declare const Buffer

export class Spi {

    private spiDevice

    init = () => {
        this.spiDevice = spi.initialize('/dev/spidev0.0')
    }

    write = (data: number[]): Promise<void> => {
        const payload = new Buffer(data)
        return new Promise((resolve, reject) => {
            this.spiDevice.write(payload, (err) => {
                err ? reject(err) : resolve()
            })
        })
    }

}
