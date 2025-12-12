# JReleaser Migration Guide

This document describes the migration from the `net.researchgate.release` plugin to JReleaser.

## Changes Made

### 1. Root `build.gradle.kts`
- **Removed**: `id("net.researchgate.release") version "3.1.0" apply(false)`
- **Added**: `id("org.jreleaser") version "1.15.0" apply(false)`

### 2. `fasttuple-core/build.gradle.kts`

#### Plugin Changes
- **Removed**: `id("net.researchgate.release")`
- **Removed**: `import net.researchgate.release.ReleaseExtension`
- **Added**: `id("org.jreleaser")`

#### Publishing Configuration
- Changed publication name from `"mavenJava"` to `"maven"` (JReleaser convention)
- Added explicit `groupId`, `artifactId`, and `version` to publication
- Fixed SCM URLs to use proper `scm:git:` prefix
- Added `inceptionYear` and `issueManagement` to POM
- Changed repository to publish to local staging directory: `build/staging-deploy`
  - This is required by JReleaser's workflow

#### Signing Configuration
- Signing configuration remains largely the same
- Updated to sign the renamed `"maven"` publication

#### JReleaser Configuration
Added new `jreleaser` block with:
- Project metadata (name, description, authors, license)
- Git root search enabled
- Signing configuration (armored GPG signatures via command mode)
- Maven Central deployment via new Sonatype API
- Credentials from environment variables or project properties

#### Removed Configuration
- Removed `ReleaseExtension` configuration block (git branch requirements, tag signing)
  - These are now handled by `jreleaser.yml`

### 3. New `jreleaser.yml`
Created a comprehensive JReleaser configuration file with:
- Project metadata and description
- GitHub release configuration
  - Automated changelog generation using conventional commits
  - Tag and release creation on master branch
  - GPG signing of releases
- Maven Central deployment configuration
  - Uses new Sonatype Central Portal API
  - Automatic repository closing and release
  - Staging from `fasttuple-core/build/staging-deploy`

### 4. `.github/workflows/deploy.yaml`
Updated the deployment workflow:
- Updated checkout action to v4 with full git history (`fetch-depth: 0`)
- Added Gradle setup action for better caching
- Added version extraction step
- Split deployment into two phases:
  1. `./gradlew publish` - Builds and publishes to local staging directory
  2. `./gradlew jreleaserFullRelease` - Handles signing, GitHub release, and Maven Central deployment

#### New Environment Variables Required
- `JRELEASER_GITHUB_TOKEN`: GitHub token for creating releases (uses `secrets.GITHUB_TOKEN`)
- `JRELEASER_GPG_PASSPHRASE`: GPG key passphrase
- `JRELEASER_GPG_PUBLIC_KEY`: GPG public key
- `JRELEASER_GPG_SECRET_KEY`: GPG secret key
- `JRELEASER_MAVENCENTRAL_USERNAME`: Maven Central username (from `secrets.MAVEN_USERNAME`)
- `JRELEASER_MAVENCENTRAL_PASSWORD`: Maven Central password (from `secrets.MAVEN_PASSWORD`)

## Functional Equivalence

The new configuration maintains all the functionality of the old release plugin:

### Version Management
- **Old**: Managed by `net.researchgate.release` plugin
- **New**: Still managed via `gradle.properties` (version=0.4.0-SNAPSHOT)
- Version bumping can be done manually or via JReleaser commands

### Git Operations
- **Old**: `requireBranch = "master"` and `signTag = true` in ReleaseExtension
- **New**: Configured in `jreleaser.yml` under `release.github.branch` and `release.github.sign`

### Signing
- **Old**: GPG signing via `useGpgCmd()` for releases only
- **New**: Same GPG command mode, plus JReleaser handles signing of release artifacts

### Publishing
- **Old**: Direct publish to Sonatype OSS (legacy endpoints)
  - Releases: `https://oss.sonatype.org/service/local/staging/deploy/maven2`
  - Snapshots: `https://oss.sonatype.org/content/repositories/snapshots`
- **New**: Publish to local staging, then JReleaser deploys to Sonatype Central Portal
  - Uses modern API: `https://central.sonatype.com/api/v1/publisher`
  - Handles staging, closing, and releasing automatically

### Release Process
- **Old**: `./gradlew release` would version, tag, build, and publish
- **New**: 
  - Manual version update in `gradle.properties`
  - `./gradlew publish` to build and stage locally
  - `./gradlew jreleaserFullRelease` to create GitHub release and deploy to Maven Central

## Benefits of JReleaser

1. **Modern Sonatype API**: Uses the new Central Portal API instead of legacy OSSRH
2. **Automated Changelog**: Generates changelogs from conventional commits
3. **GitHub Releases**: Automatically creates GitHub releases with release notes
4. **Better Control**: Separate staging and release steps for safer deployments
5. **Multi-Platform**: JReleaser supports multiple deployment targets (not just Maven)
6. **Active Development**: JReleaser is actively maintained and updated

## Migration Checklist

- [x] Update root `build.gradle.kts` to use JReleaser plugin
- [x] Update `fasttuple-core/build.gradle.kts` to remove release plugin
- [x] Configure JReleaser in `fasttuple-core/build.gradle.kts`
- [x] Create `jreleaser.yml` configuration
- [x] Update GitHub Actions workflow
- [ ] Add GPG secrets to GitHub repository:
  - `GPG_PASSPHRASE`
  - `GPG_PUBLIC_KEY`
  - `GPG_SECRET_KEY`
- [ ] Verify `MAVEN_USERNAME` and `MAVEN_PASSWORD` secrets still exist
- [ ] Test release process with a snapshot version
- [ ] Test full release process with a release version

## Testing the Migration

### Automated Testing in GitHub Actions

We've created two workflows to test the JReleaser configuration:

#### 1. Test Release Configuration (`.github/workflows/test-release.yaml`)
This workflow runs automatically on PRs that modify release-related files. It:
- ✅ Builds and stages artifacts locally
- ✅ Verifies all required artifacts are created (JAR, sources, javadoc, POM)
- ✅ Validates POM content (metadata, licenses, developers, SCM)
- ✅ Tests JReleaser configuration without deploying
- ✅ Uploads artifacts and logs for inspection

**Trigger it manually:**
```bash
# Via GitHub UI: Actions → Test Release Configuration → Run workflow
# Or via gh CLI:
gh workflow run test-release.yaml
```

**Automatic triggers:**
- Any PR that modifies `build.gradle.kts`, `jreleaser.yml`, or workflow files

#### 2. Maven Deploy with Dry-Run Option (`.github/workflows/deploy.yaml`)
The main deployment workflow now supports dry-run mode:

**Test deployment without publishing:**
```bash
# Via GitHub UI: Actions → Maven Deploy → Run workflow → Set dry-run to "true"
# Or via gh CLI:
gh workflow run deploy.yaml -f dry-run=true
```

This will:
- Build and stage artifacts
- Run JReleaser with `-Pjreleaser.dry.run=true`
- Validate everything without actually deploying
- Upload logs for inspection

### Local Testing

#### Test Snapshot Deployment
```bash
# Ensure version ends with -SNAPSHOT in gradle.properties
./gradlew clean publish
./gradlew jreleaserFullRelease -Pjreleaser.dry.run=true
```

#### Test Release Deployment
```bash
# Update version to remove -SNAPSHOT in gradle.properties
./gradlew clean publish
./gradlew jreleaserFullRelease -Pjreleaser.dry.run=true
```

#### Verify Staged Artifacts
```bash
# After running publish, check the staging directory
ls -la fasttuple-core/build/staging-deploy/
find fasttuple-core/build/staging-deploy/ -type f
```

### Useful JReleaser Commands
```bash
# Dry run (doesn't actually deploy)
./gradlew jreleaserFullRelease -Pjreleaser.dry.run=true

# Only deploy to Maven Central (skip GitHub release)
./gradlew jreleaserDeploy

# Only create GitHub release (skip Maven Central)
./gradlew jreleaserRelease

# Generate and validate configuration
./gradlew jreleaserConfig

# Show full configuration (useful for debugging)
./gradlew jreleaserConfig --full

# Check environment variables
./gradlew jreleaserEnv
```

### Inspecting Test Results

After running the test workflow:
1. Go to Actions → Test Release Configuration → Latest run
2. Check the "Summary" section for a quick overview
3. Download artifacts:
   - `staging-artifacts` - Contains all built JARs and POMs
   - `jreleaser-logs` - Contains JReleaser configuration and logs
4. Review the step outputs for detailed validation results

## Rollback Plan

If issues arise, you can rollback by:
1. Reverting changes to `build.gradle.kts` files
2. Restoring the `net.researchgate.release` plugin
3. Reverting the GitHub Actions workflow
4. Deleting `jreleaser.yml` and `JRELEASER_MIGRATION.md`

The git history contains all previous configurations for reference.
