TODO:

- beschreiben wie man ein neues XCFramework in einem Module anlegt
- Template Projekt anpassen
- rename geht noch nicht perfekt (ios files, run files, ...)

# TODO - beschreiben wie das geht

- iOS App in XCode auf dem Mac anpassen (insbesondere XCFramework einbinden bzw. entfernen)
- iOS Framework in XCode erstellen/anpassen

# XCFramework

- under `library/iosFrameworks/FeedbackManagerXCFramework` is an example framework project
- start the `tooling/scripts/build_xcframework.sh` to build a xcframework
  - ATTENTION: this script currently must be adjusted manually
- the .xcframework will be created as `library/iosFrameworks/FeedbackManagerXCFramework/FeedbackManagerXCFramework.xcframework`
- copy the created .xcframework from iOS to windows (otherwise it must be build again even if nothing changed)
