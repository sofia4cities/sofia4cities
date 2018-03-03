buildImage()
{
	echo "Docker image generation with spotify plugin for Sofia2 module: "$1 
	mvn clean package docker:build -Dmaven.test.skip=true
}

buildConfigDB()
{
	echo "ConfigDB image generation with Docker CLI: "
	docker build -t sofia2/configdb:$1 .
}

buildSchedulerDB()
{
	echo "SchedulerDB image generation with Docker CLI: "
	docker build -t sofia2/schedulerdb:$1 .
}

buildRealTimeDB()
{
	echo "RealTimeDB image generation with Docker CLI: "
	docker build -t sofia2/realtimedb:$1 .
}

echo "##########################################################################################"
echo "#                                                                                        #"
echo "#   _____             _                                                                  #"              
echo "#  |  __ \           | |                                                                 #"            
echo "#  | |  | | ___   ___| | _____ _ __                                                      #"
echo "#  | |  | |/ _ \ / __| |/ / _ \ '__|                                                     #"
echo "#  | |__| | (_) | (__|   <  __/ |                                                        #"
echo "#  |_____/ \___/ \___|_|\_\___|_|                                                        #"                
echo "#                                                                                        #"
echo "# Sofia2 Docker Image generation                                                         #"
echo "#                                                                                        #"
echo "##########################################################################################"
echo "Continue? y/n: "

read confirmation

if [ "$confirmation" != "y" ]; then
	exit 1
fi

homepath=$PWD

cd $homepath/../modules/control-panel/

buildImage "Control Panel"

cd $homepath/../modules/iotbroker/sofia2-iotbroker-boot/

buildImage "IoT Broker"

cd $homepath/../modules/api-manager/

buildImage "API Manager"

cd $homepath/dockerfiles/configdb

buildConfigDB latest

cd $homepath/dockerfiles/schedulerdb

buildSchedulerDB latest

cd $homepath/dockerfiles/realtimedb

buildRealTimeDB latest

echo "Docker images successfully generated!"

exit 0