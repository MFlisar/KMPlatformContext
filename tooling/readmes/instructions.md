

# Following directories should not be changed (the may be replaced)

- tooling

TODO:

[x] demo icon
[x] tooling Ordner darf keine lokalen Einstellungen enthalten (muss durch aktuellsten Template Stand ersetzt werden können)
[x] Einstellungen in configs hinterlegen
- beschreiben wie man ein neues XCFramework in einem Module anlegt
- Template Projekt anpassen
- rename geht noch nicht perfekt (ios files, run files, ...)

# Instructions

After creating a new project following must be done:

- [ ] update the 2 `*-config.yml` files
- [ ] execute the `RenamePackageNameKt` task
  - it is save to do this multiple times with different package names in the configs because the last used names will be stored in `tooling/state.properties`

# TODO - beschreiben wie das geht

- iOS App in XCode auf dem Mac anpassen (insbesondere XCFramework einbinden bzw. entfernen)
- iOS Framework in XCode erstellen/anpassen

# Tasks (execute manually when needed)

- `MacActions`
  - can copy the project to the mac via `delete` + `copy`
  - can build the xcframework on the mac via ssh command and copy it back to windows
  - can open the app/xcframework project on the mac inside XCode
- the `UpdateProjectKt` task can be used to copy all relevant files from the `LibraryTemplate` project to the current project - via `delete` + `copy`
  - following folders/files are copied:
    - ...

# XCFramework

- under `library/iosFrameworks/FeedbackManagerXCFramework` is an example framework project
- start the `tooling/scripts/build_xcframework.sh` to build a xcframework
  - ATTENTION: this script currently must be adjusted manually
- the .xcframework will be created as `library/iosFrameworks/FeedbackManagerXCFramework/FeedbackManagerXCFramework.xcframework`
- copy the created .xcframework from iOS to windows (otherwise it must be build again even if nothing changed)

# Scripts and other tasks

- the `tooling.updateMarkdownFiles` will be run automatically on every push by a github action
- `tooling/scripts/build_on_mac.sh`... builds the project on a macOS machine (alternatively open the app in XCode and build it there)