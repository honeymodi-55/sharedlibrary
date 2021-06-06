def call(Map config = [:], body){

    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
    agent any 
    stages {
        stage ('deploy terraform') {
            steps { 
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: "jenkins-terraform-user",
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: "AWS_SECRET_ACCESS_KEY"
                ]]){
                    sh 'terraform init -force-copy'
                    sh 'echo ${config.access_key}'
                    sh 'terraform plan -var ${config.access_key}=${AWS_ACCESS_KEY_ID} -var ${config.secret_key}=${AWS_SECRET_ACCESS_KEY} -out Outputforplan'
                    sh 'terraform apply -input=false Outputforplan'
                }
                }
            }
        }
    }
}