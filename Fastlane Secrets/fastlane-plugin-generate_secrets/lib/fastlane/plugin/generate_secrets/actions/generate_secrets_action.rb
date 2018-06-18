require 'fastlane/action'
require_relative '../helper/generate_secrets_helper'

module Fastlane
  module Actions
    class GenerateSecretsAction < Action
      def self.run(params)
        file_name = params[:file_name]
        f = File.open("./#{file_name}", "w") { |file| file.write("1234") }
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
          # FastlaneCore::ConfigItem.new(key: :your_option,
          #                         env_name: "GENERATE_SECRETS_YOUR_OPTION",
          #                      description: "A description of your option",
          #                         optional: false,
          #                             type: String)
        ]
      end

      def self.is_supported?(platform)
        [:ios].include?(platform)
      end
    end
  end
end
