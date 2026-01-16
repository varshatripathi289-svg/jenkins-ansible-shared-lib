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

            steps.sh """
                echo "===== ANSIBLE EXECUTION START ====="
                ansible-playbook \
                  -i ${config.INVENTORY} \
                  ${config.PLAYBOOK}
                echo "===== ANSIBLE EXECUTION END ====="
            """
        }
    }
}
