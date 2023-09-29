FROM openjdk:11

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL
ARG spring_config_url

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}
# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}

ARG biosdk_zip_url

ENV biosdk_zip_file_path=${biosdk_zip_url}

ARG biosdk_local_dir=biosdk-client

ENV biosdk_local_dir_name=${biosdk_local_dir}

ARG sdk_impl

ARG service_context=/biosdk-service

ENV service_context_env=${service_context}

#ENV biosdk_zip_url=$biosdk_zip_url
ENV biosdk_bioapi_impl=${sdk_impl}

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user=mosip

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_group=mosip

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_uid=1001

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_gid=1001

# install packages and create user
RUN apt-get -y update \
&& apt-get install -y unzip sudo\
&& groupadd -g ${container_user_gid} ${container_user_group} \
&& useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/sh -m ${container_user}

# set working directory for the user
WORKDIR /home/${container_user}

ENV work_dir=/home/${container_user}

ARG loader_path=${work_dir}/additional_jars/

RUN mkdir -p ${loader_path}

ENV loader_path_env=${loader_path}

ARG logging_level_root=INFO

ENV logging_level_root_env=${logging_level_root}

ADD ./target/biosdk-services-*.jar biosdk-services.jar

ADD ./configure_biosdk.sh configure_biosdk.sh

RUN chmod 775 biosdk-services.jar

RUN chmod +x configure_biosdk.sh

# change permissions of file inside working dir
RUN chown -R ${container_user}:${container_user} /home/${container_user}

# select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

EXPOSE 9099

ENTRYPOINT [ "./configure_biosdk.sh" ]

CMD echo $biosdk_bioapi_impl ; \
java -Dloader.path="${loader_path_env}" -Dbiosdk_bioapi_impl="${biosdk_bioapi_impl}" -Dserver.servlet.context-path="${service_context_env}" -Dlogging.level.root=${logging_level_root_env} -jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}"  -Dspring.cloud.config.uri="${spring_config_url_env}"  biosdk-services.jar