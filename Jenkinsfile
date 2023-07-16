pipeline {
  agent any

  tools {
    jdk 'jdk-11'
    maven 'mvn-3.6.3'
  }
  stages {
  stage('Build') {
      steps {
        script {
          def mvnHome = tool 'mvn-3.6.3'
          def maven = "${mvnHome}/bin/mvn" 
          withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
            sh "${maven} package"
          }
        }
      }
    }
   stage ('OWASP Dependency-Check Vulnerabilities') {
   steps {
     dependencyCheck additionalArguments: ''' 
     -o "./" 
     -s "./"
     -f "ALL" 
     --prettyPrint''', odcInstallation: 'OWASP Dependency-Check'
     dependencyCheckPublisher pattern: 'dependency-check-report.xml'
   }
 }
 
    stage ('PMD SpotBugs') {
      steps {
        withMaven(maven: 'mvn-3.6.3') {
          sh 'mvn pmd:pmd pmd:cpd spotbugs:spotbugs'
        }
        recordIssues(enabledForFailure: true, tools: [spotBugs(), cpd(pattern: '**/target/cpd.xml'), pmdParser(pattern: '**/target/pmd.xml')])
      }
    }
    stage ('ZAP') {
      steps {
        withMaven(maven : 'mvn-3.6.3') {
          sh 'mvn zap:analyze'
          publishHTML (target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/zap-reports',
                reportFiles: 'zapReport.html',
                reportName: "ZAP report"
              ])
        }
      }
    }

    stage('SonarQube analysis') {
      steps {
        withSonarQubeEnv(credentialsId: 'sonarqube-secret', installationName: 'sonarqube-server') {
          withMaven(maven : 'mvn-3.6.3') {
            sh 'mvn sonar:sonar -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.xmlReportPath=target/dependency-check-report.xml -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.java.pmd.reportPaths=target/pmd.xml -Dsonar.java.spotbugs.reportPaths=target/spotbugsXml.xml -Dsonar.zaproxy.reportPath=target/zap-reports/zapReport.xml -Dsonar.zaproxy.htmlReportPath=target/zap-reports/zapReport.html'
          }
        }
      }
    }
  }
}
    
    


