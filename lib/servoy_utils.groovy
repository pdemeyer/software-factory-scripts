
/**
  * Helper methods to import solutions on a certain server
  */

def restartServer(String serverBaseUrl) {

  //def serverBaseUrl = "192.168.73.159:8090"

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


	//sleep for a minute, very easy solution as the Servoy app server is restarting
    Thread.sleep(60000)

}

return this

