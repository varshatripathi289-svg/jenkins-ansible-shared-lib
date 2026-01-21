package org.opstree.ansible

class AnsibleExecutor implements Serializable {

    def steps

    AnsibleExecutor(steps) {
        this.steps = steps
    }

    def runPlaybook(Map cfg) {

        steps.withEnv([
            "ANSIBLE_CONFIG=${steps.pwd()}/ansible.cfg",
            "ANSIBLE_HOST_KEY_CHECKING=False"
        ]) {

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
}
