import shutil
from pathlib import Path
from os import chdir, getcwd, getenv, path
import subprocess

# Work in project root
if getcwd().endswith('tools'):
  chdir('..')

# Attempt to read JAVA_HOME variable to find JDK.
# Throw error if unable to locate JDK
jdk_location = getenv('JAVA_HOME')

if jdk_location is None:
  raise Exception('Set JAVA_HOME environment variable before proceeding.')

# Run gradle build to build the JAR file
print('Building JAR file...')
subprocess.run('gradlew desktop:dist', shell=True)

# Remove existing dist directory
print('Removing existing "dist" directory...')
shutil.rmtree('dist', ignore_errors=True)

# Copy assets directory into the dist folder (while creating it),
# excluding "internal" folder which contains save data and settings
print("Copying assets...")
shutil.copytree('assets', 'dist', ignore=shutil.ignore_patterns('internal'))

# Copy JDK in dist/bin (while creating it)
print(f"Copying JDK from {jdk_location}...")
shutil.copytree(jdk_location, 'dist/bin/jdk')

# Copy JAR file into dist/bin
print('Copying JAR file...')
shutil.copyfile('desktop/build/libs/desktop-1.0.jar', 'dist/bin/pokelingo.jar')

# Create convenience scripts for running the application
run_command_windows = r".\bin\jdk\bin\java.exe -jar .\bin\pokelingo.jar"
file_name_windows = "run.cmd"

# Windows
with open(path.join('dist', file_name_windows), 'w') as f:
  f.write(run_command_windows)

print("Complete!")