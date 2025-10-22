param(
  [switch]$SkipTests
)

$ErrorActionPreference = 'Stop'

$envPath = Join-Path $PSScriptRoot '..' | Join-Path -ChildPath '.env'
if (Test-Path $envPath) {
  Write-Host "Loading environment from .env" -ForegroundColor Green
  Get-Content -Path $envPath | ForEach-Object {
    $line = $_.Trim()
    if ([string]::IsNullOrWhiteSpace($line)) { return }
    if ($line.StartsWith('#')) { return }

    $eq = $line.IndexOf('=')
    if ($eq -lt 1) { return }

    $key = $line.Substring(0, $eq).Trim()
    $value = $line.Substring($eq + 1).Trim()

    # Remove optional surrounding quotes
    if ($value.StartsWith('"') -and $value.EndsWith('"')) {
      $value = $value.Substring(1, $value.Length - 2)
    }

    # Set for current process so child processes (mvnw.cmd) see it
    [System.Environment]::SetEnvironmentVariable($key, $value, 'Process')
    Set-Item -Path Env:$key -Value $value | Out-Null
  }
} else {
  Write-Warning ".env not found. You can copy .env.example to .env and set your variables."
}

# Run Spring Boot via Maven Wrapper on Windows
$mvncmd = Join-Path (Split-Path $PSScriptRoot -Parent) 'mvnw.cmd'
if (-not (Test-Path $mvncmd)) {
  throw "mvnw.cmd not found at $mvncmd"
}

$opts = @()
if ($SkipTests) { $opts += '-DskipTests' }

& $mvncmd @opts 'spring-boot:run'

