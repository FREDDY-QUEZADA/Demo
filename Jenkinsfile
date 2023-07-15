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
    stage('Run Tests') {
       steps {
         script {
           def testsResults = [:] // Variable para almacenar los resultados de las pruebas
          // Subetapa OWASP Dependency-Check Vulnerabilities
           testsResults["OWASP Dependency-Check Vulnerabilities"] = sh returnStatus: true, script: 'mvn dependency-check:check'
          // Subetapa PMD SpotBugs
           testsResults["PMD SpotBugs"] = sh returnStatus: true, script: 'mvn pmd:pmd pmd:cpd spotbugs:spotbugs'
          // Registrar problemas de OWASP Dependency-Check
           recordIssues enabledForFailure: true, tools: dependencyCheck(pattern: 'target/dependency-check-report.xml')
          // Registrar problemas de SpotBugs
           recordIssues enabledForFailure: true, tools: spotBugs()
          // Registrar problemas de CPD
           recordIssues enabledForFailure: true, tools: cpd(pattern: '**/target/cpd.xml')
          // Registrar problemas de PMD
           recordIssues enabledForFailure: true, tools: pmdParser(pattern: '**/target/pmd.xml')
        // Verificar el estado de los resultados de las pruebas y marcar el build como fallido si alguna subetapa falla
           testsResults.each { subStage, result ->
             if (result != 0) {
               error("Fallo en la subetapa: $subStage")
             }
           }
         }
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
