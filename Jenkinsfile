pipeline {
    agent any

    tools {
        maven 'apache-maven-latest'
        jdk 'openjdk-jdk11-latest'
    }

    stages {
        stage ('Build: Plain Maven (M2)') {
            steps {
                // ignore test failures since we parse the test results afterwards
                timeout(30) {
                    sh 'mvnw clean verify -Pm2 -B -Dmaven.test.failure.ignore=true'
                } 
            }
        }
        
        stage ('Build: Eclipse-based (P2)') {
            steps {
                // ignore test failures since we parse the test results afterwards
                timeout(30) {
                    sh 'mvnw clean verify -Pp2 -B -Dmaven.test.failure.ignore=true' 
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
    }
}
