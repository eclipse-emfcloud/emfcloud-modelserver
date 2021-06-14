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
                sh 'mvn clean verify -Pm2 -B -Dmaven.test.failure.ignore=true' 
            }
        }
        
        stage ('Build: Eclipse-based (P2)') {
            steps {
                // ignore test failures since we parse the test results afterwards
                sh 'mvn clean verify -Pp2 -B -Dmaven.test.failure.ignore=true' 
            }
        }
        
        stage ('Generate: Reports') {
            steps {
                junit '**/surefire-reports/*.xml'
                recordIssues failOnError: true, qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
                tools: [checkStyle(pattern: '**/target/checkstyle-result.xml', reportEncoding: 'UTF-8')]           
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
}
