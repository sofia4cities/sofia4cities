FROM mysql/mysql-server:5.7

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="schedulerdb"	

# MySQL environment
ENV MYSQL_ROOT_PASSWORD=changeIt! \
 	MYSQL_ROOT_HOST=% \
 	MYSQL_USER=indra \ 
 	MYSQL_PASSWORD=select4cities2018 \
	MYSQL_DATABASE=sofia2_s4c 

# Ignore case
RUN echo "lower_case_table_names = 1" >> /etc/my.cnf

# Allow large files
RUN echo "max_allowed_packet=536870912" >> /etc/my.cnf

EXPOSE 3306