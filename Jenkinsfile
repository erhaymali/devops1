pipeline {
    agent any
    tools {
        maven 'M3'
        jdk 'JDK21'
    }
    environment {
        SONAR_URL     = 'http://192.168.50.4:9000'
        PROJECT_KEY   = 'timesheet-sec'
        SONAR_TOKEN   = credentials('sonarqube')
        DEP_CHECK_XML = 'target/dependency-check-report.xml'
    }
    stages {
        stage('1-GIT') {
            steps {
                git branch: 'main',
                    changelog: false,
                    credentialsId: 'jenkins-github',
                    url: 'https://github.com/erhaymali/devops1.git'
            }
        }
        stage('2-MVN-clean') {
            steps { sh 'mvn -B -DskipTests clean' }
        }
        stage('3-compile') {
            steps { sh 'mvn clean compile' }
        }
        stage('4-SAST-SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN -Dsonar.projectKey=$PROJECT_KEY'
                }
            }
        }
        stage('5-SCA-OWASP') {
            steps { sh 'mvn org.owasp:dependency-check-maven:check' }
            post {
                always {
                    dependencyCheck pattern: "${DEP_CHECK_XML}"
                    dependencyCheckPublisher pattern: "${DEP_CHECK_XML}", failedTotalCritical: 1
                    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true,
                                 reportDir: 'target', reportFiles: 'dependency-check-report.html',
                                 reportName: 'OWASP-Dependency-Check'])
                }
            }
        }
        stage('6-Secrets-Gitleaks') {
            steps {
                sh 'docker run --rm -v "$WORKSPACE:/src" zricethezav/gitleaks:latest detect --source /src --verbose --report-format json --report-path gitleaks.json'
                archiveArtifacts artifacts: 'gitleaks.json', allowEmptyArchive: true
            }
        }
        stage('7-Docker-Build-Scan') {
            steps {
                script {
                    def img = docker.build("timesheet:${env.BUILD_ID}")
                    sh "trivy image --exit-code 1 --severity HIGH,CRITICAL timesheet:${env.BUILD_ID}"
                }
            }
        }
        stage('8-Quality-Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
    post {
        failure {
            emailext subject: "[FAIL] ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: 'Voir logs joints.', attachLog: true,
                     to: 'dev-team@example.com'
        }
    }
}
