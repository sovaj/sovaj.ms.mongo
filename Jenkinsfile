 node {
  stage 'Build and Test'
  checkout scm
  sh 'mvn clean package'
  sh 'mvn deploy'
 }
