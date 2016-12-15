
/**
  * Helper methods to import solutions on a certain server
  */

def restartServer(String serverBaseUrl) {

  echo "Restarting Servoy Server ${serverBaseUrl}"
        
  def restartTargetUrl = "http://${serverBaseUrl}/servoy-admin"
  
  httpRequest consoleLogResponseBody: true, 
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
            [name: 'Accept-Encoding', value: 'gzip, deflate'],
            [name: 'Accept-Language', value: 'nl-NL,nl;q=0.8,en-US;q=0.6,en;q=0.4']
        ], 
        
    httpMode: 'POST', 
    requestBody: 'rf=Restart+Server', 
    url: "http://${serverBaseUrl}/servoy-admin/"

	def max_retries = 5
	def is_online = false

	while ( max_retries > 0 && !is_online) {
    	max_retries--
    	
		//sleep for a minute, very easy solution as the Servoy app server is restarting
    	Thread.sleep(60000)
    	
    	def response = httpRequest consoleLogResponseBody: true, 
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
    		timeout: 5, url: restartTargetUrl


		if (response.indexOf("Status") > -1) { 
			is_offline = false
		}
		
	}
	
	return is_online

}


def truncateRepositoryDb(String serverBaseUrl) {
	
	httpRequest acceptType: "APPLICATION_JSON", 
            consoleLogResponseBody: true, 
            contentType: "APPLICATION_JSON", 
            httpMode: 'POST', 
            requestBody: "{ data:\"\"}", 
            timeout: 60, 
            url: "http://${serverBaseUrl}/servoy-service/api_v1/88888888-8888-8888-8888-888888888888/admin/trunc_repositorydb"
	
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

