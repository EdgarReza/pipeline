// <a.galvez.malpartida@accenture.com>

def call() {

/**
*  this method no need params
*/

  sh """
    mvn compile
    mvn -B -DskipTests clean package
  """
}
