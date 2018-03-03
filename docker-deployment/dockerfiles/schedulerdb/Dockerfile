FROM mysql/mysql-server:latest

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="schedulerdb"	

# MySQL environment
ENV MYSQL_ALLOW_EMPTY_PASSWORD true
ENV MYSQL_ROOT_HOST %
ENV MYSQL_USER indra
ENV MYSQL_PASSWORD select4cities2018
ENV MYSQL_DATABASE sofia2_s4c_scheduler

# Ignore case
RUN echo "lower_case_table_names = 1" >> /etc/my.cnf

EXPOSE 3306