require 'fastlane/action'
require 'fileutils'
require_relative '../helper/generate_secrets_helper'

module Fastlane
  module Actions
    class GenerateSecretsAction < Action
      def self.run(params)
        [:key_prefix, :class_name].each do |required_parameter|
          if params[required_parameter].nil? 
            UI.user_error!("#{required_parameter} is a required parameter for generate_secrets")
          end
        end

        file_name = params[:file_name].nil? ? params[:class_name] : params[:file_name]
        access_modifier = params[:public] ? "public " : ""
        class_name = params[:class_name]
        declaration = params[:use_extension] ? "extension" : "class"

        path = params[:path]
        directory = path ? File.expand_path(path, Dir.pwd) : Dir.pwd
        finalPath = File.join(directory, "#{file_name}.swift")
        FileUtils.mkdir_p(directory)

        File.open(finalPath, "w") { |file| 
          file.write("import Foundation\n")
          file.write("\n")
          file.write("#{access_modifier}#{declaration} #{class_name} {\n")
          file.write("\n")
          ENV.each do |key, value|
            if key.to_s.start_with?(params[:key_prefix])
              extractedKey = key.sub(params[:key_prefix], "")
              file.write("  #{access_modifier}static let #{extractedKey} = \"#{value}\"")
              file.write("\n")
            end
          end
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
                                      type: String),
          FastlaneCore::ConfigItem.new(key: :key_prefix,
                                  env_name: "GENERATE_SECRETS_KEY_PREFIX",
                                description: "The prefix that each ENV property to extract as a key starts with",
                                  optional: false,
                                      type: String),
          FastlaneCore::ConfigItem.new(key: :public,
                                  env_name: "GENERATE_SECRETS_PUBLIC",
                               description: "Whether the class and keys should be `public` accessible in Swift",
                                  optional: true,
                                      type: Boolean,
                             default_value: false),
          FastlaneCore::ConfigItem.new(key: :path,
                                  env_name: "GENERATE_SECRETS_PATH",
                               description: "The path of the file to create",
                                  optional: true,
                                      type: String,
                             default_value: "./"),
          FastlaneCore::ConfigItem.new(key: :use_extension,
                                  env_name: "GENERATE_SECRETS_USE_EXTENSION",
                               description: "Whether the declarations are a class, or an extension",
                                  optional: true,
                                      type: Boolean,
                             default_value: false)                 
        ]
      end

      def self.is_supported?(platform)
        [:ios].include?(platform)
      end
    end
  end
end
