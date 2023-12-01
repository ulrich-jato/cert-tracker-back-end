# Dockerfile for MySQL
# Use the official MySQL image as the base image
FROM mysql:latest

# Copy custom initialization script
COPY ./init-scripts/init-script.sql /docker-entrypoint-initdb.d/init-script.sql

# Set permissions for the script and ensure it is owned by the MySQL user
RUN chmod 755 /docker-entrypoint-initdb.d/init-script.sql \
    && chown -R mysql:mysql /docker-entrypoint-initdb.d/
