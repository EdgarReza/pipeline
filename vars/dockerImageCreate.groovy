// <a.galvez.malpartida@accenture.com>

def call(Map dockerParams) {

/**
* this method need below params:
*   dockerfile    = ".deploy/Dockerfile"
*   imageName     = "agmalpartida/testing:${gitBranch}"
*   registry      = 'https://registry.devsecopsascode.com'
*   credentialsId = 'dockerhub'
*/

  def app =  docker.build("${dockerParams.imageName}", "-f ${dockerParams.dockerfile} $WORKSPACE")

  sh """
    sed -i.bak -e "s/master/${dockerParams.gitBranch}/" "${dockerParams.dockerfile}"
    pwd
  """

  docker.withRegistry("${dockerParams.registry}", "${dockerParams.credentialsId}") {
    app.push "${dockerParams.gitBranch}-${dockerParams.shortGitCommit}"
    app.push "${dockerParams.gitBranch}"
  }
}
