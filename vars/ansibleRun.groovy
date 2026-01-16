def call(Map config) {

    // Validate required inputs
    if (!config.INVENTORY || !config.PLAYBOOK) {
        error "INVENTORY and PLAYBOOK are mandatory parameters"
    }

    // Copy ansible.cfg from resources to workspace
    writeFile(
        file: 'ansible.cfg',
        text: libraryResource('ansible/ansible.cfg')
    )

    def executor = new org.opstree.ansible.AnsibleExecutor(this)
    executor.runPlaybook(config)
}
