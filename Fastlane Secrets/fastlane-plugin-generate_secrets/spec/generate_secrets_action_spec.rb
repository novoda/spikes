describe Fastlane::Actions::GenerateSecretsAction do
  describe '#run' do
    it 'prints a message' do
      expect(Fastlane::UI).to receive(:message).with("The generate_secrets plugin is working!")

      Fastlane::Actions::GenerateSecretsAction.run(nil)
    end
  end
end
