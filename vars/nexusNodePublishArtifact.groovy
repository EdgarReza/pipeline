// <a.galvez.malpartida@accenture.com>

def call(Map nexusParams) {

/**
*  this method need below params:
*    nexusUrl          = "nexus.devsecopsascode.com:8080"
*    nexusRepository   = "api"
*    nexusCredentialId = "nexus"
*    release           = "0.0.1"
*/

  sh """
    cd dist
    mkdir artifacts
    echo "Packing artifact."
    tar cf "artifacts/artifact.tar" --exclude .git .
    echo "Artifact packed"
  """
  nexusArtifactUploader(
      nexusVersion: 'nexus3',
      protocol: 'http',
      nexusUrl: "${nexusParams.nexusUrl}",
      version: "${nexusParams.release}",
      repository: "${nexusParams.nexusRepository}",
      credentialsId: "${nexusParams.nexusCredentialId}",
      artifacts: [
          [artifactId: "artifact",
           classifier: '',
           file: "dist/artifacts/artifact" + '.tar',
           type: 'tar']
      ]
   )
}
