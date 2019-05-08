// <a.galvez.malpartida@accenture.com>

def call(Map nexusParams) {

/**
*  this method need below params:
*    nexusUrl = "nexus.devsecopsascode.com:8080"
*    nexusRepository = "maven-snapshots"
*    nexusCredentialId = "nexus"
*/

  def image   = readMavenPom().getArtifactId()
  def version = readMavenPom().getVersion()
  def group   = readMavenPom().getGroupId()
  def name    = readMavenPom().getName()

  nexusArtifactUploader(
      nexusVersion: 'nexus3',
      protocol: 'http',
      nexusUrl: "${nexusParams.nexusUrl}",
      groupId: "${group}",
      version: "${version}",
      repository: "${nexusParams.nexusRepository}",
      credentialsId: "${nexusParams.nexusCredentialId}",
      artifacts: [
          [artifactId: "${image}",
           classifier: '',
           file: "target/${name}" + '-' + "${version}" + '.jar',
           type: 'jar']
      ]
   )
}
