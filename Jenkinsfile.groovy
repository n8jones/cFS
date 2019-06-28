node('docker-slave') {
  stage('checkout') {
    checkout scm
  }
  stage('Fortify') {
    sh 'mkdir build'
    dir('build'){
      withEnv(["CC=${WORKSPACE}/fortify_cc.sh"]) {
        sh 'cmake ../cfe'
        sh 'make all'
        sh 'make cpu1-all'
        sh 'make mission-all'
      }
    }
    sh "sourceanalyzer -b cfs -scan -f results_${BUILD_NUMBER}.fpr"
    archiveArtifacts "results_${BUILD_NUMBER}.fpr"
  }
  stage('Export CSV') {
    sh "FPRUtility -information -search -queryAll -project results_${BUILD_NUMBER}.fpr -listIssues -outputFormat CSV -f results_${BUILD_NUMBER}.csv"
    archiveArtifacts "results_${BUILD_NUMBER}.csv"
  }
}
