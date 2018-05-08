FROM mysql/mysql-server:5.7

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="schedulerdb"	

# MySQL environment
ENV MYSQL_ROOT_PASSWORD=changeIt! \
 	MYSQL_ROOT_HOST=% \
 	MYSQL_USER=indra \
 	MYSQL_PASSWORD=select4cities2018 \
 	MYSQL_DATABASE=sofia2_s4c_scheduler

# Ignore case
RUN echo "lower_case_table_names = 1" >> /etc/my.cnf

# Copy to images mysql path
COPY quartz-schema.sql /docker-entrypoint-initdb.d/quartz-schema.sql

EXPOSE 3306