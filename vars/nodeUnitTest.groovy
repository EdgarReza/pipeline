// <a.galvez.malpartida@accenture.com>

def call() {

/**
*  this method no need params
*/

  sh """
    npm i npm@latest -g
    npm install
    npm install -g @angular/cli
    npm install -g karma
    npm install karma-junit-reporter --save-dev
    ng test --watch=false
  """
}
