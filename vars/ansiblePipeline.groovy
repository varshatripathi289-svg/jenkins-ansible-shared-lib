def call() {

    def config = readYaml text: libraryResource('ansible-config.yml')

    pipeline {
        agent any

        environment {
            SLACK_CHANNEL = config.SLACK_CHANNEL_NAME
            ENV           = config.ENVIRONMENT
            CODE_PATH     = config.CODE_BASE_PATH
        }

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
                    input message: "Approve deployment to ${ENV} environment?"
                }
            }

            stage('Ansible Playbook Execution') {
                steps {
                    dir(CODE_PATH) {
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
                    channel: SLACK_CHANNEL,
                    message: "✅ SUCCESS: ${config.ACTION_MESSAGE} on ${ENV}"
                )
            }
            failure {
                slackSend(
                    channel: SLACK_CHANNEL,
                    message: "❌ FAILED: ${config.ACTION_MESSAGE} on ${ENV}"
                )
            }
        }
    }
}
