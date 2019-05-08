// <a.galvez.malpartida@accenture.com>

def call() {

/**
*  this method no need params
*  the docker image that you use should be the below packages:
*    sudo apt-get update
*    sudo apt-get install -y curl software-properties-common
*    curl -sL https://deb.nodesource.com/setup_11.x | sudo bash -
*    sudo apt-get install -y nodejs
*/

  sh """
    java -jar /opt/selenium/selenium-server-standalone.jar &
    sudo npm install
    sudo npm install -g @angular/cli
    sudo npm install -g karma
    sudo npm install karma-junit-reporter --save-dev
    sudo npm install --save-dev jasmine-reporters@^2.0.0
    sudo npm install -g protractor
    sudo protractor --version
    sudo protractor conf.js
  """
}
