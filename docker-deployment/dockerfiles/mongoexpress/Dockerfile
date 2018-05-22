FROM mongo-express:latest

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="mongoexpress"

# create sofia user/group
RUN addgroup -S sofia -g 433 && adduser -u 431 -S -g sofia -h /usr/local -s /sbin/nologin sofia 
      
ENV ME_CONFIG_MONGODB_SERVER=realtimedb \
	ME_CONFIG_MONGODB_PORT=27017 \
	ME_CONFIG_SITE_BASEURL=/mongo \
	ME_CONFIG_BASICAUTH_USERNAME=onesait \
	ME_CONFIG_BASICAUTH_PASSWORD=0nesa1t-2018-!

USER sofia
	      
EXPOSE 8081      