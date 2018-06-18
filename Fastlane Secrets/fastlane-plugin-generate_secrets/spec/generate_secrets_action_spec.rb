describe Fastlane::Actions::GenerateSecretsAction do
  describe '#run' do
    
    test_file_name = "TestBuildSecrets"

    after(:each) do
      if File.exists?("#{test_file_name}.swift")
        File.delete("#{test_file_name}.swift")
      end
    end

    it 'writes a file to the correct place' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name,
        class_name: "ApiKeys"
      })

      expect(File.exists?("#{test_file_name}.swift")).to be(true)
    end

    it 'writes an empty Swift class' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name,
        class_name: "BuildConfig"
      })

      expect(File.open("#{test_file_name}.swift", "r").read).to eq("import Foundation

class BuildConfig {

}
")
    end

    it 'writes an empty Swift class with the correct name' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name,
        class_name: "ApiKeys"
      })

      expect(File.open("#{test_file_name}.swift", "r").read).to eq("import Foundation

class ApiKeys {

}
")
    end

    it 'writes an empty Swift class with the correct file if no file specified' do
      Fastlane::Actions::GenerateSecretsAction.run({
        class_name: "ApiKeys"
      })

      expect(File.exists?("ApiKeys.swift")).to be(true)
    end

    it 'writes a file with inputs from env' do
      ENV["apiKey"] = "12345"
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name,
        class_name: "ApiKeys"
      })

      file_content = File.open("#{test_file_name}.swift", "r").read
      expect(file_content).to include("static let apiKey = \"12345\"")
    end
  end
end
