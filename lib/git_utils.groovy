

/**
  * Helper methods for GIT commands
  */

def checkOut(String baseFolder, String branchName) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} checkout -B \"${branchName}\""
	return result
}

def addFiles(String exportOutputFolder, String filePattern) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} add ${filePattern}"
	return result
}

def commit(String baseFolder, String commitMessage, String author) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} commit --all -m \"${commitMessage}\" --author=\"${author}\""
	return result
}

def tag(String baseFolder, String tagName, String tagComment) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} tag -a \"${tagName}\" -m \"${tagComment}\""
	return result
}

def pushAll(String baseFolder) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} push --all\""
	return result
}

def pushTags(String baseFolder) {
    def result = bat returnStdout: true, script: "git -C ${baseFolder} push --tags\""
	return result
}


return this




