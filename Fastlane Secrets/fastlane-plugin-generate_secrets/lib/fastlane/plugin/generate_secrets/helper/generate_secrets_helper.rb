require 'fastlane_core/ui/ui'

module Fastlane
  UI = FastlaneCore::UI unless Fastlane.const_defined?("UI")

  module Helper
    class GenerateSecretsHelper
      # class methods that you define here become available in your action
      # as `Helper::GenerateSecretsHelper.your_method`
      #
      def self.show_message
        UI.message("Hello from the generate_secrets plugin helper!")
      end
    end
  end
end
