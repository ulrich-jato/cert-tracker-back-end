terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "3.0.2"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

variable "SPRING_APP_VERSION" {
  type = string
}

# Null resource to stop and remove all containers
resource "null_resource" "stop_and_remove_containers" {
  provisioner "local-exec" {
    command = <<-EOT
      docker stop spring-app mysqldb || true
      docker rm spring-app mysqldb || true
    EOT
  }
}

data "external" "docker_network_exists" {
  program = ["bash", "-c", <<-EOT
    if docker network inspect spring-mysql-network >/dev/null 2>&1; then
      echo '{"result":"true"}'
    else
      echo '{"result":"false"}'
    fi
  EOT
  ]
}


resource "null_resource" "create_docker_network" {
  triggers = {
    network_exists = jsonencode(data.external.docker_network_exists.result)
  }

  provisioner "local-exec" {
    command = <<EOT
    if [ "${data.external.docker_network_exists.result["result"]}" == "false" ]; then
      docker network create spring-mysql-network
    else
      echo 'Network already exists'
    fi
  EOT
  }
}


resource "docker_image" "spring_app_image" {
  name = "spring-app:${var.SPRING_APP_VERSION}"

  build {
    context    = "."
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "mysqldb_image" {
  name = "mysql:latest"

  build {
    context    = "/mysql"
    dockerfile = "/mysql/Dockerfile"
  }
}

resource "docker_container" "spring_app" {
  name        = "spring-app"
  image       = docker_image.spring_app_image.name
  restart = "on-failure"
  max_retry_count = 2
  start = true
  ports {
    internal = 8081
    external = 8081
  }
#  networks_advanced {
#    name = docker_network.spring_mysql_network.name
#  }
  network_mode = "spring-mysql-network"
#  dynamic "networks_advanced" {
#    for_each = docker_network.spring_mysql_network
#    content {
#      name = networks_advanced.key
#    }
#  }
  env = [
    "SPRING_APP_VERSION=${var.SPRING_APP_VERSION}",
    "spring.datasource.url=jdbc:mysql://mysqld:3306/certificatetracker",
    "spring.datasource.username=devops",
    "spring.datasource.password=devops",
  ]
  depends_on  = [docker_container.mysqldb, null_resource.create_docker_network,null_resource.stop_and_remove_containers]
  volumes {
    host_path      = "/m2"
    container_path = "/root/.m2"
  }
#  network_mode = docker_network.spring_mysql_network.name
}

resource "docker_container" "mysqldb" {
  name        = "mysqldb"
  image       = docker_image.mysqldb_image.name
  restart = "on-failure"
  max_retry_count = 2
  start = true
  ports {
    internal = 3306
    external = 3307
  }
#  volumes {
#    host_path      = "/init-scripts/init-script.sql"
#    container_path = "/docker-entrypoint-initdb.d/init-script.sql"
#  }

  volumes {
    host_path      = "/mysql-data"
    container_path = "/var/lib/mysql"
  }

#  networks_advanced {
#    name = "spring-mysql-network"
#  }
  network_mode = "spring-mysql-network"
#  dynamic "networks_advanced" {
#    for_each = docker_network.spring_mysql_network
#    content {
#      name = networks_advanced.key
#    }
#  }
  env = [
    "MYSQL_DATABASE=certificatetracker",
    "MYSQL_USER=devops",
    "MYSQL_PASSWORD=devops",
    "MYSQL_ROOT_PASSWORD=devops"
  ]
  depends_on  = [null_resource.create_docker_network, null_resource.stop_and_remove_containers]

#  network_mode = docker_network.spring_mysql_network.name
}

# Volume definition
resource "docker_volume" "mysql_data" {
  name = "mysql-data"
}
