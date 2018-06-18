require 'fastlane/action'
require_relative '../helper/generate_secrets_helper'

module Fastlane
  module Actions
    class GenerateSecretsAction < Action
      def self.run(params)
        file_name = params[:file_name].nil? ? params[:class_name] : params[:file_name]
        class_name = params[:class_name]
        f = File.open("./#{file_name}.swift", "w") { |file| 
          file.write("import Foundation\n")
          file.write("\n")
          file.write("class #{class_name} {\n")
          file.write("\n")
          file.write("}\n") 
        }
      end

      def self.description
        "Generate a file containing secret values from the environment"
      end

      def self.authors
        ["amlcurran"]
      end

      def self.return_value
        # If your method provides a return value, you can describe here what it does
      end

      def self.details
        # Optional:
        "Use this plugin to build a file which contains secret or confidential keys, which can be supplied by your CI environment to avoid leaking secrets!"
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(key: :file_name,
                                  env_name: "GENERATE_SECRETS_FILE_NAME",
                               description: "A file name to write the output to, excluding the file extension",
                                  optional: true,
                                      type: String),
          FastlaneCore::ConfigItem.new(key: :class_name,
                                  env_name: "GENERATE_SECRETS_CLASS_NAME",
                               description: "The class name which the generated secrets will be a part of",
                                  optional: false,
                                      type: String)                    
        ]
      end

      def self.is_supported?(platform)
        [:ios].include?(platform)
      end
    end
  end
end
