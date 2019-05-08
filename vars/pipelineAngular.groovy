// vars/pipelineAngular.groovy
// <a.galvez.malpartida@accenture.com>

// this pipeline need to be configure on 'environment' statement
/*
* Jenkinsfile configuration example
* @Library('pipeline-as-code') _
*
* pipelineAngular(branch: "${env.BRANCH_NAME}",
*                  build: 'false',
*               analysis: 'false',
*               artifact: 'false',
*                 docker: 'false',
*                 deploy: 'false',
*                    e2e: 'false',
*                  email: 'agmalpartida@gmail.com,a.galvez.malpartida@accenture.com'
*               )
*/

def call(Map pipelineParams) {
    pipeline {
        agent {
          kubernetes {
            label 'pipeline-as-code'
            defaultContainer 'jnlp'
            yaml """
              apiVersion: v1
              kind: Pod
              metadata:
                labels:
                  app: jenkins
              spec:
                nodeSelector:
                  kubernetes.io/hostname: aks-agentpool-34312959-0
                imagePullSecrets:
                  - name: gitlab-registry-images
                containers:
                   - name: node
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/node:testing
                     command:
                     - cat
                     tty: true
                   - name: sonar
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/sonar-scanner:master
                     command:
                     - cat
                     tty: true
                   - name: docker
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/docker:master
                     command:
                     - cat
                     tty: true
                     volumeMounts:
                     - name: dockersock
                       mountPath: "/var/run/docker.sock"
                   - name: kubectl
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/k8s-kubectl:master
                     command:
                     - cat
                     tty: true
                   - name: helm
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/k8s-helm:master
                     command:
                     - cat
                     tty: true
                   - name: selenium
                     image: registry.devsecopsascode.com/agmalpartida/pipeline-as-code/selenium:master
                     command:
                     - cat
                     tty: true
                volumes:
                - name: dockersock
                  hostPath:
                    path: "/var/run/docker.sock"
            """
          }
        }

        environment {
          AUTHOR = 'Alberto'
          // global
          build    = "${pipelineParams.build}"
          analysis = "${pipelineParams.analysis}"
          artifact = "${pipelineParams.artifact}"
          docker   = "${pipelineParams.docker}"
          deploy   = "${pipelineParams.deploy}"
          e2e      = "${pipelineParams.e2e}"
          email    = "${pipelineParams.email}"

          gitBranch      = "${pipelineParams.branch}"
          gitCommit      = "${env.GIT_COMMIT}"
          shortGitCommit = "${gitCommit[0..10]}"
          // this variable get the last tag on the branch
          release        = sh(returnStdout: true, script: 'git tag | head -1').trim()

          /*
          * these variables must be configured
          */
          // sonar
          key  = "angular:angular-${env.BRANCH_NAME}"
          name = "angular-${env.BRANCH_NAME}"
          file = "sonar-project.properties"

          // docker
          dockerfile    = ".deploy/Dockerfile"
          imageName     = "agmalpartida/angular-aplication:${gitBranch}"
          registry      = 'https://registry.devsecopsascode.com'
          credentialsId = 'dockerhub'

          // k8s deploy
          appName        = "app-angular-${gitBranch}"
          appChart       = ".deploy/helm"
          helmAppVersion = "1"

          // nexus
          nexusUrl          = "nexus.devsecopsascode.com:8080"
          nexusRepository   = "angular"
          nexusCredentialId = "nexus"
        }
        stages {
          stage('Code Analysis') {
            when {
              environment name: 'analysis', value: 'true'
            }
            parallel {
              stage ('Unit Test') {
                steps {
                  container('node') {
                    echo "${pipelineParams.branch}"
                    sh 'node --version'
                    echo "${AUTHOR}"
                    nodeUnitTest()
                  }
                }
              }
              stage ('SonarQube Analysis') {
                steps {
                  container('sonar') {
                    sonarQube(key, name, file)
                  }
                }
              }
            }
          }
          stage('Build') {
            when {
              environment name: 'build', value: 'true'
            }
            steps {
              container('node') {
                nodeBuild()
              }
            }
          }
          stage('Upload Artifact') {
            when {
              environment name: 'artifact', value: 'true'
            }
            steps {
              container('node') {
                nexusNodePublishArtifact(nexusUrl: "${nexusUrl}", nexusRepository: "${nexusRepository}", \
                  nexusCredentialId: "${nexusCredentialId}", release: "${release}")
              }
            }
          }
          stage('Create Docker Image') {
            when {
              environment name: 'docker', value: 'true'
            }
            steps {
              container('docker') {
                dockerImageCreate(dockerfile: "${dockerfile}", imageName: "${imageName}", \
                  registry: "${registry}", credentialsId: "${credentialsId}", gitBranch: "${gitBranch}", \
                  shortGitCommit: "${shortGitCommit}")
              }
            }
          }
          stage('Deploy to Kubernetes') {
            when {
              environment name: 'deploy', value: 'true'
            }
            steps {
              container('helm') {
                k8sHelmDeploy(appName: "${appName}", appChart: "${appChart}", \
                  helmAppVersion: "${helmAppVersion}", release: "${release}", gitBranch: "${gitBranch}")
              }
            }
          }
          stage('E2E Test') {
            when {
              environment name: 'e2e', value: 'true'
            }
            steps {
              container('selenium') {
                nodeProtractorTest()
              }
            }
          }
        }

        post {
            success {
                mail to: "${email}",
                from: "jenkins@devsecopsascode.com",
                subject: "Successful Pipeline: ${currentBuild.fullDisplayName}",
                body: "Successful build completed ${env.BUILD_URL}"
            }
            failure {
                mail to: "${email}",
                from: "jenkins@devsecopsascode.com",
                subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                body: "Something is wrong with ${env.BUILD_URL}"
            }
        }
    }
}
