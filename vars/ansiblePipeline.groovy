def call(Map config) {
    def pipeline = new org.company.ansible.AnsiblePipeline(this, config)
    pipeline.run()
}
