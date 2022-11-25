pipeline {
    agent any

    tools {
        maven 'apache-maven-latest'
        jdk 'openjdk-jdk11-latest'
    }

    environment {
        EMAIL_TO = "ndoschek+eclipseci@eclipsesource.com, eneufeld+eclipseci@eclipsesource.com"
    }

    stages {
        stage ('Build: Plain Maven (M2)') {
            steps {
                // ignore test failures since we parse the test results afterwards
                timeout(30) {
                    sh './mvnw clean verify -Pm2 -B -Dmaven.test.failure.ignore=true'
                } 
            }
        }
        
        stage ('Build: Eclipse-based (P2)') {
            steps {
                // ignore test failures since we parse the test results afterwards
                timeout(30) {
                    sh './mvnw clean verify -Pp2 -B -Dmaven.test.failure.ignore=true' 
                }
            }
        }

        stage('Deploy') {
            when { branch 'master' }
            steps {
            	parallel(
            	    p2: {
            	        build job: 'deploy-emfcloud-modelserver-p2', wait: false
            	    },
            	    m2: {
            	        build job: 'deploy-emfcloud-modelserver-m2', wait: false
            	    }
            	)
            }
        }
    }

     post {
        always {
                // Record & publish checkstyle issues
            recordIssues  enabledForFailure: true, publishAllIssues: true,
            tool: checkStyle(reportEncoding: 'UTF-8'),
            qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]

            // Record & publish test results
            withChecks('Tests') {
                junit 'tests/**/surefire-reports/*.xml'
            }

            // Record maven,java warnings
            recordIssues enabledForFailure: true, skipPublishingChecks:true, tools: [mavenConsole(), java()]
        }
        failure {
            script {
                if (env.BRANCH_NAME == 'master') {
                    echo "Build result FAILURE: Send email notification to ${EMAIL_TO}"
                    emailext attachLog: true,
                    body: 'Job: ${JOB_NAME}<br>Build Number: ${BUILD_NUMBER}<br>Build URL: ${BUILD_URL}',
                    mimeType: 'text/html', subject: 'Build ${JOB_NAME} (#${BUILD_NUMBER}) FAILURE', to: "${EMAIL_TO}"
                }
            }
        }
        unstable {
            script {
                if (env.BRANCH_NAME == 'master') {
                    echo "Build result UNSTABLE: Send email notification to ${EMAIL_TO}"
                    emailext attachLog: true,
                    body: 'Job: ${JOB_NAME}<br>Build Number: ${BUILD_NUMBER}<br>Build URL: ${BUILD_URL}',
                    mimeType: 'text/html', subject: 'Build ${JOB_NAME} (#${BUILD_NUMBER}) UNSTABLE', to: "${EMAIL_TO}"
                }
            }
        }
        fixed {
            script {
                if (env.BRANCH_NAME == 'master') {
                    echo "Build back to normal: Send email notification to ${EMAIL_TO}"
                    emailext attachLog: false,
                    body: 'Job: ${JOB_NAME}<br>Build Number: ${BUILD_NUMBER}<br>Build URL: ${BUILD_URL}',
                    mimeType: 'text/html', subject: 'Build ${JOB_NAME} back to normal (#${BUILD_NUMBER})', to: "${EMAIL_TO}"
                }
            }
        }
    }
}
