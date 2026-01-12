def call() {

    def config = readYaml text: libraryResource('ansible-config.yml')

    pipeline {
        agent any

        stages {

            stage('Clone Repository') {
                steps {
                    git branch: config.GIT_BRANCH,
                        url: config.GIT_REPO_URL
                }
            }

            stage('User Approval') {
                when {
                    expression { config.KEEP_APPROVAL_STAGE == true }
                }
                steps {
                    input message: "Approve deployment to ${config.ENVIRONMENT} environment?"
                }
            }

            stage('Ansible Playbook Execution') {
                steps {
                    dir(config.CODE_BASE_PATH) {
                        sh """
                        ansible-playbook \
                        -i ${config.ANSIBLE_INVENTORY} \
                        ${config.ANSIBLE_PLAYBOOK}
                        """
                    }
                }
            }
        }

        post {
            success {
                slackSend(
                    channel: config.SLACK_CHANNEL_NAME,
                    message: "✅ SUCCESS: ${config.ACTION_MESSAGE} on ${config.ENVIRONMENT}"
                )
            }
            failure {
                slackSend(
                    channel: config.SLACK_CHANNEL_NAME,
                    message: "❌ FAILED: ${config.ACTION_MESSAGE} on ${config.ENVIRONMENT}"
                )
            }
        }
    }
}
