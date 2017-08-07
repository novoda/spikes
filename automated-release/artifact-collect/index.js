const path = require('path');
const archiver = require('archiver')
const fs = require('fs-extra');

const collectDirectory = (input, outputDir, outputFilename) => {
    const output = createOutput(outputDir, outputFilename)
    const archive = archiver.create('zip', {})
    const outputFile = fs.createWriteStream(output)
    archive.pipe(outputFile)
    archive
        .directory(input)
        .finalize()
    return {
        name: outputFilename,
        path: output
    }
}

const collectFile = (input, outputDir, outputFilename) => {
    const output = createOutput(outputDir, outputFilename)
    fs.copySync(input, output)
    return {
        name: outputFilename,
        path: output
    }
}

const createOutput = (outputDir, outputFilename) => {
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir)
    }
    return path.resolve(outputDir, outputFilename)
}

module.exports = {
    collectDirectory: collectDirectory,
    collectFile: collectFile
}
