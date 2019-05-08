// <a.galvez.malpartida@accenture.com>

def call(def fileSoap) {

/**
*  this method need below params:
*    fileSoap = "REST-Project-1-readyapi-project.xml"
*/

  sh """
    /opt/SoapUI/bin/testrunner.sh -J ${fileSoap}
  """
}
