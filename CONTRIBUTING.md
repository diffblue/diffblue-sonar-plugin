# Contributing to Diffblue SonarQube Plugin

This guide will help you set up your development environment and walk through the testing workflow for making changes to the plugin.

## Development Prerequisites

- Java Development Kit (JDK 17 or higher)
- Maven 3.6+
- A local instance of SonarQube Community Edition
- Diffblue Cover CLI (for generating test coverage data - optional)
- A Java project with Diffblue tests present for testing

## Initial Setup

### 1. Set Up SonarQube Locally

Download and set up a local instance of **SonarQube Community Edition** from the [official SonarQube downloads page](https://www.sonarqube.org/downloads/).

### 2. Prepare a Test Project

Pick a Java project to use for testing the plugin. For convenience, you can clone the [SonarQube Example Repository](https://github.com/diffblue/SonarQube-Example-Repo), which includes pre-generated Diffblue tests and metadata.

### 3. Configure SonarQube

1. Create a new project in your local SonarQube instance.
2. Configure the **Analysis Method** to "Locally".
3. Generate a SonarQube authentication token through the UI.
4. Store this token somewhere secure for later use.

## Development Workflow

### Building the Plugin

Build the SonarQube plugin JAR file from the project root:

```bash
mvn clean package
```

This produces the plugin JAR at `sonar-plugin/target/diffblue-sonar-plugin-X.X.X-SNAPSHOT.jar`.

### Installing Your Build

1. Copy the produced JAR to the `extensions/plugins/` directory of your SonarQube installation, replacing any previous versions:

   ```bash
   cp sonar-plugin/target/diffblue-sonar-plugin-X.X.X-SNAPSHOT.jar ~/sonarqube-25.11.0.114957/extensions/plugins/
   ```
   
2. Restart SonarQube to load the new version of the plugin:

   ```bash
   # Navigate to your SonarQube installation directory
   ./bin/[your-platform]/sonar.sh restart
   ```

### Running an Analysis

Run the SonarQube analysis on your test project using the Maven command provided by SonarQube:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=Your-Project-Key \
  -Dsonar.projectName='Your Project Name' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN_HERE
```

Replace `Your-Project-Key`, `Your Project Name`, and `YOUR_TOKEN_HERE` with the appropriate values for your test project.

### Viewing Results

1. Open the SonarQube UI at `http://localhost:9000`.
2. Navigate to your project.
3. View the latest measures, including the Diffblue coverage metrics.

## Testing Changes

When making changes to the plugin, follow this iteration cycle:

1. Make your code changes.
2. Build the plugin: `mvn clean package`
3. Copy the new JAR to SonarQube's plugins directory.
4. Restart SonarQube.
5. Run the analysis on your test project.
6. Verify the changes in the SonarQube UI.
7. Repeat as needed.

## Debugging and Logs

### Analysis-Time Logs

When running the analysis locally, log messages from **DiffblueCoverageSensor** will appear in the console where you execute the `mvn sonar:sonar` command. These logs show information about:
- Detection of Diffblue coverage files
- Processing of coverage data
- Any errors or warnings during sensor execution

### Server-Side Logs

Log lines from the **DiffblueMeasureComputer** and **PercentageMeasureComputer** can be found in SonarQube's server log, as this component runs server-side after the analysis completes.

To view server logs:
- Navigate to your SonarQube installation directory
- Check `logs/sonar.log` or `logs/web.log`

## Code Style and Standards

- Follow standard Java coding conventions
- Write clear, descriptive commit messages
- Include unit tests for new functionality
- Run the code formatter before submitting changes: `mvn spotless:apply`
- Ensure all tests pass before submitting changes: `mvn clean test`

## Questions or Issues?

If you encounter problems or have questions:
- Check existing [GitHub Issues](https://github.com/diffblue/diffblue-sonar-plugin/issues)
- Open a new issue with detailed information about your problem
- Include relevant logs and SonarQube/plugin versions
- Contact Diffblue with [support@Diffblue.com](support@Diffblue.com)

Thank you for contributing to the Diffblue SonarQube Plugin!
