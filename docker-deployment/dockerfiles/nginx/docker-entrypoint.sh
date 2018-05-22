#!/bin/sh

if [ "$RESOLVER" == "localhost" ]; then
    export RESOLVER=`cat /etc/resolv.conf | grep "nameserver" | awk '{print $2}' | tr '\n' ' '`
    echo "1- Nameserver is: $RESOLVER"
fi

echo "2- Nameserver is: $RESOLVER"

echo "Copying nginx config"
envsubst '\$SERVER_NAME \$ZEPPELIN_HOST \$RESOLVER' < /etc/nginx/nginx_template.conf > /etc/nginx/nginx.conf

echo "Using nginx config:"
cat /etc/nginx/nginx.conf

echo "Starting nginx"
nginx -c /etc/nginx/nginx.conf -g "daemon off;"