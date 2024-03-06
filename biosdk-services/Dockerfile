FROM openjdk:11

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label
ARG active_profile
ARG spring_config_url
ARG spring_application_name
ARG spring_cloud_config_name
ARG server_servlet_context
ARG biosdk_zip_url
ARG biosdk_local_dir=biosdk-client
ARG sdk_impl
ARG container_user=mosip
ARG container_user_group=mosip
ARG container_user_uid=1001
ARG container_user_gid=1001
ARG loader_path=/home/${container_user}/additional_jars/
ARG logging_level_root=INFO

# environment variables for runtime
ENV spring_config_label_env=${spring_config_label}
ENV active_profile_env=${active_profile}
ENV spring_config_url_env=${spring_config_url}
ENV spring_application_name_env=${spring_application_name}
ENV spring_cloud_config_name_env=${spring_cloud_config_name}
ENV server_servlet_context_env=${server_servlet_context}
ENV biosdk_zip_file_path=${biosdk_zip_url}
ENV biosdk_local_dir_name=${biosdk_local_dir}
ENV biosdk_bioapi_impl=${sdk_impl}
ENV work_dir=/home/${container_user}
ENV loader_path_env=${loader_path}
ENV logging_level_root_env=${logging_level_root}

# install packages, create user, set permissions, and set working directory
RUN apt-get -y update \
    && apt-get install -y unzip sudo \
    && groupadd -g ${container_user_gid} ${container_user_group} \
    && useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/sh -m ${container_user} \
    && mkdir -p ${loader_path} \
    && chown -R ${container_user}:${container_user} /home/${container_user}

# copy necessary files
COPY ./target/biosdk-services-*.jar /home/${container_user}/biosdk-services.jar
COPY ./configure_biosdk.sh /home/${container_user}/configure_biosdk.sh

# set permissions for files
RUN chmod 775 /home/${container_user}/biosdk-services.jar \
    && chmod +x /home/${container_user}/configure_biosdk.sh

# set working directory
WORKDIR /home/${container_user}

# select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

# expose port and set entrypoint with command
EXPOSE 9099
ENTRYPOINT [ "./configure_biosdk.sh" ]
CMD echo $biosdk_bioapi_impl ; \
    java -Dloader.path="${loader_path_env}" -Dbiosdk_bioapi_impl="${biosdk_bioapi_impl}" -Dlogging.level.root=${logging_level_root_env} -jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dspring.application.name="${spring_application_name_env}" -Dspring.cloud.config.name=${spring_cloud_config_name_env} -Dserver.servlet.context-path=${server_servlet_context_env} biosdk-services.jar
