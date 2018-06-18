describe Fastlane::Actions::GenerateSecretsAction do
  describe '#run' do
    it 'writes a file to the correct place' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: "foobar.swift"
      })

      expect(File.exists?("foobar.swift")).to be(true)
    end
  end
end
