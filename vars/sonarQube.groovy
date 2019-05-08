// <a.galvez.malpartida@accenture.com>

def call(def key, def name, def file) {

/**
*  this method need below params:
*    key  = "angular:angular-${env.BRANCH_NAME}"
*    name = "angular-${env.BRANCH_NAME}"
*    file = "sonar-project.properties"
*/

  scannerHome = tool 'SonarQubeScanner'

  withSonarQubeEnv('Sonar') {
    sh """
      sed -i -e "s/\\(^sonar\\.projectKey=\\).*/\\1${key}/" ${file}
      sed -i -e "s/\\(^sonar\\.projectName=\\).*/\\1${name}/" ${file}
      cat sonar-project.properties
      ${scannerHome}/bin/sonar-scanner -Dproject.settings=${file}
    """
  }
  timeout(time: 10, unit: 'MINUTES') {
      waitForQualityGate abortPipeline: true
  }
}
