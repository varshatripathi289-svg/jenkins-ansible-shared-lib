def call(Map config) {

    if (!config.INVENTORY || !config.PLAYBOOK) {
        error "INVENTORY and PLAYBOOK are mandatory"
    }

    // Load ansible.cfg from resources
    writeFile(
        file: 'ansible.cfg',
        text: libraryResource('ansible/ansible.cfg')
    )

    def executor = new org.opstree.ansible.AnsibleExecutor(this)
    executor.runPlaybook(config)
}
