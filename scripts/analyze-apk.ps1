# PowerShell Script برای تحلیل APK

param(
    [string]$ApkPath = "app\build\outputs\apk\release\app-release.apk"
)

if (-not (Test-Path $ApkPath)) {
    Write-Host "❌ APK not found: $ApkPath" -ForegroundColor Red
    Write-Host "Usage: .\scripts\analyze-apk.ps1 -ApkPath [path-to-apk]"
    exit 1
}

Write-Host "📦 Analyzing APK: $ApkPath" -ForegroundColor Cyan
Write-Host ""

# Extract APK
$TempDir = New-TemporaryFile | ForEach-Object { Remove-Item $_; New-Item -ItemType Directory -Path $_ }
Expand-Archive -Path $ApkPath -DestinationPath $TempDir -Force

Write-Host "📊 APK Size Breakdown:" -ForegroundColor Yellow
Write-Host "======================"
Write-Host ""

# Total size
$TotalSize = (Get-Item $ApkPath).Length / 1MB
Write-Host "Total APK Size: $([math]::Round($TotalSize, 2)) MB"
Write-Host ""

# DEX files
Write-Host "📝 DEX Files (Code):" -ForegroundColor Green
Get-ChildItem -Path $TempDir -Filter "classes*.dex" -Recurse | 
    ForEach-Object { 
        $size = $_.Length / 1KB
        Write-Host "$([math]::Round($size, 2)) KB - $($_.Name)"
    } | Sort-Object -Descending
Write-Host ""

# Native libraries
Write-Host "🔧 Native Libraries (.so files):" -ForegroundColor Green
$LibPath = Join-Path $TempDir "lib"
if (Test-Path $LibPath) {
    Get-ChildItem -Path $LibPath -Recurse -Filter "*.so" | 
        ForEach-Object { 
            $size = $_.Length / 1KB
            Write-Host "$([math]::Round($size, 2)) KB - $($_.FullName.Replace($TempDir, ''))"
        } | Sort-Object -Descending
} else {
    Write-Host "No native libraries found"
}
Write-Host ""

# Resources
Write-Host "🎨 Resources:" -ForegroundColor Green
$ResPath = Join-Path $TempDir "res"
if (Test-Path $ResPath) {
    Get-ChildItem -Path $ResPath -Directory | 
        ForEach-Object { 
            $size = (Get-ChildItem $_.FullName -Recurse -File | Measure-Object -Property Length -Sum).Sum / 1KB
            Write-Host "$([math]::Round($size, 2)) KB - $($_.Name)"
        } | Sort-Object -Descending | Select-Object -First 10
} else {
    Write-Host "No resources found"
}
Write-Host ""

# Top 20 largest files
Write-Host "🔝 Top 20 Largest Files:" -ForegroundColor Green
Get-ChildItem -Path $TempDir -Recurse -File | 
    Select-Object FullName, @{Name="SizeKB";Expression={$_.Length / 1KB}} | 
    Sort-Object SizeKB -Descending | 
    Select-Object -First 20 | 
    ForEach-Object { 
        $relPath = $_.FullName.Replace($TempDir, '')
        Write-Host "$([math]::Round($_.SizeKB, 2)) KB - $relPath"
    }
Write-Host ""

# Cleanup
Remove-Item -Path $TempDir -Recurse -Force

Write-Host "✅ Analysis complete!" -ForegroundColor Green
