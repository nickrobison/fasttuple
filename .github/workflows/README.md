# GitHub Actions Workflows

## Overview

This directory contains GitHub Actions workflows for building, testing, and releasing the FastTuple project.

## Workflows

### 1. Build and Test (`build.yaml`)
Runs on every push and pull request to build the project and run tests.

### 2. Test Release Configuration (`test-release.yaml`)
**Purpose**: Validate JReleaser configuration without deploying

**When it runs:**
- Automatically on PRs that modify:
  - `build.gradle.kts`
  - `fasttuple-core/build.gradle.kts`
  - `jreleaser.yml`
  - Workflow files
- Manually via workflow dispatch

**What it does:**
1. Builds the project
2. Publishes to local staging directory
3. Verifies all required artifacts exist:
   - Main JAR
   - Sources JAR
   - Javadoc JAR
   - POM file
4. Validates POM content:
   - Project metadata
   - License information
   - Developer information
   - SCM configuration
5. Tests JReleaser configuration
6. Uploads artifacts and logs for inspection

**How to run manually:**
```bash
# Via GitHub UI
Actions → Test Release Configuration → Run workflow

# Via GitHub CLI
gh workflow run test-release.yaml
```

**Artifacts produced:**
- `staging-artifacts` - All built JARs and POMs (7 days retention)
- `jreleaser-logs` - JReleaser configuration and logs (7 days retention)

### 3. Maven Deploy (`deploy.yaml`)
**Purpose**: Deploy releases to Maven Central and create GitHub releases

**When it runs:**
- After successful "Build and test" workflow on master branch
- Manually via workflow dispatch

**Dry-run mode:**
You can test the full deployment process without actually publishing:

```bash
# Via GitHub UI
Actions → Maven Deploy → Run workflow → Set dry-run to "true"

# Via GitHub CLI
gh workflow run deploy.yaml -f dry-run=true
```

**What it does:**
1. Builds the project
2. Publishes to local staging directory
3. Runs JReleaser to:
   - Sign artifacts with GPG
   - Create GitHub release with changelog
   - Deploy to Maven Central (Sonatype Central Portal)
   - Close and release staging repository

**Required secrets:**
- `MAVEN_USERNAME` - Maven Central username
- `MAVEN_PASSWORD` - Maven Central password
- `GPG_PASSPHRASE` - GPG key passphrase
- `GPG_PUBLIC_KEY` - GPG public key
- `GPG_SECRET_KEY` - GPG secret key
- `GITHUB_TOKEN` - Automatically provided by GitHub

**Artifacts produced:**
- `jreleaser-logs` - JReleaser logs for debugging (30 days retention)

### 4. Coverity (`coverity.yaml`)
Runs Coverity static analysis (if configured).

## Testing the Release Process

### Step 1: Test Configuration (Safe)
Create a PR with your changes and the `test-release.yaml` workflow will automatically run.

### Step 2: Dry-Run Deployment (Safe)
Once merged to master, manually trigger the deploy workflow in dry-run mode:
```bash
gh workflow run deploy.yaml -f dry-run=true
```

This will simulate the entire release process without actually publishing.

### Step 3: Real Deployment
If dry-run succeeds, trigger the real deployment:
```bash
# Either wait for automatic trigger after successful build
# Or manually trigger without dry-run
gh workflow run deploy.yaml -f dry-run=false
```

## Troubleshooting

### View workflow logs
```bash
# List recent workflow runs
gh run list --workflow=test-release.yaml

# View logs for a specific run
gh run view <run-id> --log
```

### Download artifacts
```bash
# List artifacts for a run
gh run view <run-id>

# Download all artifacts
gh run download <run-id>

# Download specific artifact
gh run download <run-id> -n staging-artifacts
```

### Common issues

**"Staging directory not found"**
- Check that the `publish` task completed successfully
- Verify the path in `build.gradle.kts` matches the workflow

**"POM validation failed"**
- Review the POM content in the staging artifacts
- Check the `publishing` configuration in `build.gradle.kts`

**"JReleaser configuration error"**
- Download the `jreleaser-logs` artifact
- Check `trace.log` for detailed error messages
- Verify all required environment variables are set

**"GPG signing failed"**
- Ensure GPG secrets are properly configured in GitHub
- Test GPG signing locally first
- Check that the GPG key hasn't expired

## Workflow Diagram

```
┌─────────────────┐
│   Push/PR       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Build & Test   │
└────────┬────────┘
         │
         ├──────────────────┐
         │                  │
         ▼                  ▼
┌─────────────────┐  ┌──────────────────┐
│ Test Release    │  │  Deploy (master) │
│ Configuration   │  │                  │
│ (on PR)         │  │  - Dry-run mode  │
└─────────────────┘  │  - Full release  │
                     └──────────────────┘
```

## Best Practices

1. **Always test in dry-run mode first** before doing a real release
2. **Review the staging artifacts** to ensure they contain what you expect
3. **Check the POM validation** to ensure all metadata is correct
4. **Monitor the JReleaser logs** during deployment for any warnings
5. **Verify the GitHub release** was created correctly
6. **Check Maven Central** to ensure artifacts are available (can take a few hours)

## Related Documentation

- [JReleaser Migration Guide](../../JRELEASER_MIGRATION.md)
- [JReleaser Documentation](https://jreleaser.org/guide/latest/)
- [Sonatype Central Portal](https://central.sonatype.com/)
