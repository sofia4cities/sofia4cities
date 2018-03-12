FROM nginx:alpine

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
	  module.name="nginx"

# Default hostname
ENV SERVER_NAME=localhost \
	ZEPPELIN_HOST=localhost
	
# Create www-data user before start nginx comtainer
RUN addgroup -g 1000 -S www-data \
 && adduser -u 1000 -D -S -G www-data www-data && \
 mkdir -p /etc/nginx/conf.d/ && \
 mkdir -p /var/www/html && \
 mkdir -p /etc/nginx/ssl && \
 chown -R www-data:www-data /etc/nginx && \
 chown -R www-data:www-data /var/cache/nginx && \
 chmod 777 -R /etc/nginx && \
 chmod 777 -R /var/cache/nginx

# Copy nginx config file
COPY nginx.conf /etc/nginx/nginx_template.conf

# Copy default config
COPY default.conf /etc/nginx/conf.d/default.conf

# Copy certs
COPY sofia4cities.crt /etc/nginx/ssl/
COPY server.key /etc/nginx/ssl/
				
CMD /bin/sh -c "envsubst '\$SERVER_NAME \$ZEPPELIN_HOST' < /etc/nginx/nginx_template.conf > /etc/nginx/nginx.conf && nginx -g 'daemon off;' || cat /etc/nginx/nginx.conf"