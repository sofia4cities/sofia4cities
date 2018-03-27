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
      SYSTEMCONFIG = 'config/init' 
      IMAGEGENPATH = 'docker-deployment'
      DOCKERCONFIG = 'docker-deployment/data'   
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
		    	// Only compile and generate artifacts
	        	sh "mvn clean install -Dmaven.test.skip=true"	
	        	
	   			// Generates persistence images only if 
	   			// they are not present in local Docker registry		   		
	   		    dir("${env.IMAGEGENPATH}") {
	   		    	sh "./image-generation.sh -1"
	   		    }	
					
				// Starts configdb, realtimedb and Quasar	
				dir("${env.DOCKERCONFIG}") {
					sh "docker-compose up -d || true"
				}
				
				// Wait until config db are loaded
				sleep 30	
	   			
	   			// Execute tests
	   			sh "mvn clean install"			
					
				sh "mvn sonar:sonar"
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
        		
        		echo "Removing config init image"
        		sh "docker rmi -f \$(docker images | grep sofia2/configinit | awk '{print \$3}' | uniq) || true"			
			}        
			
        	echo 'Clean up workspace...'
        	deleteDir()       		
        }   
	    success {	    
	        echo "Pipeline: '${currentBuild.fullDisplayName}' completado satisfactoriamente" 
			emailext attachLog: true, 
			body: 'La compilación de la rama $BRANCH_NAME del proyecto $PROJECT_NAME se ha completado satisfactoriamente. url del Build: $BUILD_URL', 
			compressLog: true, 
			subject: '[SUCCESSFUL!] La compilación de la rama $BRANCH_NAME del proyecto $PROJECT_NAME se ha completado satisfactoriamente. id del Build: $BUILD_NUMBER', 
			to: 'mmoran@minsait.com'
 		}
	    failure {   
	    	echo "El pipeline: '${currentBuild.fullDisplayName}' ha fallado: '${env.BUILD_URL}' se procede a enviar notificación por correo"
			emailext attachLog: true, 
			body: 'Ha ocurrido un error al compilar los fuentes de la rama $BRANCH_NAME del proyecto $PROJECT_NAME.url del Build: $BUILD_URL', 
			compressLog: true, 
			subject: '[ERROR!] Ha ocurrido un error al compilar los fuentes de la rama $BRANCH_NAME del proyecto $PROJECT_NAME. Se adjuntan los logs de la compilación. id del Build: $BUILD_NUMBER', 
			to: 'mmoran@minsait.com'
	    }
   }      
}
