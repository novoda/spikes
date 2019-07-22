const fs = require('fs-extra')

module.exports = class AndroidVersionProperties {

    update(options) {
        const currentProps = this._read(options.path)
        const newProps = {
            versionCode: parseInt(currentProps.versionCode, 10) + options.increment,
            versionName: options.versionName
        }
        this._write(options.path, newProps)
    }

    _write(path, props) {
        fs.writeJsonSync(path, props)
    }

    _read(path) {
        return fs.readJsonSync(path)
    }

}
