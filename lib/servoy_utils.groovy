
/**
  * Helper methods to import solutions on a certain server
  */

def restartServer(String serverBaseUrl) {

  echo "Restarting Servoy Server ${serverBaseUrl}"
        
  def restartTargetUrl = "http://${serverBaseUrl}/servoy-admin"
  
  httpRequest consoleLogResponseBody: false, 
    customHeaders: 
        [
            [name: 'Host', value: "${serverBaseUrl}"],
            [name: 'Referer', value: "http://${serverBaseUrl}/servoy-admin/shutdown"],
            [name: 'Authorization', value: 'Basic Z2xvYmlzYWRtaW46UHdkNDlsbzgxNQ=='],
            [name: 'Origin', value: "http://${serverBaseUrl}"],
            [name: 'Connection', value: 'keep-alive'],
            [name: 'Cache-Control', value: 'max-age=0'],
            [name: 'Upgrade-Insecure-Requests', value: '1'],
            [name: 'Origin', value: "http://${serverBaseUrl}"],
            [name: 'User-Agent', value: 'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36'],
            [name: 'Content-Type', value: 'application/x-www-form-urlencoded'],
            [name: 'Accept', value: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'],
            [name: 'Accept-Language', value: 'nl-NL,nl;q=0.8,en-US;q=0.6,en;q=0.4']
        ], 
        
    httpMode: 'POST', 
    requestBody: 'rf=Restart+Server', 
    url: "http://${serverBaseUrl}/servoy-admin/"

	def max_retries = 15 //for TST & REL, the time needed to get a server up and running is between 1 and 3 minutes normally
	def is_online = false

	while ( max_retries > 0 && !is_online) {
    	max_retries--
    	
		//sleep for a minute, very easy solution as the Servoy app server is restarting
    	Thread.sleep(20000)
    	
    	def response = httpRequest consoleLogResponseBody: false, 
    		customHeaders: 
        	[
        	    [name: 'Host', value: "${serverBaseUrl}"],
        	    [name: 'Referer', value: "http://${serverBaseUrl}/servoy-admin/shutdown"],
        	    [name: 'Authorization', value: 'Basic Z2xvYmlzYWRtaW46UHdkNDlsbzgxNQ=='],
        	    [name: 'Origin', value: "http://${serverBaseUrl}"],
        	    [name: 'Connection', value: 'keep-alive'],
        	    [name: 'Cache-Control', value: 'max-age=0'],
        	    [name: 'Upgrade-Insecure-Requests', value: '1'],
        	    [name: 'Origin', value: "http://${serverBaseUrl}"],
        	    [name: 'User-Agent', value: 'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36'],
        	    [name: 'Content-Type', value: 'application/x-www-form-urlencoded'],
        	    [name: 'Accept', value: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'],
        	    [name: 'Accept-Language', value: 'nl-NL,nl;q=0.8,en-US;q=0.6,en;q=0.4']
        	], 
    		timeout: 5, url: restartTargetUrl, validResponseCodes: '100:599'


		if (response.getContent().indexOf("Servoy Server Administration") > -1) { 
			is_online = true
		}
		
	}
	
	return is_online

}


def truncateRepositoryDb(String serverBaseUrl) {
	
	def max_retries = 5
	def retry = true

	while ( max_retries > 0 && retry) {
    	max_retries--
    	
		//sleep for a minute, very easy solution as the Servoy app server is restarting
    	Thread.sleep(20000)
    	
    	def empty_request_body = /{ data:"" }/
    	
    	echo "request body: ${empty_request_body}"
    	
		def response = httpRequest acceptType: "APPLICATION_JSON", 
    	        consoleLogResponseBody: true, 
    	        contentType: "APPLICATION_JSON", 
    	        httpMode: 'POST', 
    	        requestBody: empty_request_body, 
    	        url: "http://${serverBaseUrl}/servoy-service/api_v1/88888888-8888-8888-8888-888888888888/admin/trunc_repositorydb",
    	        timeout: 5, validResponseCodes: '100:599'
    	
    	if (response.getStatus() == 200) { 
			retry = false
		}
	}
    	
	
}

def exportReleaseFiles(String releaseFileExporterUrl, String releaseFileShareEscaped, String ownerIdsEscaped) {
 httpRequest acceptType: "APPLICATION_JSON", 
            consoleLogResponseBody: true, 
            contentType: "APPLICATION_JSON", 
            httpMode: 'POST', 
            requestBody: "{server_path: \"${releaseFileShareEscaped}\", owner_ids: ${ownerIdsEscaped}, type:\"D\"}", 
            timeout: 600, 
            url: "http://${releaseFileExporterUrl}/servoy-service/api_v1/88888888-8888-8888-8888-888888888888/admin_meta/exportreleasefiles", 
            validResponseContent: '{"messages":[]}'
}

def exportSolutions(String equinoxJar, String solutionsToExport, String exportOutputFolder, String servoyAppServerHome, String exportServoyProperties ) {

       def exportCommand = 'java '
       exportCommand += "-cp ${equinoxJar}"
       exportCommand += " org.eclipse.equinox.launcher.Main "
       exportCommand += " -Xms256m "
       exportCommand += " -Xmx2048m "
       exportCommand += " -XX:MaxPermSize=512M "
       exportCommand += " -Dservoy.application_server.dir=${servoyAppServerHome}"
       exportCommand += " -Dosgi.configuration.cascaded=false "
       exportCommand += " -data globis_online_repo"
       exportCommand += " -application com.servoy.eclipse.exporter.solution.application"
       exportCommand += " -s ${solutionsToExport} "
       exportCommand += " -o ${exportOutputFolder}"
       exportCommand += " -as ${servoyAppServerHome}"
       exportCommand += " -p ${exportServoyProperties}"
       exportCommand += " -ie "
       exportCommand += " -dbd "
       exportCommand += " -dbi "
       exportCommand += " -md ws "
       exportCommand += " -i18n "
       exportCommand += " -tables "
       exportCommand += " -modules "
       
       echo exportCommand
       def out_str =  bat returnStdout: true, script: exportCommand
       echo out_str  
}



return this

