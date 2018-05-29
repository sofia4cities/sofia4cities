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
	echo "Docker image generation with spotify plugin for Sofia2 module: "$2
	#mvn clean package docker:build -Dmaven.test.skip=true
	cp $1/target/*-exec.jar $1/docker/
	docker build -t sofia2/$2:$3 .
	rm $1/docker/*.jar
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
	cp $homepath/../tools/Flow-Engine-Manager/*.zip $homepath/../modules/flow-engine/docker/nodered.zip
	cd $homepath/../modules/flow-engine/docker
	unzip nodered.zip		
	cp -f $homepath/dockerfiles/nodered/proxy-nodered.js $homepath/../modules/flow-engine/docker/Flow-Engine-Manager/
	cp -f $homepath/dockerfiles/nodered/sofia2-config-nodes-config.js $homepath/../modules/flow-engine/docker/Flow-Engine-Manager/node_modules/node-red-sofia/nodes/config/sofia2-config.js
	cp -f $homepath/dockerfiles/nodered/sofia2-config-public-config.js $homepath/../modules/flow-engine/docker/Flow-Engine-Manager/node_modules/node-red-sofia/public/config/sofia2-config.js	
}

removeNodeRED()
{
	cd $homepath/../modules/flow-engine/docker
	rm -rf Flow-Engine-Manager
	rm nodered.zip		
}

buildPersistence()
{
	echo "++++++++++++++++++++ Persistence layer generation..."
	
	# Generates images only if they are not present in local docker registry
	if [[ "$(docker images -q sofia2/configdb 2> /dev/null)" == "" ]]; then
		cd $homepath/dockerfiles/configdb
		buildConfigDB latest
	fi
	
	if [[ "$(docker images -q sofia2/schedulerdb 2> /dev/null)" == "" ]]; then
		cd $homepath/dockerfiles/schedulerdb
		buildSchedulerDB latest
	fi
	
	if [[ "$(docker images -q sofia2/realtimedb 2> /dev/null)" == "" ]]; then
		cd $homepath/dockerfiles/realtimedb
		buildRealTimeDB latest
	fi
	
	if [[ "$(docker images -q sofia2/quasar 2> /dev/null)" == "" ]]; then
		cd $homepath/dockerfiles/quasar
		buildQuasar latest
	fi
	
	if [[ "$(docker images -q sofia2/configinit 2> /dev/null)" == "" ]]; then
		cd $homepath/../config/init/docker
		buildImage $homepath/../config/init/ init latest
	fi		
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
echo "# arg1 (opt) --> -1 if only want to create images for modules layer (skip persistence)   #"
echo "#                                                                                        #"
echo "##########################################################################################"

homepath=$PWD

# Only create persistence layer
if [ -z "$1" ]; then
	# Generates images only if they are not present in local docker registry
	if [[ "$(docker images -q sofia2/controlpanel 2> /dev/null)" == "" ]]; then
		cd $homepath/../modules/control-panel/docker
		buildImage $homepath/../modules/control-panel controlpanel latest
	fi	
	
	if [[ "$(docker images -q sofia2/iotbroker 2> /dev/null)" == "" ]]; then
		cd $homepath/../modules/iotbroker/sofia2-iotbroker-boot/docker	
		buildImage $homepath/../modules/iotbroker/sofia2-iotbroker-boot iotbroker latest
	fi
	
	if [[ "$(docker images -q sofia2/apimanager 2> /dev/null)" == "" ]]; then	
		cd $homepath/../modules/api-manager/docker
		buildImage $homepath/../modules/api-manager apimanager latest
	fi	
	
	# Persistence layer image generation
	buildPersistence		
fi

if [ ! -z "$1" ]; then
	# Persistence layer image generation
	buildPersistence
fi

echo "Docker images successfully generated!"

exit 0
