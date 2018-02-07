pipeline {
   
   // Execute the Pipeline, or stage, on any available agent	
   agent { node { label 'sofia2master' } }
   
   tools { 
   	  maven 'maven'
   	  jdk 'java8'
   }   

   environment {          
      // Base sources path 
      SYSTEMCONFIG = 'systemconfig-init'    

   }
   
   triggers {
   	  // some time between 12:00 AM (midnight) to 2:59 AM
   	  cron('H H(0-2) * * *')
   }   
   
   stages {
	   
	   stage('Build Artifacts') {
            when {
                branch 'feature/testdockerintegration'
            }	   
	   		steps {	   			
					
				// Load Sofia CDB					
	   			dir("${env.SYSTEMCONFIG}") {	   				
	   				sh "docker run --name sofiabdc \
						-e MYSQL_ROOT_PASSWORD='my-secret-pw' \
						-e MYSQL_USER='indra' \
						-e MYSQL_PASSWORD='select4cities2018' \
						-e MYSQL_DATABASE='sofia2_s4c' \
						-p 3306:3306 \
						-d mysql/mysql-server"	 
						
					sh "mvn spring-boot:run"	  			
	   			}					
					
		    	// Run maven build
	        	sh "mvn clean install"
	   		}
	   }	 
   
   }
   
   post {
        always {
        	echo 'Clean up workspace...'
        	deleteDir()
        	
        	echo 'Stopping Docker containers...'
        	sh "docker stop sofiabdc || true"
        	sh "docker rm sofiabdc || true"
        	
        	echo "Removing orphan volumes"
        	sh "docker volume rm \$(docker volume ls -qf dangling=true) || true"
        }   
	    success {
	        echo "Pipeline: '${currentBuild.fullDisplayName}' completado satisfactoriamente" 
	        mail from: 'plataformasofia2@gmail.com',
	             to: 'mmoran@minsait.com',
	             subject: "La compilación de la rama ${env.BRANCH_NAME} del proyecto Select4Cities se ha completado satisfactoriamente. id del Build: ${currentBuild.fullDisplayName}",
	             body: "La compilación de la rama ${env.BRANCH_NAME} del proyecto Select4Cities se ha completado satisfactoriamente. id del Build: ${env.BUILD_URL}"
	    }
	    failure {
	    	echo "El pipeline: '${currentBuild.fullDisplayName}' ha fallado: '${env.BUILD_URL}' se procede a enviar notificación por correo"
	        mail from: 'plataformasofia2@gmail.com',
	             to: 'mmoran@minsait.com',
	             subject: "Ha ocurrido un error al compilar los fuentes de la rama ${env.BRANCH_NAME} del proyecto Select4Cities: ${currentBuild.fullDisplayName}",
	             body: "Ha ocurrido un error al compilar los fuentes de la rama ${env.BRANCH_NAME} del proyecto Select4Cities. id del Build: ${env.BUILD_URL}"	    
	    }
   }      
}