// <a.galvez.malpartida@accenture.com>

def call() {

/**
*  this method no need params
*/

  sh """
    mvn test
    ls `pwd`/target
  """
}
