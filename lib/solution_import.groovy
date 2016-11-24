


/**
  * Helper methods to import solutions on a certain server
  */


def importSolutions(String solutionFilePath, String serverUrl, boolean allowDBModelChanges = false) {

  echo "importing solution ${solutionFilePath} - to ${serverUrl} - allowing model changes ${allowDBModelChanges}"
        
  def server = "server"
  def importTargetUrl = "http://${serverUrl}/servoy-admin/solutions/import"
  def projectName = "INT"

  def username = "globisadmin"
  def apiToken = "Pwd49lo815"


  def client = new HttpClient()
  client.state.setCredentials(
    AuthScope.ANY,
    new UsernamePasswordCredentials( username, apiToken )
  )

  // Jenkins does not do any authentication negotiation,
  // ie. it does not return a 401 (Unauthorized)
  // but immediately a 403 (Forbidden)
  client.params.authenticationPreemptive = true

  def post = new PostMethod( "${importTargetUrl}" )
  post.doAuthentication = true
  
  def workspace = pwd()
  
   input = new File(workspace + "/" + solutionFilePath);
  RequestEntity entity = new FileRequestEntity(input, "application/octet-stream; charset=UTF-8");
  post.setRequestEntity(entity);
  
  Part[] multiParts = new Part[10];
  
  if (allowDBModelChanges) {
  	multiParts = new Part[11];
  }
  
  //Part[] multiParts = {
  multiParts[0] = new StringPart("emm", "on"); //Enter maintenance mode
  multiParts[1] = new StringPart("ac", "on");  //activate new release
  multiParts[2] = new StringPart("newname", ""); //use a new name stays empty
  multiParts[3] = new StringPart("fd", "on");  //override existing default values
  multiParts[4] = new StringPart("ak", "on");  //Allow reserved keywords
  multiParts[5] = new StringPart("id", "on");  //Import i18n  data (insert & update)
  multiParts[6] = new StringPart("md", "on");  //import solution meta data
  multiParts[7] = new StringPart("solution_password", ""); //solution pwd empty
  multiParts[8] = new StringPart("submit", "Import!");  //submit button
   
  if (allowDBModelChanges) {
  	multiParts[10] = new StringPart("dm", "on");  //allow data model database changes
  }
  
  post.addParameter("solution_password", "");
  post.addParameter("submit", "Import!");
  multiParts[9] = new FilePart("if", "if", input, "application/octet-stream", "charset=UTF-8");

  post.setRequestEntity(
      new MultipartRequestEntity(multiParts, post.getParams())
      );
  
  try {
    int result = client.executeMethod(post)
    println "Return code: ${result}"
    string body = post.getResponseBodyAsString();
    println body;
    if (body.indexOf("[error]") > -1) 
        currentBuild.result = 'FAILURE'
  } finally {
    	post.releaseConnection()
  }

}

