package com.novoda.comparereports.bean

import com.fasterxml.jackson.databind.ObjectMapper
import com.novoda.comparereports.Reporter

class Compare {

    String OLD_XML = """
<checkstyle version="5.7">
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/CategoryDataRequester.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/CategoryStateHolder.java">
        <error line="53" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
    </file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/DataRequester.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/TopAppsActivity.java">
        <error line="259" severity="info" message="Line is longer than 150 characters (found 153)." source="com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck"/>
    </file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/adapter/ApplicationAdapter.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/persistance/MarshalledCursorLoader.java">
        <error line="131" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
        <error line="132" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
    </file>
</checkstyle>
"""
    String NEW_XML = """
<checkstyle version="5.7">
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/CategoryStateHolder.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/DataRequester.java">
        <error line="131" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
    </file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/FeaturedCategoryFragment.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/TopAppsActivity.java">
        <error line="259" severity="info" message="Line is longer than 150 characters (found 153)." source="com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck"/>
    </file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/appcategories/adapter/ApplicationAdapter.java"></file>
    <file name="/storage/jenkins/workspace/acme-top-apps/android/src/main/java/com/tesco/topapps/persistance/MarshalledCursorLoader.java">
        <error line="131" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
        <error line="133" severity="info" message="Comment matches to-do format 'TODO'." source="com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck"/>
    </file>
</checkstyle>
"""

    def main(ObjectMapper mapper) {
        def oldCheckstyle = mapper.readValue(OLD_XML, Checkstyle.class)
        def newCheckstyle = mapper.readValue(NEW_XML, Checkstyle.class)

        Report report = Reporter.generate(oldCheckstyle, newCheckstyle)
        println()
        println()
        println report.fixedIssues.forHumans()
        println()
        println()
        println report.introducedIssues.forHumans()
        println()
        println()
    }
}
