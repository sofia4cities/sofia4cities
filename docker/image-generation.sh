buildImage()
{
	echo "Docker image generation with spotify plugin for Sofia2 module: "$1 
	mvn clean package docker:build -Dmaven.test.skip=true
}

echo "##########################################################################################"
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