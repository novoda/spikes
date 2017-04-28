import Foundation
import Files

extension Files.File {

	var isSourceCode: Bool {
		return self.extension == "m" ||
			self.extension == "swift" ||
			self.extension == "h"
	}

}

extension String {

	var lineCount: Int {
		return self.components(separatedBy: ",").count
	}

}

for file in try Folder(path: "./").makeFileSequence(recursive: true, includeHidden: false)
	where file.isSourceCode {
	do {
		let string = try file.readAsString()
		print("\(file.name):\(string.lineCount)")
	} catch {
		print("Failed to read \(file.path)")
	}
}
