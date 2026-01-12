package org.jenkinsci.pipeline

def call(Map config = [:]) {

    // Load configuration
    def cfg = readYaml text: libraryResource('vars/ansible-config.yml')

    node {
        stage('Checkout SCM') {
            checkout scm
        }

        stage('Clone Repository') {
            dir("${cfg.CODE_BASE_PATH}") {
                git branch: cfg.GIT_BRANCH,
                    url: cfg.GIT_REPO_URL,
                    credentialsId: cfg.GIT_CREDENTIALS_ID
            }
        }

        // User approval stage
        if (cfg.KEEP_APPROVAL_STAGE) {
            stage('User Approval') {
                input message: "Approve deployment to ${cfg.ENVIRONMENT} environment?"
            }
        }

        stage('Ansible Playbook Execution') {
            dir("${cfg.CODE_BASE_PATH}") {
                sh "ansible-playbook -i ${cfg.ANSIBLE_INVENTORY} ${cfg.ANSIBLE_PLAYBOOK}"
            }
        }

        stage('Post Actions') {
            slackSend(
                channel: cfg.SLACK_CHANNEL_NAME,
                tokenCredentialId: cfg.SLACK_CREDENTIAL_ID,
                message: "${cfg.ACTION_MESSAGE}"
            )
        }
    }
}
