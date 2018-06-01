#!/bin/bash

#
# Copyright Indra Sistemas, S.A.
# 2013-2018 SPAIN
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#      http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ------------------------------------------------------------------------

buildImage()
{
	echo "Docker image generation with spotify plugin for Sofia2 module: "$1 
	mvn clean package docker:build -Dmaven.test.skip=true
}

deleteImage()
{
	echo "Delete Sofia2 image locally, module: "$1  
	docker rmi -f $(docker images | grep sofia2/$1 |  awk '{print $3}' | uniq)		
}

deleteUntaggedImages()
{
	echo "Remove all untagged images"
	docker rmi $(docker images -f dangling=true -q)
}

removeOrphanVolumes()
{
	echo "Remove containers orphan volumes"
	docker volume rm $(docker volume ls -qf dangling=true)
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

buildElasticSearchDB()
{
	echo "ElasticSearchDB image generation with Docker CLI: "
	docker build -t sofia2/elasticdb:$1 .
}

buildNginx()
{
	echo "NGINX image generation with Docker CLI: "
	docker build -t sofia2/nginx:$1 .		
}

buildQuasar()
{
	echo "Quasar image generation with Docker CLI: "
	echo "Step 1: download quasar binary file"
	wget https://github.com/quasar-analytics/quasar/releases/download/v14.2.6-quasar-web/quasar-web-assembly-14.2.6.jar
	
	echo "Step 2: build quasar image"
	docker build -t sofia2/quasar:$1 .	
	
	rm quasar-web-assembly*.jar
}

prepareNodeRED()
{
	cp $homepath/../../tools/Flow-Engine-Manager/*.zip $homepath/../../modules/flow-engine/docker/nodered.zip
	cd $homepath/../../modules/flow-engine/docker
	unzip nodered.zip		
	cp -f $homepath/../dockerfiles/nodered/proxy-nodered.js $homepath/../../modules/flow-engine/docker/Flow-Engine-Manager/
	cp -f $homepath/../dockerfiles/nodered/sofia2-config-nodes-config.js $homepath/../../modules/flow-engine/docker/Flow-Engine-Manager/node_modules/node-red-sofia/nodes/config/sofia2-config.js
	cp -f $homepath/../dockerfiles/nodered/sofia2-config-public-config.js $homepath/../../modules/flow-engine/docker/Flow-Engine-Manager/node_modules/node-red-sofia/public/config/sofia2-config.js	
}

removeNodeRED()
{
	cd $homepath/../../modules/flow-engine/docker
	rm -rf Flow-Engine-Manager
	rm nodered.zip		
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
echo "# arg1 (opt) --> string represents the name of module to deploy image                    #"
echo "#                                                                                        #"
echo "##########################################################################################"

homepath=$PWD

if [[ "$1" == "controlpanel" ]]; then
	cd $homepath/../../modules/control-panel/
	buildImage "Control Panel"
fi

if [[ "$1" == "iotbroker" ]]; then
	cd $homepath/../../modules/iotbroker/sofia2-iotbroker-boot/	
	buildImage "IoT Broker"
fi

if [[ "$1" == "apimanager" ]]; then
	cd $homepath/../../modules/api-manager/	
	buildImage "API Manager"
fi

if [[ "$1" == "digitaltwin" ]]; then
	cd $homepath/../../modules/digitaltwin-broker/	
	buildImage "Digital Twin"
fi

if [[ "$1" == "dashboard" ]]; then
	cd $homepath/../../modules/dashboard-engine/
	buildImage "Dashboard Engine"
fi

if [[ "$1" == "devicesimulator" ]]; then
	cd $homepath/../../modules/device-simulator/
	buildImage "Device Simulator" 
fi

if [[ "$1" == "monitoringui" ]]; then
	cd $homepath/../../modules/monitoring-ui/
	buildImage "Monitoring UI"
fi

if [[ "$1" == "flowengine" ]]; then
	prepareNodeRED		
	
	cd $homepath/../../modules/flow-engine/
	buildImage "Flow Engine"
	
	removeNodeRED
fi

if [[ "$1" == "nginx" ]]; then
	cd $homepath/../dockerfiles/nginx
	buildNginx latest
fi

if [[ "$1" == "configinit" ]]; then
	cd $homepath/../../config/init/
	buildImage "Config Init"
fi

echo "Docker images successfully generated!"

deleteUntaggedImages
removeOrphanVolumes

exit 0