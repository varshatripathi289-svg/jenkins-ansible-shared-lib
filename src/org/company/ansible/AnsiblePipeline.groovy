package org.company.ansible

class AnsiblePipeline implements Serializable {

    def steps
    Map config

    AnsiblePipeline(steps, Map config) {
        this.steps = steps
        this.config = config
        validateConfig()
    }

    def run() {
        cloneStage()
        approvalStage()
        playbookStage()
        notificationStage()
    }

    private void cloneStage() {
        steps.stage('Clone Repository') {
            steps.echo "Cloning repository..."
            steps.git(
                url: config.GIT_REPO,
                branch: config.GIT_BRANCH
            )
        }
    }

    private void approvalStage() {
        if (config.KEEP_APPROVAL_STAGE) {
            steps.stage('User Approval') {
                steps.input(
                    message: "Approve deployment to ${config.ENVIRONMENT}?",
                    ok: 'Deploy'
                )
            }
        } else {
            steps.echo "Approval stage skipped"
        }
    }

    private void playbookStage() {
        steps.stage('Ansible Playbook Execution') {
            steps.dir(config.CODE_BASE_PATH) {
                steps.sh """
                    ansible-playbook \
                    -i ${config.INVENTORY_FILE} \
                    ${config.PLAYBOOK}
                """
            }
        }
    }

    private void notificationStage() {
        steps.stage('Notification') {
            steps.slackSend(
                channel: config.SLACK_CHANNEL_NAME,
                message: config.ACTION_MESSAGE
            )
        }
    }

    private void validateConfig() {
        def requiredKeys = [
            'SLACK_CHANNEL_NAME',
            'ENVIRONMENT',
            'CODE_BASE_PATH',
            'ACTION_MESSAGE',
            'KEEP_APPROVAL_STAGE',
            'GIT_REPO',
            'GIT_BRANCH',
            'INVENTORY_FILE',
            'PLAYBOOK'
        ]

        requiredKeys.each {
            if (!config.containsKey(it)) {
                throw new Exception("Missing required config key: ${it}")
            }
        }
    }
}
