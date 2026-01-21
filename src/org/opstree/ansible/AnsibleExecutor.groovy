class AnsibleExecutor implements Serializable {

    def steps

    AnsibleExecutor(steps) {
        this.steps = steps
    }

    def runPlaybook(Map cfg) {
        steps.withCredentials([
            steps.sshUserPrivateKey(
                credentialsId: cfg.SSH_CREDENTIAL_ID,
                keyFileVariable: 'SSH_KEY'
            )
        ]) {
            steps.sh """
              ansible-playbook \
              -i ${cfg.INVENTORY} \
              ${cfg.PLAYBOOK} \
              --private-key \$SSH_KEY
            """
        }
    }
}
