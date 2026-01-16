package org.opstree.ansible

class AnsibleExecutor implements Serializable {

    def steps

    AnsibleExecutor(steps) {
        this.steps = steps
    }

    def runPlaybook(Map config) {

        steps.withEnv([
            "ANSIBLE_CONFIG=${steps.pwd()}/ansible.cfg",
            "ANSIBLE_HOST_KEY_CHECKING=False"
        ]) {

            // If SSH key is provided, use it
            if (config.SSH_CREDENTIAL_ID) {

                steps.withCredentials([
                    steps.sshUserPrivateKey(
                        credentialsId: config.SSH_CREDENTIAL_ID,
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    steps.sh """
                        ansible-playbook \
                          -i ${config.INVENTORY} \
                          ${config.PLAYBOOK} \
                          --private-key \$SSH_KEY
                    """
                }

            } else {

                // Run without SSH key (fallback)
                steps.sh """
                    ansible-playbook \
                      -i ${config.INVENTORY} \
                      ${config.PLAYBOOK}
                """
            }
        }
    }
}
