pipeline {
   
   // Execute the Pipeline, or stage, on any available agent	
   agent { node { label 'sofia2master' } }
   
   tools { 
   	  maven 'maven'
   	  jdk 'java8'
   }   

   environment {          
      // Base sources path 
      BASE_PATH = ''    

   }
   
   triggers {
   	  // some time between 12:00 AM (midnight) to 2:59 AM
   	  cron('H H(0-2) * * *')
   }   
   
   stages {
	   
	   stage('Build Artifacts') {
            when {
                branch 'develop'
            }	   
	   		steps {
		    	// Run maven build
	        	sh "mvn clean install -Dmaven.test.skip=true"
	   		}
	   }	 
   
   }
   
   post {
        always {
        	echo 'Clean up workspace...'
        	deleteDir()
        }   
	    success {
	        echo "Pipeline: '${currentBuild.fullDisplayName}' completado satisfactoriamente" 
	        mail from: 'plataformasofia2@gmail.com',
	             to: 'lmgracia@minsait.com, jjmorenoa@minsait.com, mmoran@minsait.com',
	             subject: "La compilación de la rama ${env.BRANCH_NAME} del proyecto Select4Cities se ha completado satisfactoriamente. id del Build: ${currentBuild.fullDisplayName}",
	             body: "La compilación de la rama ${env.BRANCH_NAME} del proyecto Select4Cities se ha completado satisfactoriamente. id del Build: ${env.BUILD_URL}"
	    }
	    failure {
	    	echo "El pipeline: '${currentBuild.fullDisplayName}' ha fallado: '${env.BUILD_URL}' se procede a enviar notificación por correo"
	        mail from: 'plataformasofia2@gmail.com',
	             to: 'lmgracia@minsait.com, jjmorenoa@minsait.com, mmoran@minsait.com',
	             subject: "Ha ocurrido un error al compilar los fuentes de la rama ${env.BRANCH_NAME} del proyecto Select4Cities: ${currentBuild.fullDisplayName}",
	             body: "Ha ocurrido un error al compilar los fuentes de la rama ${env.BRANCH_NAME} del proyecto Select4Cities. id del Build: ${env.BUILD_URL}"	    
	    }
   }      
}