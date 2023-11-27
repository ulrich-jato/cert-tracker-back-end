pipeline {
    agent { label 'linux' }
    environment {
        containerRegistryCredentials = credentials('ARTIFACTORY_PUBLISH')
        containerRegistryURL = 'jato.jfrog.io'
        imageName = 'devops-hello'
        ARTIFACTORY_URL = 'jato.jfrog.io'
        ARTIFACTORY_REPO = 'docker'
        SPRING_APP_IMAGE_NAME = 'spring-app'
        MYSQL_IMAGE_NAME = 'mysql'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
         MYSQL_TAG = 'latest'
    }

    stages {
        stage('Environment Setup') {
            steps {
                script {
                    // Set the version number using the Jenkins BUILD_ID environment variable.
                    version = "1.0.${env.BUILD_ID}"
                    //artifactoryServer = Artifactory.server 'default'
                    //artifactoryDocker = Artifactory.docker server: artifactoryServer
                    //buildInfo = Artifactory.newBuildInfo()
                }
            }
        }
        stage('Docker Image') {
            steps {

                sh """
                    docker login -u ${containerRegistryCredentials_USR} -p ${containerRegistryCredentials_PSW} ${containerRegistryURL}
                    docker-compose build --no-cache
                    docker tag ${SPRING_APP_IMAGE_NAME} ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${version}
                    docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${version}
                    docker tag ${MYSQL_IMAGE_NAME} ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${MYSQL_IMAGE_NAME}:${MYSQL_TAG}
                    docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${MYSQL_IMAGE_NAME}:${MYSQL_TAG}
                   """
            }
        }
//         stage('Source') {
//             steps {
//                 git branch: 'main',
//                     url: 'https://github.com/LinkedInLearning/essential-jenkins-2468076.git'
//             }
//         }
//         stage('Clean') {
//             steps {
//                 dir("${env.WORKSPACE}/Ch04/04_02-ssh-agent"){
//                     sh 'mvn clean'
//                 }
//             }
//         }
//         stage('Test') {
//             steps {
//                 dir("${env.WORKSPACE}/Ch04/04_02-ssh-agent"){
//                     sh 'mvn test'
//                 }
//             }
//         }
//         stage('Package') {
//             steps {
//                 dir("${env.WORKSPACE}/Ch04/04_02-ssh-agent"){
//                     sh 'mvn package -DskipTests'
//                 }
//             }
//         }
    }
}