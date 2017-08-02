import * as spi from 'pi-spi'
declare const Buffer

export class Spi {

    private spiDevice

    init = () => {
        this.spiDevice = spi.initialize('/dev/spidev0.0')
    }

    write = (data: number[]) => {
        const payload = new Buffer(data)
        return new Promise((resolve, reject) => {
            this.spiDevice.transfer(payload, payload.length, (err, result) => {
                err ? reject(err) : resolve(result)
            })
        })
    }

}
