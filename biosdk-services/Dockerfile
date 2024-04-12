FROM openjdk:11

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL
ARG spring_config_url

# can be passed during Docker build as build time environment for application name
ARG spring_application_name

# can be passed during Docker build as build time environment for config name
ARG spring_cloud_config_name

# can be passed during Docker build as build time environment for context path
ARG server_servlet_context

# can be passed during Docker build as build time environment for biosdk zip url
ARG biosdk_zip_url

# can be passed during Docker build as build time environment for biosdk locak dir
ARG biosdk_local_dir=biosdk-client

# can be passed during Docker build as build time environment for sdk impl
ARG sdk_impl

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user=mosip
ARG container_user_group=mosip
ARG container_user_uid=1001
ARG container_user_gid=1001

# can be passed during Docker build as build time environment for label related addition to docker.
ARG SOURCE
ARG COMMIT_HASH
ARG COMMIT_ID
ARG BUILD_TIME
ARG service_context=/biosdk-service

# environment variable to pass github branch to pickup configuration from, at docker runtime 
ENV spring_config_label_env=${spring_config_label}
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}
# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}
# environment variable to pass spring application name, at docker runtime
ENV spring_application_name_env=${spring_application_name}
# environment variable to pass spring config name, at docker runtime
ENV spring_cloud_config_name_env=${spring_cloud_config_name}
# environment variable to pass spring context path , at docker runtime
ENV server_servlet_context_env=${server_servlet_context}
# environment variable to pass biosdk zip file path, at docker runtime
ENV biosdk_zip_file_path=${biosdk_zip_url}
# environment variable to pass biosdk local directory name, at docker runtime
ENV biosdk_local_dir_name=${biosdk_local_dir}

ENV service_context_env=${service_context}

#ENV biosdk_zip_url=$biosdk_zip_url

ENV biosdk_bioapi_impl=${sdk_impl}

# can be passed during Docker build as build time environment for label.
LABEL source=${SOURCE}
LABEL commit_hash=${COMMIT_HASH}
LABEL commit_id=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}

# set working directory for the user
WORKDIR /home/${container_user}

# copy jar and script files
ADD ./target/biosdk-services-*.jar biosdk-services.jar
ADD ./configure_biosdk.sh configure_biosdk.sh

#install packages create user and change permissions of jar and script files
RUN apt-get -y update \
&& apt-get install -y unzip sudo \
&& groupadd -g ${container_user_gid} ${container_user_group} \
&& useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/sh -m ${container_user} \
&& mkdir -p /home/${container_user}/additional_jars/ \
&& chmod 775 biosdk-services.jar \
&& chmod +x configure_biosdk.sh \
&& chown -R ${container_user}:${container_user} /home/${container_user}

# select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

# expose port and set entry point
EXPOSE 9099
ENTRYPOINT ["./configure_biosdk.sh"]

# command to run the application
CMD echo $biosdk_bioapi_impl ; \

java -Dloader.path="/home/${container_user}/additional_jars/" -Dbiosdk_bioapi_impl="${biosdk_bioapi_impl}" -Dlogging.level.root=${logging_level_root_env} -jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dspring.application.name="${spring_application_name_env}" -Dspring.cloud.config.name=${spring_cloud_config_name_env} -Dserver.servlet.context-path=${server_servlet_context_env} biosdk-services.jar
