def call(Map args) {

    // 1️⃣ Read configuration file
    def config = readYaml file: args.configFile

    // 2️⃣ Clone the repo with credentials
    stage('Clone') {
        checkout([
            $class: 'GitSCM',
            branches: [[name: config.GIT_BRANCH]],
            userRemoteConfigs: [[
                url: config.GIT_REPO,
                credentialsId: 'github-pat'  // Jenkins credential ID for GitHub PAT
            ]]
        ])
    }

    // 3️⃣ Optional manual approval
    if (config.KEEP_APPROVAL_STAGE) {
        stage('User Approval') {
            input message: "Deploy to ${config.ENVIRONMENT}?"
        }
    }

    // 4️⃣ Execute Ansible playbook
    stage('Run Ansible') {
        def executor = new org.opstree.ansible.AnsibleExecutor(this)
        executor.runPlaybook(config.ANSIBLE)
    }

    // 5️⃣ Slack / Notification
    stage('Notify') {
        // simple Slack notification, adjust as needed
        slackSend(
            channel: config.SLACK_CHANNEL_NAME,
            message: "${config.ACTION_MESSAGE} | ENV: ${config.ENVIRONMENT}"
        )
    }
}
