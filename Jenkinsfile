pipeline {
    agent any
    environment {
        // Define Java home directory
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17'  // Update this path based on your Java installation
        PATH = "${JAVA_HOME}\\bin;${env.PATH}"  // Add Java to the PATH and retain existing PATH
        
        AZURE_APP_NAME = 'eshwar-test-01' // Replace with your Azure Web App name
        AZURE_RESOURCE_GROUP = 'eshwar' // Replace with your resource group
        // Replace with your actual Azure WebApp name 
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from your Git repository and specify the branch
                git branch: 'main', url: 'https://github.com/EshwarBashaBathini/lokesh-backend.git', credentialsId: '2cfb3354-b0cc-4c3f-890f-b339640f685f'
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    // Install dependencies using Maven (or Gradle if you're using it)
                    bat 'mvn clean install'  // For Maven-based projects
                    // bat 'gradle build' // For Gradle-based projects
                }
            }
        }

         // Manager Approval Stage
          stage('Manager Approval') {
            steps {
                script {
                    // Send email to manager notifying them about the approval request
                    emailext(
                        subject: "Approval Request for Deployment",
                        body: """Please review the Building Logs and Approve or Reject the deployment for the app.
                        Kindly Approve or Reject the Deployment via the Jenkins interface.""",
                        to: "eshwar@middlewaretalents.com",  // Manager's email
                        from: 'eshwar.bashabathini88@gmail.com',  // Your email (Gmail, if using SMTP correctly configured)
                        replyTo: 'eshwar.bashabathini88@gmail.com',  // Reply-to address
                        attachLog: true  // Optionally attaches the build log
                    )
                }
                
                // Requesting manager approval via Jenkins input
                script {
                    // Collecting approval decision from the manager
                    def approval = input(
                        message: 'Do you approve the deployment to Azure?', 
                        parameters: [
                            choice(name: 'Approval', choices: ['Approve', 'Reject'], description: 'Manager Approval')
                        ]
                    )

                    // Print the decision for debugging purposes
                    echo "Manager's Approval status 01: ${approval}"

                    // Assign the value of approval to the variable
                    env.APPROVAL_STATUS = approval
                }
            }
        } 

        stage('Deploying to Azure ') {
            steps {
                script {
                    if (env.APPROVAL_STATUS == 'Reject') {
                        echo "Deployment was rejected by the manager. Skipping deployment."
                    } else if (env.APPROVAL_STATUS == 'Approve') {
                        // Log in using service principal
                        script {
                            withCredentials([  // Access Azure secrets from GitHub's secret manager (Jenkins Credentials)
                                string(credentialsId: 'CLIENT_ID', variable: 'CLIENT_ID'), 
                                string(credentialsId: 'CLIENT_PASSWORD', variable: 'CLIENT_PASSWORD'), 
                                string(credentialsId: 'TENANT_ID', variable: 'TENANT_ID') 
                            ]) {
                                // Azure login using service principal
                                bat """
                                    az login --service-principal -u ${CLIENT_ID} -p ${CLIENT_PASSWORD} --tenant ${TENANT_ID}
                                """
                            }
                        }
                        script {
                            // Ensure you have set up the Azure CLI plugin or other necessary Azure credentials
                            // Example: Using Azure CLI to deploy the ZIP file to Azure Web App
                            bat '''
                            az webapp deploy --resource-group eshwar --name eshwar-test-01  --src-path target/ems-backend-0.0.1-SNAPSHOT.jar
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            // Clean up or other post-deployment steps
            echo 'Backend Deployment Process Finished.'
        }
    }
}
