describe Fastlane::Actions::GenerateSecretsAction do
  describe '#run' do
    
    test_file_name = "TestBuildSecrets"

    after(:each) do
      File.delete("#{test_file_name}.swift")
    end

    it 'writes a file to the correct place' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name
      })

      expect(File.exists?("#{test_file_name}.swift")).to be(true)
    end
  end
end
