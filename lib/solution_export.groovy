

/**
  * Helper methods to export Servoy solutions with the help of a command line IDE
  */


def exportSolutions(String equinoxJar,String servoyAppServerHome,String solutionsToExport,String exportOutputFolder,String exportServoyProperties) {
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




