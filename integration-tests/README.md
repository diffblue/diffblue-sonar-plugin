# Diffblue SonarQube Plugin - Integration Tests

This module contains integration tests for the Diffblue SonarQube Plugin. These tests verify that the plugin works correctly in a real SonarQube environment.

## What Do These Tests Do?

The integration tests use [TestContainers](https://www.testcontainers.org/) to:

1. **Start a real SonarQube instance** in Docker (SonarQube 9.9 Community Edition)
2. **Install the Diffblue SonarQube plugin** by mounting the built plugin JAR into the container
3. **Create a test project** and enable the Diffblue plugin via the SonarQube Web API
4. **Run SonarQube analysis** on example Java projects from `test-examples/`
5. **Validate metrics** via the SonarQube REST API to ensure:
   - File-level metrics are computed correctly
   - Metrics are aggregated properly at the project level
   - Both count metrics (lines covered) and percentage metrics are accurate

### Test Cases

#### `testBasicCoverage` - Mixed Diffblue and Manual Tests

Tests a project that has both Diffblue-generated tests and manually-written tests. Validates:
- `diffblue.lines.coverable` - Total coverable lines
- `diffblue.lines.covered.diffblue` - Lines covered by Diffblue tests (including overlap with manual tests)
- `diffblue.lines.covered.diffblue_only` - Lines covered exclusively by Diffblue tests
- `diffblue.percent.covered.diffblue` - Percentage of lines covered by Diffblue tests
- `diffblue.percent.covered.diffblue_only` - Percentage of lines covered only by Diffblue tests

Expected values for the test project:
- 27 coverable lines total
- 27 lines covered by Diffblue tests (100% coverage)
- 22 lines covered exclusively by Diffblue tests (81.5%)

## Prerequisites

Before running the integration tests, ensure you have:

**Docker** - Docker must be installed and running
- Download from: https://www.docker.com/get-started
- Verify with: `docker --version`

## How to Run

### Option 1: Run from the Project Root (Recommended)

Build the entire project and run all tests including integration tests:

```bash
# From the project root directory
mvn clean verify 
```

This will:
1. Build the core module
2. Build the sonar-plugin module (creates the plugin JAR)
3. Build the test-examples module
4. Run integration tests

### Option 2: Run a Specific Test

To run a specific test method:

```bash
cd integration-tests
mvn verify -Dit.test=DiffbluePluginIntegrationTest#testBasicCoverage
```

### Skip Integration Tests

To build without running integration tests:

```bash
mvn clean install -DskipITs
```

## How It Works Internally

### Test Lifecycle

1. **Before All Tests** (`@BeforeAll setUp()`)
   - Verifies the plugin JAR exists at `../sonar-plugin/target/diffblue-sonar-plugin-X.X.X-SNAPSHOT.jar`
   - TestContainers starts a SonarQube Docker container with the plugin mounted
   - Waits for SonarQube to be fully ready (health check on `/api/system/status`)
   - Verifies the Diffblue plugin is installed via `/api/plugins/installed`
2. **For Each Test**
   - Creates a test project via SonarQube Web API (`/api/projects/create`)
   - Enables the Diffblue plugin for the project (`/api/settings/set`)
   - Runs `mvn clean verify sonar:sonar` on the test project
   - Waits 5 seconds for SonarQube's Compute Engine to process results
   - Queries metrics via `/api/measures/component`
   - Validates that metrics match expected values
3. **After All Tests**
   - TestContainers automatically stops and removes the Docker container

### Key Components

- **DiffbluePluginIntegrationTest** - Main test class with test cases
- **SonarAnalysisRunner** - Helper class to execute SonarQube analysis on Maven projects
- **test-examples/** - Sample Java projects used for testing

### Why Web API Configuration?

The test creates the project and sets the toggle property via the SonarQube Web API before running analysis. This is necessary because:

- The **Sensor** (file-level metric collection) reads from analysis parameters (`-Dsonar.diffblue.toggle=true`)
- The **MeasureComputers** (project-level aggregation) run server-side and read from project settings stored in SonarQube's database
- Setting the property via Web API ensures both components can access the configuration

## Troubleshooting

### Docker Issues

**Problem:** `Could not find a valid Docker environment`

```
Solution: Ensure Docker Desktop is running
- macOS: Open Docker Desktop from Applications
- Windows: Open Docker Desktop from Start Menu
- Linux: Start Docker daemon with `sudo systemctl start docker`
```

**Problem:** `docker.sock permission denied`

```
Solution (Linux): Add your user to the docker group
sudo usermod -aG docker $USER
newgrp docker
```

### Build Issues

**Problem:** `Could not find artifact com.diffblue:sonar-plugin:diffblue-sonar-plugin:X.X.X-SNAPSHOT`

```
Solution: Build the plugin first
cd ..
mvn clean install -DskipTests
```

**Problem:** `Plugin JAR must exist at ...`

```
Solution: The plugin JAR is missing. Build it with:
mvn clean install -DskipTests
```

### Test Failures

**Problem:** Test fails with 404 errors querying metrics

```
Solution: The test now polls the SonarQube Compute Engine API to check processing status.
If processing still times out, you can increase POLLING_TIMEOUT_MS in SonarAnalysisRunner.java.
Current timeout: 60 seconds, polling interval: 500ms
```

**Problem:** Test fails with "invalid target release: 21"

```
Solution: Ensure test-examples/pom.xml uses Java 17
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

**Problem:** Container startup timeout

```
Solution: Increase TestContainers timeout or check Docker resources
- Ensure Docker has enough memory (4GB+ recommended)
- Check Docker disk space
- Increase timeout in DiffbluePluginIntegrationTest.java (line 97)
```

### Performance Issues

**Problem:** Tests are very slow

```
Causes:
- Docker container startup takes 30-40 seconds
- SonarQube initialization takes 10-15 seconds
- Analysis and compute engine processing takes 10-20 seconds
Total: ~60 seconds per test run

Tips:
- Use -Dit.test=TestName to run specific tests
- Consider reusing containers (testcontainers.reuse.enable=true)
- Ensure Docker has adequate resources allocated
```

