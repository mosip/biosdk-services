FROM mosipdev/openjdk-21-jre:latest

# label to be assigned along with  Docker build [Mandatory]
ARG SOURCE
ARG COMMIT_HASH
ARG COMMIT_ID
ARG BUILD_TIME
LABEL source=${SOURCE}
LABEL commit_hash=${COMMIT_HASH}
LABEL commit_id=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL
ARG spring_config_url

# can be passed during Docker build as build time environment for config server name
ARG spring_cloud_config_name=application,biosdk-service

# can be passed during Docker build as build time environment for spring application name
ARG spring_application_name=biosdk-service

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}
# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}
# environment variable to pass spring config server name, at docker runtime
ENV spring_cloud_config_name_env=${spring_cloud_config_name}
# environment variable to pass spring application name, at docker runtime
ENV spring_application_name_env=${spring_application_name}

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

# set working directory for the user
WORKDIR /home/${container_user}

ENV work_dir=/home/${container_user}

ARG loader_path=${work_dir}/additional_jars/

ENV loader_path_env=${loader_path}

ARG logging_level_root=INFO

ENV logging_level_root_env=${logging_level_root}

# install packages and create user for ubunutu latest
RUN apt-get update && \
    apt-get install -y --no-install-recommends sudo && \
    groupadd -g ${container_user_gid} ${container_user_group} && \
    useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/bash -m ${container_user} && \
    mkdir -p /home/${container_user} ${loader_path}
    

ADD configure_biosdk.sh /home/${container_user}/configure_biosdk.sh

ADD ./target/biosdk-services-*.jar biosdk-services.jar

RUN chmod +x /home/${container_user}/configure_biosdk.sh \
    && chmod a-w /home/${container_user}/configure_biosdk.sh \
    && chmod 775 biosdk-services.jar \
    && chown -R ${container_user}:${container_user} /home/${container_user}

# select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

EXPOSE 9099

ENTRYPOINT ["./configure_biosdk.sh"]

CMD echo $biosdk_bioapi_impl ; \
java -Dloader.path="${loader_path_env}" -Dserver.servlet.context-path="${service_context_env}" -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}"  -Dspring.cloud.config.uri="${spring_config_url_env}" -Dspring.application.name="${spring_application_name_env}" -Dspring.cloud.config.name="${spring_cloud_config_name_env}" --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect.DirectMethodHandleAccessor=ALL-UNNAMED -jar biosdk-services.jar
