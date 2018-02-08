pipeline {
   
   // Execute the Pipeline, or stage, on any available agent	
   agent { node { label 'sofia2master' } }

   options { 
   	  buildDiscarder(logRotator(numToKeepStr:'1'))
      disableConcurrentBuilds()
   }
      
   tools { 
   	  maven 'maven'
   	  jdk 'java8'
   }   

   environment {          
      // Base sources path 
      SYSTEMCONFIG = 'systemconfig-init' 
      DOCKERCONFIG = 'docker'   
	  BRANCHNAME = 'develop'	
   }
   
   triggers {
   	  // some time between 12:00 AM (midnight) to 2:59 AM
   	  cron('H H(0-2) * * *')
   }   
   
   stages {
	   
	   stage('Build Artifacts') {
            when {
                branch "${env.BRANCHNAME}"
            }

	   		steps {	   			
					
				// Starts BDC, BDTR and Quasar	
				dir("${env.DOCKERCONFIG}") {
					sh "docker-compose up -d || true"
				}
					
				// Load Sofia2 CDB and BDTR					
	   			dir("${env.SYSTEMCONFIG}") {
					// Wait until CDB and BDTR are up and running
					sleep 10
						
					sh "mvn spring-boot:run"	  			
	   			}					
					
		    	// Run maven build
	        	sh "mvn clean install"
	   		}
	   }	 
   
   }
   
   post {
        always {
			dir("${env.DOCKERCONFIG}") {
				echo "Stopping Docker containers"
				sh "docker-compose down || true"
				
        		echo "Removing orphan volumes"
        		sh "docker volume rm \$(docker volume ls -qf dangling=true) || true"				
			}        
			
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