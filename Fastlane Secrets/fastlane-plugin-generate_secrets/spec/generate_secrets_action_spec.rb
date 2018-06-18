describe Fastlane::Actions::GenerateSecretsAction do
  describe '#run' do
    
    test_file_name = "TestBuildSecrets"
    defaultParameters = {
      file_name: test_file_name,
      key_prefix: "PREFIX_",
      class_name: "ApiKeys"
    }

    after(:each) do
      if File.exists?("#{test_file_name}.swift")
        File.delete("#{test_file_name}.swift")
      end

      if File.exists?("ApiKeys.swift")
        File.delete("ApiKeys.swift")
      end
    end

    it 'writes a file to the correct place' do
      Fastlane::Actions::GenerateSecretsAction.run(defaultParameters)

      expect(File.exists?("#{test_file_name}.swift")).to be(true)
    end

    it 'writes a file to the correct directory' do
      parameters = defaultParameters.clone
      parameters[:path] = "./Resources/"
      Fastlane::Actions::GenerateSecretsAction.run(parameters)

      expect(File.exists?("./Resources/TestBuildSecrets.swift")).to be(true)
    end

    it 'writes an empty Swift class' do
      Fastlane::Actions::GenerateSecretsAction.run({
        file_name: test_file_name,
        key_prefix: "PREFIX_",
        class_name: "BuildConfig"
      })

      expect(File.open("#{test_file_name}.swift", "r").read).to eq("import Foundation

class BuildConfig {

}
")
    end

    it 'writes an empty Swift class with the correct name' do
      Fastlane::Actions::GenerateSecretsAction.run(defaultParameters)

      expect(File.open("#{test_file_name}.swift", "r").read).to eq("import Foundation

class ApiKeys {

}
")
    end

    it 'writes an empty Swift class with the correct file if no file specified' do
      Fastlane::Actions::GenerateSecretsAction.run({
        class_name: "ApiKeys",
        key_prefix: "PREFIX_"
      })

      expect(File.exists?("ApiKeys.swift")).to be(true)
    end

    it 'writes a file with inputs from env' do
      ENV["PREFIX_apiKey"] = "12345"
      Fastlane::Actions::GenerateSecretsAction.run(defaultParameters)

      file_content = File.open("#{test_file_name}.swift", "r").read
      expect(file_content).to include("static let apiKey = \"12345\"")
    end

    it 'writes a key from env' do
      ENV["FOO_crashlyticsKey"] = "12345"
      parameters = defaultParameters.clone
      parameters[:key_prefix] = "FOO_"
      Fastlane::Actions::GenerateSecretsAction.run(parameters)

      file_content = File.open("#{test_file_name}.swift", "r").read
      expect(file_content).to include("static let crashlyticsKey = \"12345\"")
    end

    it 'writes a string value from env' do
      ENV["FOO_crashlyticsKey"] = "ABCDE"
      parameters = defaultParameters.clone
      parameters[:key_prefix] = "FOO_"
      Fastlane::Actions::GenerateSecretsAction.run(parameters)

      file_content = File.open("#{test_file_name}.swift", "r").read
      expect(file_content).to include("static let crashlyticsKey = \"ABCDE\"")
    end

    it 'does not write non-prefixed keys from env' do
      ENV["FOO_crashlyticsKey"] = "12345"
      ENV["BAR_fabricKey"] = "12345"
      parameters = defaultParameters.clone
      parameters[:key_prefix] = "FOO_"
      Fastlane::Actions::GenerateSecretsAction.run(parameters)

      file_content = File.open("#{test_file_name}.swift", "r").read
      expect(file_content).to_not include("static let fabricKey = \"12345\"")
    end

    it 'throws a descriptive error when missing key_prefix parameter' do
      parameters = defaultParameters.clone
      parameters[:key_prefix] = nil

      expect do
        Fastlane::Actions::GenerateSecretsAction.run(parameters)
      end.to raise_error("key_prefix is a required parameter for generate_secrets")
    end

    it 'throws a descriptive error when missing class_name parameter' do
      parameters = defaultParameters.clone
      parameters[:class_name] = nil

      expect do
        Fastlane::Actions::GenerateSecretsAction.run(parameters)
      end.to raise_error("class_name is a required parameter for generate_secrets")
    end

    it 'makes everything public when it is specified to do so' do
      ENV["PREFIX_apiKey"] = "12345"
      Fastlane::Actions::GenerateSecretsAction.run({
        class_name: "ApiKeys",
        key_prefix: "PREFIX_",
        public: true
      })

      expect(File.open("ApiKeys.swift", "r").read).to eq("import Foundation

public class ApiKeys {

  public static let apiKey = \"12345\"
}
")
    end

    it 'makes an extension when it is specified to do so' do
      ENV["PREFIX_apiKey"] = "12345"
      Fastlane::Actions::GenerateSecretsAction.run({
        class_name: "ApiKeys",
        key_prefix: "PREFIX_",
        public: true,
        use_extension: true
      })

      expect(File.open("ApiKeys.swift", "r").read).to eq("import Foundation

public extension ApiKeys {

  public static let apiKey = \"12345\"
}
")
    end
  end
end
