#!/bin/bash

set -e

# Function to wait for MySQL to be ready
wait_for_mysql() {
    until mysql  -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW DATABASES;"; do
        echo "MySQL is not ready yet. Retrying in 1 second..."
        sleep 1
    done
}

# Wait for MySQL to be ready
wait_for_mysql

# Execute the SQL initialization script
echo "Executing init-script.sql..."
mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" < /docker-entrypoint-initdb.d/init-script.sql

echo "Initialization complete."
