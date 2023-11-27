pipeline {
    agent { label 'linux' }
    environment {
        containerRegistryCredentials = credentials('ARTIFACTORY_PUBLISH')
        containerRegistryURL = 'jato.jfrog.io'
        imageName = 'devops-hello'
        ARTIFACTORY_URL = 'jato.jfrog.io'
        ARTIFACTORY_REPO = 'docker'
        SPRING_APP_IMAGE_NAME = 'spring-app'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        version = "1.0.${env.BUILD_ID}"
        SPRING_APP_VERSION = "${env.version}"
    }

    stages {
//         stage('Environment Setup') {
//             steps {
//                 script {
//                     // Set the version number using the Jenkins BUILD_ID environment variable.
//                     //version = "1.0.${env.BUILD_ID}"
//                     //artifactoryServer = Artifactory.server 'default'
//                     //artifactoryDocker = Artifactory.docker server: artifactoryServer
//                     //buildInfo = Artifactory.newBuildInfo()
//                 }
//             }
//         }
        stage('Build Docker Image') {
            steps {

                sh """
                    docker login -u ${containerRegistryCredentials_USR} -p ${containerRegistryCredentials_PSW} ${containerRegistryURL}
                    docker-compose build --build-arg SPRING_APP_VERSION=${env.version}
                    docker tag ${SPRING_APP_IMAGE_NAME}:${env.version} ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${env.version}
                    docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${SPRING_APP_IMAGE_NAME}:${env.version}
                   """
            }
        }
       stage("Deploy to Dev") {
           steps {
               script {
                   def currentBranch = env.BRANCH_NAME
                   echo "Current Branch: ${currentBranch}"

                   // Add more conditions if needed
                   //if (currentBranch == 'main') {
                       sh "docker-compose -f ${DOCKER_COMPOSE_FILE} down"
                       sh "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
                   //} else {
                   //    echo "Skipping deploy for branch: ${currentBranch}"
                   //}
               }
           }
       }
    }

    post {
       // Command to run always, here we set the initial value for variable resultString to 'None'.
       always {
            script {
                 resultString = "None"
            }
       }
       success {
            script {
                 resultString = "Success"
            }
       }
       unstable {
            script {
                 resultString = "Unstable"
            }
       }
       failure {
            script {
                 resultString = "Failure"
            }
       }
       cleanup {
            // Commands to run during cleanup (after all post steps) go here.
            script {
            // Send an email notification with build result details to recipient providers only if the pipeline does not succeed.
            if (resultString != 'Success') {
                 emailext body: "<p>See build result details at: <a href='${env.JOB_URL}'>${env.JOB_URL}</a></p>",
                 mimeType: 'text/html; charset=UTF-8',
                 recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider'], [$class: 'UpstreamComitterRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                 replyTo: "ulrichjato@yahoo.fr"
                 subject: "${currentBuild.fullDisplayName} ${resultString}"
                 }

                  // clean the workspace after the build.
                  cleanWs()

                  // Retrieve the list of images with the same name.
                  images = sh(
                      returnStdout: true,
                      script: "docker images -q ${SPRING_APP_IMAGE_NAME}"
                      ).trim().split('\n')

                      // Iterate through the images and remove older docker images.
                  if (env.BRANCH_NAME == null){
                      for (image in images) {
                          img_version = sh(
                              returnStdout: true,
                              script: "docker image inspect --format='{{index .RepoTags 0}}' ${image} | cut -d ':' -f 2"
                          ).trim()
                          if (img_version != version) {
                              sh "docker rmi -f ${image}"
                          }
                      }
                  }
            }
       }
    }
}