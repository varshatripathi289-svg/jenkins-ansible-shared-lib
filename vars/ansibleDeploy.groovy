def call(Map args) {

    def config = readYaml file: args.configFile

    stage('Clone') {
        git url: config.GIT_REPO, branch: config.GIT_BRANCH
    }

    if (config.KEEP_APPROVAL_STAGE) {
        stage('Approval') {
            input "Deploy to ${config.ENVIRONMENT}?"
        }
    }

    stage('Run Ansible') {
        def executor = new org.opstree.ansible.AnsibleExecutor(this)
        executor.runPlaybook(config.ANSIBLE)
    }

    stage('Notify') {
        slackSend(
            channel: config.SLACK_CHANNEL_NAME,
            message: "${config.ACTION_MESSAGE} (${config.ENVIRONMENT})"
        )
    }
}
