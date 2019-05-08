// <a.galvez.malpartida@accenture.com>

def call(Map helmParams) {

/**
*  this method need below params:
*    appName        = "app-angular-${gitBranch}"
*    appChart       = ".deploy/helm"
*    helmAppVersion = "1.0"
*    release        = "0.0.1"
*/

  sh """
    sed -i -e "s/\\(tag:\\).*/\\1 ${helmParams.gitBranch}/" ${helmParams.appChart}/values.yaml
    sed -i -e "s/\\(^appVersion:\\).*/\\1 ${helmParams.helmAppVersion}/" ${helmParams.appChart}/Chart.yaml
    sed -i -e "s/\\(^name:\\).*/\\1 ${helmParams.appName}/" ${helmParams.appChart}/Chart.yaml
    sed -i -e "s/\\(^version:\\).*/\\1 ${helmParams.release}/" ${helmParams.appChart}/Chart.yaml
    cat ${helmParams.appChart}/values.yaml
    cat ${helmParams.appChart}/Chart.yaml
    helm upgrade -i --recreate-pods ${helmParams.appName} ${helmParams.appChart}
    helm list
    ls
    pwd
  """
}
