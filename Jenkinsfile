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
         MYSQL_TAG = '8.0'
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
        stage('Build Docker Image') {
            steps {

                sh """
                    docker login -u ${containerRegistryCredentials_USR} -p ${containerRegistryCredentials_PSW} ${containerRegistryURL}
                    docker-compose build --no-cache
                    docker tag ${SPRING_APP_IMAGE_NAME} ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${version}
                    docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${version}
                   """
            }
        }
       stage("Deploy to Dev") {
           steps {
               script {
                   def currentBranch = env.BRANCH_NAME
                   echo "Current Branch: ${currentBranch}"

                   // Add more conditions if needed
                   if (currentBranch == 'main') {
                       sh "docker-compose -f ${DOCKER_COMPOSE_FILE} down"
                       sh "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
                   } else {
                       echo "Skipping deploy for branch: ${currentBranch}"
                   }
               }
           }
       }
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